package com.puntoseguro.observation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.net.URI;
import java.net.http.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.*;

/** Contrato HTTP de las Observaciones de un alumno. */
@Testcontainers @ActiveProfiles("dev") @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class ObservationIntegrationTest {
    @Container static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:17");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry p) {
        p.add("spring.datasource.url", db::getJdbcUrl); p.add("spring.datasource.username", db::getUsername); p.add("spring.datasource.password", db::getPassword);
        p.add("puntoseguro.jwt.secret", () -> "ZGV2ZWxvcG1lbnQtb25seS1jaGFuZ2UtdGhpcy1zZWNyZXQta2V5LTAx");
    }
    @LocalServerPort int port;
    HttpClient client=HttpClient.newHttpClient(); JsonMapper json=new JsonMapper();
    String profesor, centro, estudiante; int sid;

    HttpResponse<String> call(String method,String path,String body,String token) throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/"+path)).header("Content-Type","application/json");
        if(token!=null) b.header("Authorization","Bearer "+token);
        return client.send(b.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
    }
    String login(String role) throws Exception {
        var r=call("POST","auth/login","{\"email\":\""+role+"@puntoseguro.local\",\"password\":\"puntoseguro\"}",null);
        assertEquals(200,r.statusCode(),r.body()); return json.readTree(r.body()).get("accessToken").asText();
    }
    String me(String token) throws Exception { return json.readTree(call("GET","auth/me",null,token).body()).get("id").asText(); }
    String observations() { return "students/"+sid+"/observations"; }
    String body(String texto) throws Exception { return json.writeValueAsString(java.util.Map.of("texto",texto)); }

    @BeforeEach void setUp() throws Exception {
        profesor=login("profesor"); centro=login("centro"); estudiante=login("estudiante");
        var created=call("POST","students","{\"nombre\":\"Alumno\",\"apellidos\":\"Observado\"}",profesor);
        assertEquals(201,created.statusCode(),created.body()); sid=json.readTree(created.body()).get("id").asInt();
    }

    @Test void teacherCreatesObservationAuthoredByTheTokenUserRegardlessOfBody() throws Exception {
        String spoofed="{\"texto\":\"  Se aísla en el recreo.  \",\"autor\":{\"id\":\""+me(centro)+"\",\"nombre\":\"Otro\"},\"fechaCreacion\":\"2000-01-01T00:00:00\"}";
        var created=call("POST",observations(),spoofed,profesor);
        assertEquals(201,created.statusCode(),created.body());
        JsonNode o=json.readTree(created.body());
        assertTrue(o.get("id").isNumber());
        assertEquals("Se aísla en el recreo.",o.get("texto").asText());
        assertEquals(me(profesor),o.get("autor").get("id").asText());
        assertEquals("Usuario profesor",o.get("autor").get("nombre").asText());
        assertFalse(o.get("fechaCreacion").asText().startsWith("2000"));
    }

    @Test void teacherAndCentreReadObservationsNewestFirst() throws Exception {
        assertEquals("[]",call("GET",observations(),null,profesor).body());
        for (String texto : new String[]{"Primera","Segunda","Tercera"}) assertEquals(201,call("POST",observations(),body(texto),profesor).statusCode());
        for (String token : new String[]{profesor,centro}) {
            var listed=call("GET",observations(),null,token);
            assertEquals(200,listed.statusCode(),listed.body());
            JsonNode list=json.readTree(listed.body());
            assertEquals(3,list.size());
            assertEquals("Tercera",list.get(0).get("texto").asText());
            assertEquals("Segunda",list.get(1).get("texto").asText());
            assertEquals("Primera",list.get(2).get("texto").asText());
            assertEquals("Usuario profesor",list.get(0).get("autor").get("nombre").asText());
            assertTrue(list.get(0).hasNonNull("fechaCreacion"));
        }
    }

    @Test void onlyTeachersCreateAndStudentsCannotReachObservations() throws Exception {
        var centreCreates=call("POST",observations(),body("Desde el centro"),centro);
        assertEquals(403,centreCreates.statusCode());
        assertEquals("ACCESS_DENIED",json.readTree(centreCreates.body()).get("code").asText());
        assertEquals(403,call("POST",observations(),body("Desde un alumno"),estudiante).statusCode());
        assertEquals(403,call("GET",observations(),null,estudiante).statusCode());
        assertEquals(401,call("POST",observations(),body("Sin sesión"),null).statusCode());
        assertEquals(401,call("GET",observations(),null,null).statusCode());
        assertEquals("[]",call("GET",observations(),null,profesor).body());
    }

    @Test void rejectsEmptyBlankOrTooLongText() throws Exception {
        for (String invalid : new String[]{"{}","{\"texto\":\"\"}",body("   \n\t "),body("a".repeat(2001))}) {
            var r=call("POST",observations(),invalid,profesor);
            assertEquals(400,r.statusCode(),invalid);
            JsonNode problem=json.readTree(r.body());
            assertEquals("VALIDATION_ERROR",problem.get("code").asText());
            assertEquals("texto",problem.get("fields").get(0).asText());
        }
        assertEquals(201,call("POST",observations(),body("  "+"a".repeat(2000)+"  "),profesor).statusCode());
        assertEquals(1,json.readTree(call("GET",observations(),null,profesor).body()).size());
    }

    @Test void missingStudentIsNotFoundWhenCreatingOrReading() throws Exception {
        assertEquals(404,call("POST","students/999999/observations",body("Texto"),profesor).statusCode());
        assertEquals(404,call("GET","students/999999/observations",null,profesor).statusCode());
        assertEquals(404,call("GET","students/999999/observations",null,centro).statusCode());
    }
}

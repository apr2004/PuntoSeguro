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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.puntoseguro.security.JwtService;
import com.puntoseguro.entity.enums.UserRole;
import java.util.UUID;

/** Contrato HTTP de las Observaciones de un alumno. */
@Testcontainers @ActiveProfiles("dev") @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class ObservationIntegrationTest {
    @Container static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:17");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry p) {
        p.add("spring.datasource.url", db::getJdbcUrl); p.add("spring.datasource.username", db::getUsername); p.add("spring.datasource.password", db::getPassword);
        p.add("puntoseguro.jwt.secret", () -> "ZGV2ZWxvcG1lbnQtb25seS1jaGFuZ2UtdGhpcy1zZWNyZXQta2V5LTAx");
    }
    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    @Autowired JwtService jwt;
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
        var created=call("POST","students","{\"nombre\":\"Alumno\",\"apellidos\":\"Observado\"}",centro);
        assertEquals(201,created.statusCode(),created.body()); sid=json.readTree(created.body()).get("id").asInt();
        assertEquals(204,call("PUT","students/"+sid+"/teachers/"+me(profesor),null,centro).statusCode());
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

    @Test void editingObservationPreservesCreationAndExposesImmutableHistory() throws Exception {
        var created=call("POST",observations(),body("Texto inicial"),profesor);
        assertEquals(201,created.statusCode(),created.body());
        JsonNode original=json.readTree(created.body());
        int id=original.get("id").asInt();
        String originalAuthor=original.get("autor").get("id").asText();
        String originalDate=original.get("fechaCreacion").asText();

        var edited=call("PUT",observations()+"/"+id,body("Texto corregido"),centro);
        assertEquals(200,edited.statusCode(),edited.body());
        JsonNode current=json.readTree(edited.body());
        assertEquals("Texto corregido",current.get("texto").asText());
        assertEquals(originalAuthor,current.get("autor").get("id").asText());
        assertEquals(originalDate,current.get("fechaCreacion").asText());

        var history=call("GET",observations()+"/"+id+"/history",null,centro);
        assertEquals(200,history.statusCode(),history.body());
        JsonNode versions=json.readTree(history.body());
        assertEquals(2,versions.size());
        assertEquals("Texto inicial",versions.get(0).get("texto").asText());
        assertEquals(originalAuthor,versions.get(0).get("autor").get("id").asText());
        assertEquals("Texto corregido",versions.get(1).get("texto").asText());
        assertEquals(me(centro),versions.get(1).get("autor").get("id").asText());
        assertTrue(versions.get(1).get("fechaEdicion").asText().compareTo(versions.get(0).get("fechaEdicion").asText()) >= 0);
        assertFalse(versions.get(0).get("importado").asBoolean());
        assertFalse(versions.get(1).get("importado").asBoolean());

        jdbc.update("UPDATE observacion_historial SET importado = TRUE WHERE id = ?", versions.get(0).get("id").asInt());
        JsonNode imported=json.readTree(call("GET",observations()+"/"+id+"/history",null,centro).body());
        assertTrue(imported.get(0).get("importado").asBoolean());
        assertFalse(imported.get(1).get("importado").asBoolean());

        assertEquals(403,call("PUT",observations()+"/"+id,body("Edición no permitida"),estudiante).statusCode());
        assertEquals(403,call("GET",observations()+"/"+id+"/history",null,estudiante).statusCode());
    }

    @Test void editingMissingObservationIsNotFound() throws Exception {
        assertEquals(404,call("PUT",observations()+"/999999",body("Texto"),profesor).statusCode());
        assertEquals(404,call("GET",observations()+"/999999/history",null,profesor).statusCode());
    }

    @Test void onlyCentreCanAssignAndRevocationImmediatelyBlocksTeacher() throws Exception {
        String path="students/"+sid+"/teachers/"+me(profesor);
        assertEquals(403,call("GET","teachers",null,profesor).statusCode());
        assertEquals(403,call("GET","students/"+sid+"/teachers",null,profesor).statusCode());
        assertEquals(200,call("GET","teachers",null,centro).statusCode());
        assertEquals(me(profesor),json.readTree(call("GET","students/"+sid+"/teachers",null,centro).body()).get(0).get("id").asText());
        assertEquals(404,call("PUT","students/"+sid+"/teachers/"+UUID.randomUUID(),null,centro).statusCode());
        assertEquals(404,call("PUT","students/"+sid+"/teachers/"+me(estudiante),null,centro).statusCode());
        assertEquals(403,call("DELETE",path,null,profesor).statusCode());
        assertEquals(403,call("PUT",path,null,estudiante).statusCode());
        assertEquals(204,call("PUT",path,null,centro).statusCode());
        assertEquals(201,call("POST",observations(),body("Antes de revocar"),profesor).statusCode());
        assertEquals(204,call("DELETE",path,null,centro).statusCode());
        assertEquals(204,call("DELETE",path,null,centro).statusCode());
        assertEquals(403,call("GET",observations(),null,profesor).statusCode());
        assertEquals(403,call("POST",observations(),body("Después"),profesor).statusCode());
        assertEquals(200,call("GET",observations(),null,centro).statusCode());
        assertEquals(204,call("PUT",path,null,centro).statusCode());
        assertEquals(200,call("GET",observations(),null,profesor).statusCode());
    }

    @Test void academicDataAccessFollowsRolesAndLinks() throws Exception {
        String studentId=me(estudiante);
        String link="students/"+sid+"/links/"+studentId;
        assertEquals(0,json.readTree(call("GET","students",null,estudiante).body()).size());
        assertEquals(403,call("GET","students/"+sid,null,estudiante).statusCode());
        assertEquals(403,call("PUT",link,null,profesor).statusCode());
        assertEquals(204,call("PUT",link,null,centro).statusCode());
        assertEquals(200,call("GET","students/"+sid,null,estudiante).statusCode());
        assertEquals(200,call("GET","students/"+sid+"/grades",null,estudiante).statusCode());
        assertEquals(200,call("GET","students/"+sid+"/attendance",null,estudiante).statusCode());
        assertEquals(1,json.readTree(call("GET","students",null,estudiante).body()).size());
        assertEquals(403,call("PUT","students/"+sid,"{\"nombre\":\"Cambio\",\"apellidos\":\"Prueba\"}",estudiante).statusCode());
        assertEquals(403,call("POST","students","{\"nombre\":\"Nuevo\",\"apellidos\":\"Alumno\"}",profesor).statusCode());
        assertEquals(200,call("PUT","students/"+sid,"{\"nombre\":\"Cambio\",\"apellidos\":\"Prueba\"}",profesor).statusCode());
        assertEquals(204,call("DELETE",link,null,centro).statusCode());
        assertEquals(403,call("GET","students/"+sid,null,estudiante).statusCode());
        assertEquals(204,call("DELETE","students/"+sid+"/teachers/"+me(profesor),null,centro).statusCode());
        assertEquals(403,call("GET","students/"+sid+"/grades",null,profesor).statusCode());
        assertEquals(200,call("GET","students/"+sid+"/grades",null,centro).statusCode());
    }

    @Test void centreAssignsGuardianRoleAndRevokesOldSessionAndLinks() throws Exception {
        String uid=me(estudiante);
        String role="users/"+uid+"/role";
        assertEquals(403,call("PUT",role,"{\"role\":\"TUTOR_LEGAL\"}",profesor).statusCode());
        assertEquals(204,call("PUT",role,"{\"role\":\"TUTOR_LEGAL\"}",centro).statusCode());
        assertEquals(401,call("GET","students",null,estudiante).statusCode());
        String tutor=jwt.issue(UUID.fromString(uid),UserRole.TUTOR_LEGAL);
        assertEquals(403,call("GET","students/"+sid,null,tutor).statusCode());
        assertEquals(204,call("PUT","students/"+sid+"/links/"+uid,null,centro).statusCode());
        assertEquals(200,call("GET","students/"+sid+"/attendance",null,tutor).statusCode());
        assertEquals(403,call("POST","students/"+sid+"/grades","{}",tutor).statusCode());
        assertEquals(403,call("GET",observations(),null,tutor).statusCode());
        assertEquals(204,call("PUT",role,"{\"role\":\"ESTUDIANTE\"}",centro).statusCode());
        assertEquals(401,call("GET","students",null,estudiante).statusCode());
        assertEquals(403,call("GET","students/"+sid,null,jwt.issue(UUID.fromString(uid),UserRole.ESTUDIANTE)).statusCode());
    }

    @Test void unassignedTeacherCannotReadCreateOrEditAndOnlyAuthorCanEdit() throws Exception {
        UUID otherId=UUID.randomUUID();
        jdbc.update("INSERT INTO usuario(id,nombre,email,password_hash,role,activo) SELECT ?, 'Otro docente', ?, password_hash, 'PROFESOR', true FROM usuario WHERE id=?::uuid",
                otherId,"otro-"+otherId+"@puntoseguro.local",UUID.fromString(me(profesor)));
        String other=jwt.issue(otherId,UserRole.PROFESOR);
        String assignment="students/"+sid+"/teachers/"+otherId;
        var made=call("POST",observations(),body("Original"),profesor);
        assertEquals(201,made.statusCode(),made.body());
        JsonNode original=json.readTree(made.body());
        String edit=observations()+"/"+original.get("id").asInt();
        assertEquals(403,call("GET",observations(),null,other).statusCode());
        assertEquals(403,call("POST",observations(),body("Ajena"),other).statusCode());
        assertEquals(403,call("PUT",edit,body("Ajena"),other).statusCode());
        assertEquals(204,call("PUT",assignment,null,centro).statusCode());
        assertEquals(200,call("GET",observations(),null,other).statusCode());
        assertEquals(403,call("PUT",edit,body("Ajena"),other).statusCode());
        var changed=call("PUT",edit,body("Corregida"),profesor);
        assertEquals(200,changed.statusCode(),changed.body());
        JsonNode correction=json.readTree(changed.body());
        assertEquals(original.get("autor").get("id").asText(),correction.get("autor").get("id").asText());
        assertEquals(original.get("fechaCreacion").asText(),correction.get("fechaCreacion").asText());
        assertEquals("Corregida",correction.get("texto").asText());
        assertEquals(200,call("PUT",edit,body("Corregida por el centro"),centro).statusCode());
        assertEquals(404,call("PUT",observations()+"/999999",body("No existe"),centro).statusCode());
        assertEquals(400,call("PUT",edit,body(" "),centro).statusCode());
    }
}

package com.puntoseguro.auth;

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
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers @ActiveProfiles("dev") @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest {
    @Container static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:17");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry p) {
        p.add("spring.datasource.url", db::getJdbcUrl); p.add("spring.datasource.username", db::getUsername); p.add("spring.datasource.password", db::getPassword);
        p.add("puntoseguro.jwt.secret", () -> "ZGV2ZWxvcG1lbnQtb25seS1jaGFuZ2UtdGhpcy1zZWNyZXQta2V5LTAx");
    }
    @LocalServerPort int port;
    HttpClient client=HttpClient.newHttpClient(); JsonMapper json=new JsonMapper();
    HttpResponse<String> call(String method,String path,String body,String token) throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/auth/"+path)).header("Content-Type","application/json");
        if(token!=null) b.header("Authorization","Bearer "+token);
        return client.send(b.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
    }
    @Test void loginAndAuthorizationForAllRoles() throws Exception {
        assertEquals(401,call("GET","me",null,null).statusCode());
        assertEquals(401,call("GET","me",null,"invalid").statusCode());
        assertEquals(401,call("POST","login","{\"email\":\"estudiante@puntoseguro.local\",\"password\":\"incorrecta\"}",null).statusCode());
        for (String role : new String[]{"estudiante","profesor","centro"}) {
            var login=call("POST","login","{\"email\":\""+role+"@puntoseguro.local\",\"password\":\"puntoseguro\"}",null);
            assertEquals(200,login.statusCode(),login.body());
            String token=json.readTree(login.body()).get("accessToken").asText();
            var me=call("GET","me",null,token); assertEquals(200,me.statusCode()); assertEquals(role.toUpperCase(),json.readTree(me.body()).get("role").asText());
        }
    }
    @Test void registrationCreatesOnlyStudentsAndRejectsDuplicateEmail() throws Exception {
        String body="{\"nombre\":\"Prueba\",\"email\":\"nuevo@example.com\",\"password\":\"puntoseguro\",\"role\":\"CENTRO\"}";
        var result=call("POST","register",body,null); assertEquals(201,result.statusCode(),result.body());
        assertEquals("ESTUDIANTE",json.readTree(result.body()).get("role").asText());
        assertEquals(409,call("POST","register",body,null).statusCode());
        assertEquals(400,call("POST","register","{}",null).statusCode());
    }

    HttpResponse<String> data(String method,String path,String body,String token) throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/"+path)).header("Content-Type","application/json");
        if(token!=null) b.header("Authorization","Bearer "+token);
        return client.send(b.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
    }
    @Test void studentRecordsCrudAndFailedUpdatePreservesData() throws Exception {
        assertEquals(401,data("GET","students",null,null).statusCode());
        var login=call("POST","login","{\"email\":\"profesor@puntoseguro.local\",\"password\":\"puntoseguro\"}",null);
        String token=json.readTree(login.body()).get("accessToken").asText();
        var created=data("POST","students","{\"nombre\":\"Test\",\"apellidos\":\"Alumno\",\"curso\":\"1 ESO\",\"grupo\":\"A\"}",token);
        assertEquals(201,created.statusCode(),created.body()); int sid=json.readTree(created.body()).get("id").asInt();
        String student="students/"+sid;
        assertEquals(200,data("GET",student,null,token).statusCode());
        assertEquals(200,data("PUT",student,"{\"nombre\":\"Test editado\",\"apellidos\":\"Alumno\"}",token).statusCode());
        int subject=json.readTree(data("GET","catalogs/subjects",null,token).body()).get(0).get("id").asInt();
        int platform=json.readTree(data("GET","catalogs/platforms",null,token).body()).get(0).get("id").asInt();
        int type=json.readTree(data("GET","catalogs/attendance-types",null,token).body()).get(0).get("id").asInt();
        String grade="{\"asignaturaId\":"+subject+",\"plataformaId\":"+platform+",\"nota\":8,\"evaluacion\":\"Primera\",\"fecha\":\"2026-10-15\"}";
        var madeGrade=data("POST",student+"/grades",grade,token); assertEquals(201,madeGrade.statusCode(),madeGrade.body());
        int gid=json.readTree(madeGrade.body()).get("id").asInt(); String gradePath=student+"/grades/"+gid;
        assertEquals(1,json.readTree(data("GET",student+"/grades",null,token).body()).size());
        assertEquals(200,data("PUT",gradePath,grade.replace("8,", "9,"),token).statusCode());
        assertEquals(400,data("PUT",gradePath,grade.replace("8,", "11,"),token).statusCode());
        assertEquals(9,json.readTree(data("GET",gradePath,null,token).body()).get("nota").asInt());
        assertEquals(404,data("GET","students/999999/grades/"+gid,null,token).statusCode());
        String att="{\"tipoId\":"+type+",\"plataformaId\":"+platform+",\"dia\":\"2026-10-15\"}";
        var madeAtt=data("POST",student+"/attendance",att,token); assertEquals(201,madeAtt.statusCode(),madeAtt.body()); int aid=json.readTree(madeAtt.body()).get("id").asInt(); String attPath=student+"/attendance/"+aid;
        assertEquals(200,data("PUT",attPath,att.replace("2026-10-15","2026-10-16"),token).statusCode());
        assertEquals("2026-10-16",json.readTree(data("GET",attPath,null,token).body()).get("dia").asText());
        assertEquals(409,data("DELETE",student,null,token).statusCode());
        assertEquals(204,data("DELETE",gradePath,null,token).statusCode()); assertEquals(204,data("DELETE",attPath,null,token).statusCode());
        assertEquals(204,data("DELETE",student,null,token).statusCode()); assertEquals(404,data("GET",student,null,token).statusCode());
    }
}

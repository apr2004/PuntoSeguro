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
}

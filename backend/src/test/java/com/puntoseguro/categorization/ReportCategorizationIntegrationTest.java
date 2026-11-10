package com.puntoseguro.categorization;

import com.puntoseguro.llm.LlmClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.net.URI;
import java.net.http.*;
import java.util.ArrayDeque;
import java.util.Deque;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.*;

/** Contrato HTTP de la Categorización de Reportes, con un LLM falso. */
@Testcontainers @ActiveProfiles("dev") @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportCategorizationIntegrationTest {
    @Container static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:17");
    @DynamicPropertySource static void properties(DynamicPropertyRegistry p) {
        p.add("spring.datasource.url", db::getJdbcUrl); p.add("spring.datasource.username", db::getUsername); p.add("spring.datasource.password", db::getPassword);
        p.add("puntoseguro.jwt.secret", () -> "ZGV2ZWxvcG1lbnQtb25seS1jaGFuZ2UtdGhpcy1zZWNyZXQta2V5LTAx");
    }

    /** Respuestas preparadas del LLM; con la cola vacía, el LLM "no responde". */
    static final Deque<String> llmAnswers = new ArrayDeque<>();
    @TestConfiguration static class FakeLlmConfig {
        @Bean @Primary LlmClient fakeLlm() { return (system, message, schema) -> {
            if (llmAnswers.isEmpty()) throw new IllegalStateException("el proveedor no responde");
            return llmAnswers.pop();
        }; }
    }

    @LocalServerPort int port;
    HttpClient client=HttpClient.newHttpClient(); JsonMapper json=new JsonMapper();
    @BeforeEach void reset() { llmAnswers.clear(); }

    HttpResponse<String> call(String method,String path,String body,String token) throws Exception {
        var b=HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/api/"+path)).header("Content-Type","application/json");
        if(token!=null) b.header("Authorization","Bearer "+token);
        return client.send(b.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
    }
    String login() throws Exception {
        var login=call("POST","auth/login","{\"email\":\"estudiante@puntoseguro.local\",\"password\":\"puntoseguro\"}",null);
        return json.readTree(login.body()).get("accessToken").asText();
    }
    static final String REPORT="{\"conversacion\":[{\"rol\":\"ALUMNO\",\"texto\":\"A mi amiga Lucía la insultan en clase todos los días.\"}]}";

    @Test void requiresSession() throws Exception {
        assertEquals(401,call("POST","reports/categorization",REPORT,null).statusCode());
    }

    @Test void returnsCategorizationOfReport() throws Exception {
        llmAnswers.add("{\"esAcoso\":\"SI\",\"tipoAcoso\":\"VERBAL\",\"relacionInformante\":\"TESTIGO\",\"reiteracion\":\"SI\",\"nivelUrgencia\":\"PREOCUPANTE\",\"resumen\":\"Un alumno cuenta que a Lucía la insultan en clase.\",\"indicios\":[\"la insultan en clase todos los días\"]}");
        var response=call("POST","reports/categorization",REPORT,login());
        assertEquals(200,response.statusCode(),response.body());
        var body=json.readTree(response.body());
        assertEquals("SI",body.get("esAcoso").asText()); assertEquals("TESTIGO",body.get("relacionInformante").asText());
        assertEquals("PREOCUPANTE",body.get("nivelUrgencia").asText()); assertEquals("la insultan en clase todos los días",body.get("indicios").get(0).asText());
    }

    @Test void rejectsInvalidConversation() throws Exception {
        String token=login();
        assertEquals(400,call("POST","reports/categorization","{\"conversacion\":[]}",token).statusCode());
        assertEquals(400,call("POST","reports/categorization","{\"conversacion\":[{\"texto\":\"Hola\"}]}",token).statusCode());
        for (String body : new String[]{"{\"conversacion\":[{\"rol\":\"PROFESOR\",\"texto\":\"Me quiero morir\"}]}", "{\"conversacion\":[{\"rol\":\"ALUMNO\",\"texto\":\"Me quiero morir\""}) {
            var response=call("POST","reports/categorization",body,token);
            assertEquals(400,response.statusCode(),response.body());
            assertEquals("VALIDATION_ERROR",json.readTree(response.body()).get("code").asText());
            assertFalse(response.body().contains("Me quiero morir"),"un 400 nunca devuelve el relato");
        }
    }

    @Test void llmUnavailableWithoutRiskReturns503WithStableCode() throws Exception {
        var response=call("POST","reports/categorization",REPORT,login());
        assertEquals(503,response.statusCode(),response.body());
        assertEquals("CATEGORIZATION_UNAVAILABLE",json.readTree(response.body()).get("code").asText());
    }
}

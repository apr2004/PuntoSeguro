package com.puntoseguro.report;

import com.puntoseguro.llm.LlmClient;
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
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/** Contrato HTTP de chat, persistencia y alertas de HU05 con datos ficticios. */
@Testcontainers
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry p) {
        p.add("spring.datasource.url", db::getJdbcUrl);
        p.add("spring.datasource.username", db::getUsername);
        p.add("spring.datasource.password", db::getPassword);
        p.add("puntoseguro.jwt.secret", () -> "ZGV2ZWxvcG1lbnQtb25seS1jaGFuZ2UtdGhpcy1zZWNyZXQta2V5LTAx");
    }

    @TestConfiguration
    static class FakeLlmConfiguration {
        @Bean
        @Primary
        LlmClient fakeLlm() {
            return (system, message, schema) -> system.contains("Formato de respuesta para la API")
                    ? "{\"respuesta\":\"Puedes acompañar a la persona afectada y contárselo a un adulto de confianza.\",\"puedeContinuar\":true}"
                    : "{\"esAcoso\":\"SI\",\"tipoAcoso\":\"FISICO\",\"relacionInformante\":\"TESTIGO\",\"reiteracion\":\"SI\",\"nivelUrgencia\":\"PREOCUPANTE\",\"resumen\":\"Un alumno cuenta que a Hugo le empujan en el recreo.\",\"indicios\":[\"a mi compañero Hugo le empujan\"]}";
        }
    }

    @LocalServerPort int port;
    final HttpClient client = HttpClient.newHttpClient();
    final JsonMapper json = new JsonMapper();

    HttpResponse<String> call(String method, String path, String body, String token) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/" + path))
                .header("Content-Type", "application/json");
        if (token != null) builder.header("Authorization", "Bearer " + token);
        var request = builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    String login(String role) throws Exception {
        var response = call("POST", "auth/login", "{\"email\":\"" + role + "@puntoseguro.local\",\"password\":\"puntoseguro\"}", null);
        assertEquals(200, response.statusCode(), response.body());
        return json.readTree(response.body()).get("accessToken").asText();
    }

    @Test
    void witnessCanChatSubmitReportAndCentreReceivesPendingAlert() throws Exception {
        String student = login("estudiante");
        String teacher = login("profesor");
        String conversation = "{\"conversacion\":[{\"rol\":\"ALUMNO\",\"texto\":\"A mi compañero Hugo le empujan en el recreo casi todos los días.\"}]}";

        var chat = call("POST", "reports/chat", conversation, student);
        assertEquals(200, chat.statusCode(), chat.body());
        assertTrue(json.readTree(chat.body()).get("respuesta").asText().contains("persona afectada"));

        var created = call("POST", "reports", conversation, student);
        assertEquals(201, created.statusCode(), created.body());
        JsonNode report = json.readTree(created.body());
        int reportId = report.get("id").asInt();
        assertEquals("PENDIENTE_REVISION", report.get("estadoRevision").asText());
        assertEquals("TESTIGO", report.get("categorizacion").get("relacionInformante").asText());
        assertEquals(1, report.get("conversacion").size());

        var alerts = call("GET", "alerts", null, teacher);
        assertEquals(200, alerts.statusCode(), alerts.body());
        JsonNode alert = json.readTree(alerts.body()).get(0);
        assertEquals(reportId, alert.get("reportId").asInt());
        assertEquals("PENDIENTE_REVISION", alert.get("estado").asText());
        assertEquals("PREOCUPANTE", alert.get("nivelUrgencia").asText());

        var studentAlerts = call("GET", "alerts", null, student);
        assertEquals(403, studentAlerts.statusCode(), studentAlerts.body());
        var centreReport = call("GET", "reports/" + reportId, null, teacher);
        assertEquals(200, centreReport.statusCode(), centreReport.body());
    }

    @Test
    void onlyStudentsCanSubmitPersistedReports() throws Exception {
        String teacher = login("profesor");
        assertEquals(403, call("POST", "reports", "{\"conversacion\":[{\"rol\":\"ALUMNO\",\"texto\":\"Texto ficticio\"}]}", teacher).statusCode());
    }
}

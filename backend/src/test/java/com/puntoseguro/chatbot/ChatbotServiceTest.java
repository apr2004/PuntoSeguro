package com.puntoseguro.chatbot;

import com.puntoseguro.dto.ChatResponse;
import com.puntoseguro.dto.ReportMessage;
import com.puntoseguro.entity.enums.MessageRole;
import com.puntoseguro.llm.LlmClient;
import com.puntoseguro.service.ChatbotService;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChatbotServiceTest {
    @Test
    void answersAReportAboutAnotherPersonWithoutAssumingTheInformantIsAffected() {
        LlmClient fake = (system, message, schema) -> {
            assertTrue(system.contains("No des por hecho que le pasa a él o a ella"));
            assertTrue(message.contains("A mi compañero Hugo le empujan"));
            assertEquals(Map.of("type", "string"), ((Map<?, ?>) schema.get("properties")).get("respuesta"));
            return "{\"respuesta\":\"Has hecho bien en contarlo. Puedes acompañar a Hugo y avisar a un adulto de confianza.\",\"puedeContinuar\":true}";
        };
        ChatbotService service = new ChatbotService(fake, new JsonMapper(),
                Validation.buildDefaultValidatorFactory().getValidator());

        ChatResponse response = service.reply(List.of(
                new ReportMessage(MessageRole.ALUMNO, "A mi compañero Hugo le empujan.")));

        assertEquals("Has hecho bien en contarlo. Puedes acompañar a Hugo y avisar a un adulto de confianza.", response.getRespuesta());
        assertTrue(response.getPuedeContinuar());
    }
}

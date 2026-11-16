package com.puntoseguro.service;

import com.puntoseguro.dto.ChatResponse;
import com.puntoseguro.dto.ReportMessage;
import com.puntoseguro.entity.enums.MessageRole;
import com.puntoseguro.exception.ChatbotUnavailableException;
import com.puntoseguro.llm.LlmClient;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Genera orientación conversacional y mantiene al Informante sin etiquetas ni suposiciones. */
@Service
@RequiredArgsConstructor
public class ChatbotService {
    private static final int ATTEMPTS = 2;
    private static final Map<String, Object> SCHEMA = schema();

    private final LlmClient llm;
    private final JsonMapper json;
    private final Validator validator;

    public ChatResponse reply(List<ReportMessage> conversation) {
        RuntimeException last = null;
        String system = ReportCategorizationService.prompt("chatbot");
        String message = "Conversación completa:\n" + transcript(conversation)
                + "\n\nResponde al último mensaje del alumno sin llamarle víctima ni asumir que el relato le ocurre a él o a ella.";
        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            try {
                ChatResponse response = json.readValue(llm.generate(system, message, SCHEMA), ChatResponse.class);
                if (validator.validate(response).isEmpty()) return response;
                last = new IllegalStateException("Respuesta del chatbot incompleta");
            } catch (RuntimeException exception) {
                last = exception;
            }
        }
        throw new ChatbotUnavailableException("El chatbot no ha podido responder.", last);
    }

    private static String transcript(List<ReportMessage> conversation) {
        return conversation.stream()
                .map(m -> (m.getRol() == MessageRole.ALUMNO ? "Alumno: " : "Asistente: ") + m.getTexto())
                .collect(Collectors.joining("\n"));
    }

    private static Map<String, Object> schema() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("respuesta", Map.of("type", "string"));
        properties.put("puedeContinuar", Map.of("type", "boolean"));
        return Map.of("type", "object", "properties", properties,
                "required", List.copyOf(properties.keySet()), "additionalProperties", false);
    }
}

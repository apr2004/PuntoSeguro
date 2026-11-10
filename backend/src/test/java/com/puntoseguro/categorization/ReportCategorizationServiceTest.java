package com.puntoseguro.categorization;

import com.puntoseguro.dto.CategorizationResponse;
import com.puntoseguro.dto.ReportMessage;
import com.puntoseguro.entity.enums.*;
import com.puntoseguro.exception.CategorizationUnavailableException;
import com.puntoseguro.llm.LlmClient;
import com.puntoseguro.service.ImminentRiskDetector;
import com.puntoseguro.service.ReportCategorizationService;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Punto de prueba S1: la Categorización con un LLM falso (rápido, sin red ni clave; se ejecuta en CI). */
class ReportCategorizationServiceTest {

    /** Devuelve, en orden, las respuestas preparadas. Una excepción se lanza en lugar de devolverse. */
    static class FakeLlm implements LlmClient {
        final Deque<Object> answers; int calls; final List<String> systemPrompts = new ArrayList<>(), messages = new ArrayList<>(); final List<Map<String, Object>> schemas = new ArrayList<>();
        FakeLlm(Object... answers) { this.answers = new ArrayDeque<>(List.of(answers)); }
        @Override public String generate(String systemPrompt, String userMessage, Map<String, Object> jsonSchema) {
            calls++; systemPrompts.add(systemPrompt); messages.add(userMessage); schemas.add(jsonSchema);
            Object answer = answers.pop();
            if (answer instanceof RuntimeException e) throw e;
            return (String) answer;
        }
    }

    static String llmAnswer(String urgency, String... evidence) {
        String indicios = String.join(",", Arrays.stream(evidence).map(e -> "\"" + e + "\"").toList());
        return "{\"esAcoso\":\"SI\",\"tipoAcoso\":\"VERBAL\",\"relacionInformante\":\"TESTIGO\",\"reiteracion\":\"SI\",\"nivelUrgencia\":\"" + urgency
                + "\",\"resumen\":\"Un alumno cuenta que a Lucía la insultan en clase todos los días.\",\"indicios\":[" + indicios + "]}";
    }

    static List<ReportMessage> student(String... texts) {
        return Arrays.stream(texts).map(t -> new ReportMessage(MessageRole.ALUMNO, t)).toList();
    }

    static ReportCategorizationService service(LlmClient llm) { return new ReportCategorizationService(llm, new ImminentRiskDetector(), new JsonMapper(), Validation.buildDefaultValidatorFactory().getValidator()); }

    @Test void validLlmJsonBecomesCategorization() {
        var result = service(new FakeLlm(llmAnswer("PREOCUPANTE", "la insultan todos los días"))).categorize(student("A Lucía la insultan en clase todos los días."));
        assertEquals(new CategorizationResponse(HarassmentAssessment.SI, HarassmentType.VERBAL, InformantRelation.TESTIGO, Recurrence.SI, UrgencyLevel.PREOCUPANTE,
                "Un alumno cuenta que a Lucía la insultan en clase todos los días.", List.of("la insultan todos los días")), result);
    }

    @Test void invalidJsonIsRetriedOnce() {
        var llm = new FakeLlm("esto no es JSON", llmAnswer("PREOCUPANTE"));
        var result = service(llm).categorize(student("A Lucía la insultan en clase."));
        assertEquals(HarassmentAssessment.SI, result.getEsAcoso());
        assertEquals(2, llm.calls);
    }

    @Test void invalidOrIncompleteJsonTwiceMeansCategorizationUnavailable() {
        var llm = new FakeLlm("{\"esAcoso\":\"QUIZAS\"}", "{\"esAcoso\":\"SI\"}", llmAnswer("NORMAL"));
        assertThrows(CategorizationUnavailableException.class, () -> service(llm).categorize(student("A Lucía la insultan en clase.")));
        assertEquals(2, llm.calls);
    }

    @Test void llmDownWithoutRiskMeansCategorizationUnavailable() {
        var llm = new FakeLlm(new IllegalStateException("el proveedor no responde"), new IllegalStateException("timeout"));
        assertThrows(CategorizationUnavailableException.class, () -> service(llm).categorize(student("A Lucía la insultan en clase.")));
    }

    @SuppressWarnings("unchecked")
    @Test void llmReceivesWholeConversationPromptAndCategorizationSchema() {
        var llm = new FakeLlm(llmAnswer("NORMAL"));
        var conversation = List.of(new ReportMessage(MessageRole.ALUMNO, "Quería contarte algo de un compañero."),
                new ReportMessage(MessageRole.ASISTENTE, "Claro, cuéntame lo que quieras."),
                new ReportMessage(MessageRole.ALUMNO, "En el grupo de clase suben fotos suyas para reírse."));
        service(llm).categorize(conversation);
        conversation.forEach(m -> assertTrue(llm.messages.get(0).contains(m.getTexto())));
        assertTrue(llm.systemPrompts.get(0).contains("relacionInformante"));
        var properties = (Map<String, Object>) llm.schemas.get(0).get("properties");
        assertEquals(Set.of("esAcoso", "tipoAcoso", "relacionInformante", "reiteracion", "nivelUrgencia", "resumen", "indicios"), properties.keySet());
    }

    @Test void safetyNetRaisesUrgencyToImminentRisk() {
        var result = service(new FakeLlm(llmAnswer("NORMAL"))).categorize(student("Se meten conmigo en el recreo.", "A veces ME QUIERO MORÍR."));
        assertEquals(UrgencyLevel.RIESGO_INMINENTE, result.getNivelUrgencia());
        assertTrue(result.getIndicios().contains("A veces ME QUIERO MORÍR."));
    }

    @Test void safetyNetNeverLowersUrgencyMarkedByLlm() {
        var result = service(new FakeLlm(llmAnswer("RIESGO_INMINENTE", "me espera a la salida"))).categorize(student("Un chico de 4º me espera a la salida para pegarme."));
        assertEquals(UrgencyLevel.RIESGO_INMINENTE, result.getNivelUrgencia());
        assertEquals(List.of("me espera a la salida"), result.getIndicios());
    }

    @Test void assistantMessagesDoNotTriggerSafetyNet() {
        var conversation = List.of(new ReportMessage(MessageRole.ALUMNO, "Mis amigos ya no me hablan."),
                new ReportMessage(MessageRole.ASISTENTE, "Si alguna vez piensas en el suicidio, llama al 024."));
        assertEquals(UrgencyLevel.NORMAL, service(new FakeLlm(llmAnswer("NORMAL"))).categorize(conversation).getNivelUrgencia());
    }

    @Test void safetyNetMatchesWholeWordsExceptRoots() {
        var result = service(new FakeLlm(llmAnswer("NORMAL"))).categorize(student("Me encerraron en un armario del gimnasio y luego me pidieron perdón."));
        assertEquals(UrgencyLevel.NORMAL, result.getNivelUrgencia());
    }

    @Test void llmDownWithRiskDetectedReturnsImminentRiskCategorization() {
        var llm = new FakeLlm(new IllegalStateException("el proveedor no responde"), "esto no es JSON");
        var result = service(llm).categorize(student("Me insultan por WhatsApp.", "He pensado en suicidarme."));
        assertEquals(UrgencyLevel.RIESGO_INMINENTE, result.getNivelUrgencia());
        assertEquals(HarassmentAssessment.DUDOSO, result.getEsAcoso());
        assertEquals(InformantRelation.NO_ESPECIFICADO, result.getRelacionInformante());
        assertEquals(Recurrence.DESCONOCIDO, result.getReiteracion());
        assertEquals(List.of("He pensado en suicidarme."), result.getIndicios());
    }
}

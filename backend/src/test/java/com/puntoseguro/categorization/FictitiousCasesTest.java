package com.puntoseguro.categorization;

import com.puntoseguro.dto.CategorizationResponse;
import com.puntoseguro.dto.ReportMessage;
import com.puntoseguro.entity.enums.MessageRole;
import com.puntoseguro.llm.LlmProperties;
import com.puntoseguro.llm.OpenAiCompatibleLlmClient;
import com.puntoseguro.service.ImminentRiskDetector;
import com.puntoseguro.service.ReportCategorizationService;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import tools.jackson.databind.json.JsonMapper;
import java.util.*;
import static com.puntoseguro.entity.enums.HarassmentAssessment.*;
import static com.puntoseguro.entity.enums.HarassmentType.*;
import static com.puntoseguro.entity.enums.InformantRelation.*;
import static com.puntoseguro.entity.enums.UrgencyLevel.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Punto de prueba S2: la Categorización con el LLM real y casos ficticios. Solo con datos ficticios (ver docs/adr/0001).
 * Se ejecuta a mano, con la clave en el entorno:
 *   PUNTOSEGURO_LLM_API_KEY=... ./mvnw test -Dtest=FictitiousCasesTest
 * Se salta si no hay clave (con Ollama, PUNTOSEGURO_LLM_API_KEY=ollama). Solo comprueba los campos clave, no el texto del resumen,
 * porque el modelo no es determinista.
 */
@EnabledIf("llmConfigured")
class FictitiousCasesTest {
    static ReportCategorizationService service;

    static boolean llmConfigured() { return !env("PUNTOSEGURO_LLM_API_KEY", "").isBlank(); }
    static String env(String name, String fallback) { return Optional.ofNullable(System.getenv(name)).orElse(fallback); }

    @BeforeAll static void realLlm() {
        var p = new LlmProperties();
        p.setUrl(env("PUNTOSEGURO_LLM_URL", "https://api.groq.com/openai/v1"));
        p.setModel(env("PUNTOSEGURO_LLM_MODELO", "openai/gpt-oss-20b"));
        p.setApiKey(env("PUNTOSEGURO_LLM_API_KEY", ""));
        p.setTimeoutSeconds(Integer.parseInt(env("PUNTOSEGURO_LLM_TIMEOUT", "60")));
        service = new ReportCategorizationService(new OpenAiCompatibleLlmClient(p), new ImminentRiskDetector(), new JsonMapper(), Validation.buildDefaultValidatorFactory().getValidator());
    }

    static CategorizationResponse student(String... texts) {
        return service.categorize(Arrays.stream(texts).map(t -> new ReportMessage(MessageRole.ALUMNO, t)).toList());
    }

    @Test void directlyAffectedWithRepeatedVerbalHarassment() {
        var c = student("Desde que empezó el curso dos chicos de mi clase me llaman gorda todos los días y se ríen de mí.");
        assertEquals(SI, c.getEsAcoso()); assertEquals(AFECTADO_DIRECTO, c.getRelacionInformante()); assertEquals(VERBAL, c.getTipoAcoso());
        assertEquals(com.puntoseguro.entity.enums.Recurrence.SI, c.getReiteracion()); assertTrue(Set.of(PREOCUPANTE, RIESGO_INMINENTE).contains(c.getNivelUrgencia()));
    }

    @Test void witnessOfPhysicalAggression() {
        var c = student("A mi compañero Hugo le empujan y le dan collejas en el pasillo casi todos los días, y no sé qué hacer para ayudarle.");
        assertEquals(SI, c.getEsAcoso()); assertEquals(TESTIGO, c.getRelacionInformante()); assertEquals(FISICO, c.getTipoAcoso());
        assertTrue(Set.of(PREOCUPANTE, RIESGO_INMINENTE).contains(c.getNivelUrgencia()));
    }

    @Test void cyberbullyingToldInSeveralMessages() {
        var c = service.categorize(List.of(new ReportMessage(MessageRole.ALUMNO, "Hola, quería contar una cosa."),
                new ReportMessage(MessageRole.ASISTENTE, "Claro, aquí estoy. Cuéntame lo que quieras."),
                new ReportMessage(MessageRole.ALUMNO, "En un grupo de WhatsApp de la clase están pasando una foto mía editada para reírse y me escriben mensajes diciendo que doy asco.")));
        assertEquals(SI, c.getEsAcoso()); assertEquals(CIBERACOSO, c.getTipoAcoso()); assertEquals(AFECTADO_DIRECTO, c.getRelacionInformante());
    }

    @Test void exclusionIsRecognisedAsSocialHarassment() {
        var c = student("A una chica nueva de mi clase nadie le deja sentarse con ellos en el comedor y cuando se acerca se van. Lleva así semanas.");
        assertTrue(Set.of(SI, DUDOSO).contains(c.getEsAcoso())); assertEquals(SOCIAL, c.getTipoAcoso()); assertEquals(TESTIGO, c.getRelacionInformante());
    }

    @Test void whatIsNotHarassmentIsNotTreatedAsHarassment() {
        var c = student("¿Sabes cuándo es el examen de recuperación de matemáticas? No lo encuentro en PASEN.");
        assertEquals(NO, c.getEsAcoso()); assertEquals(NORMAL, c.getNivelUrgencia());
    }

    @Test void imminentRisk() {
        // Paráfrasis fuera de palabras_riesgo.txt: comprueba el criterio del modelo, no la red de seguridad.
        var c = student("Ya no aguanto más lo que me hacen en el instituto. Esta tarde pienso tirarme desde la azotea.");
        assertEquals(RIESGO_INMINENTE, c.getNivelUrgencia()); assertFalse(c.getIndicios().isEmpty());
    }

    @Test void informantWhoDoesNotSayWhoItHappensTo() {
        assertEquals(NO_ESPECIFICADO, student("¿Qué se puede hacer si en clase hay alguien a quien insultan siempre?").getRelacionInformante());
    }

    @Test void ambiguous() {
        var c = student("Hoy me he peleado con mi mejor amiga y estoy triste.");
        assertTrue(Set.of(NO, DUDOSO).contains(c.getEsAcoso())); assertNotEquals(RIESGO_INMINENTE, c.getNivelUrgencia());
    }
}

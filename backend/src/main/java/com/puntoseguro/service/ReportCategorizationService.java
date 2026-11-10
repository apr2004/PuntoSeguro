package com.puntoseguro.service;
import com.puntoseguro.dto.CategorizationResponse; import com.puntoseguro.dto.ReportMessage; import com.puntoseguro.entity.enums.*; import com.puntoseguro.exception.CategorizationUnavailableException; import com.puntoseguro.llm.LlmClient;
import jakarta.validation.Validator; import lombok.RequiredArgsConstructor; import org.springframework.core.io.ClassPathResource; import org.springframework.stereotype.Service; import tools.jackson.databind.json.JsonMapper;
import java.io.IOException; import java.io.UncheckedIOException; import java.nio.charset.StandardCharsets; import java.util.*; import java.util.stream.Collectors;
/** Categorización de Reportes. Interpreta la conversación completa y devuelve una Categorización orientativa. */
@Service @RequiredArgsConstructor public class ReportCategorizationService {
 private static final int ATTEMPTS = 2; // el primero y un reintento
 private static final Map<String, Object> SCHEMA = schema();
 private final LlmClient llm; private final ImminentRiskDetector risk; private final JsonMapper json; private final Validator validator;

 /** Lanza CategorizationUnavailableException si el LLM falla y no hay señal de Riesgo inminente. */
 public CategorizationResponse categorize(List<ReportMessage> conversation) {
  List<String> riskSentences = risk.riskSentences(conversation);
  CategorizationResponse c;
  try { c = categorizeWithLlm(conversation); }
  catch (CategorizationUnavailableException e) {
   if (riskSentences.isEmpty()) throw e; // nunca se inventa una Categorización sin riesgo
   return imminentRiskWithoutLlm(riskSentences);
  }
  if (!riskSentences.isEmpty()) { // la red solo sube la urgencia, nunca la baja
   c.setNivelUrgencia(UrgencyLevel.RIESGO_INMINENTE);
   var evidence = new ArrayList<>(c.getIndicios());
   riskSentences.stream().filter(f -> !evidence.contains(f)).forEach(evidence::add);
   c.setIndicios(evidence);
  }
  return c;
 }

 /** Si el LLM falla y la red detecta riesgo, la alerta no se pierde. */
 private static CategorizationResponse imminentRiskWithoutLlm(List<String> riskSentences) {
  return new CategorizationResponse(HarassmentAssessment.DUDOSO, HarassmentType.OTRO, InformantRelation.NO_ESPECIFICADO, Recurrence.DESCONOCIDO, UrgencyLevel.RIESGO_INMINENTE,
   "Categorización automática no disponible. La red de seguridad ha detectado expresiones de riesgo inminente en el relato.", riskSentences);
 }

 private CategorizationResponse categorizeWithLlm(List<ReportMessage> conversation) {
  String system = prompt("categorizacion"), message = "Conversación:\n" + transcript(conversation);
  RuntimeException last = null;
  for (int i = 0; i < ATTEMPTS; i++) {
   try {
    CategorizationResponse c = json.readValue(llm.generate(system, message, SCHEMA), CategorizationResponse.class);
    var violations = validator.validate(c);
    if (violations.isEmpty()) return c;
    last = new IllegalStateException("Categorización incompleta: " + violations);
   } catch (RuntimeException e) { last = e; } // JSON inválido, sin respuesta, timeout o error del proveedor: se reintenta
  }
  throw new CategorizationUnavailableException("El LLM no ha podido categorizar el Reporte.", last);
 }

 /** Prompts en español, editables sin tocar código, en resources/prompts. */
 public static String prompt(String name) {
  try { return new ClassPathResource("prompts/" + name + ".md").getContentAsString(StandardCharsets.UTF_8); }
  catch (IOException e) { throw new UncheckedIOException(e); }
 }

 private static String transcript(List<ReportMessage> conversation) {
  return conversation.stream().map(m -> (m.getRol() == MessageRole.ALUMNO ? "Alumno: " : "Asistente: ") + m.getTexto()).collect(Collectors.joining("\n"));
 }

 private static Map<String, Object> schema() {
  var properties = new LinkedHashMap<String, Object>();
  properties.put("esAcoso", values(HarassmentAssessment.values()));
  properties.put("tipoAcoso", values(HarassmentType.values()));
  properties.put("relacionInformante", values(InformantRelation.values()));
  properties.put("reiteracion", values(Recurrence.values()));
  properties.put("nivelUrgencia", values(UrgencyLevel.values()));
  properties.put("resumen", Map.of("type", "string"));
  properties.put("indicios", Map.of("type", "array", "items", Map.of("type", "string")));
  return Map.of("type", "object", "properties", properties, "required", List.copyOf(properties.keySet()), "additionalProperties", false);
 }
 private static Map<String, Object> values(Enum<?>[] values) { return Map.of("type", "string", "enum", Arrays.stream(values).map(Enum::name).toList()); }
}

package com.puntoseguro.service;
import com.puntoseguro.dto.ReportMessage; import com.puntoseguro.entity.enums.MessageRole;
import org.springframework.core.io.ClassPathResource; import org.springframework.stereotype.Component;
import java.io.IOException; import java.io.UncheckedIOException; import java.nio.charset.StandardCharsets; import java.text.Normalizer; import java.util.*; import java.util.regex.Pattern;
/** Red de seguridad determinista: detecta señales de Riesgo inminente con la lista editable de palabras clave. */
@Component public class ImminentRiskDetector {
 private static final Pattern SENTENCE = Pattern.compile("[^.!?\\n]+[.!?]*");
 private final List<Pattern> patterns = load();

 /** Frases de los mensajes del alumno que contienen alguna expresión de riesgo. */
 public List<String> riskSentences(List<ReportMessage> conversation) {
  var found = new ArrayList<String>();
  for (ReportMessage m : conversation) {
   if (m.getRol() != MessageRole.ALUMNO) continue; // el asistente puede nombrar el suicidio al dar teléfonos de ayuda
   var sentences = SENTENCE.matcher(m.getTexto());
   while (sentences.find()) {
    String sentence = sentences.group().strip(), normalized = normalize(sentence);
    if (!sentence.isEmpty() && patterns.stream().anyMatch(p -> p.matcher(normalized).find())) found.add(sentence);
   }
  }
  return found;
 }

 /** Minúsculas y sin tildes, para comparar sin depender de cómo escriba el alumno. */
 static String normalize(String text) {
  return Normalizer.normalize(text.toLowerCase(Locale.ROOT), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
 }

 /** Una expresión por línea: palabras enteras, o raíz si termina en *. */
 private static List<Pattern> load() {
  String text;
  try { text = new ClassPathResource("categorization/palabras_riesgo.txt").getContentAsString(StandardCharsets.UTF_8); }
  catch (IOException e) { throw new UncheckedIOException(e); }
  return text.lines().map(String::strip).map(ImminentRiskDetector::normalize).filter(l -> !l.isEmpty() && !l.startsWith("#"))
   .map(l -> l.endsWith("*") ? "\\b" + Pattern.quote(l.substring(0, l.length() - 1)) : "\\b" + Pattern.quote(l) + "\\b")
   .map(r -> Pattern.compile(r, Pattern.UNICODE_CHARACTER_CLASS)).toList();
 }
}

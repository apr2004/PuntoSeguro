package com.puntoseguro.llm;
import java.util.Map;
/** Cliente de un LLM que responde con JSON conforme a un esquema. */
public interface LlmClient {
    /** Devuelve el JSON que genera el modelo. Lanza una excepción si no hay respuesta. */
    String generate(String systemPrompt, String userMessage, Map<String, Object> jsonSchema);
}

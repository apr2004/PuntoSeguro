package com.puntoseguro.llm;
import org.springframework.http.client.JdkClientHttpRequestFactory; import org.springframework.stereotype.Component; import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import java.net.http.HttpClient; import java.time.Duration; import java.util.List; import java.util.Map;
/** Adaptador de cualquier API compatible con OpenAI (`chat/completions`): Groq u Ollama. Salida forzada por esquema JSON y temperature 0. */
@Component public class OpenAiCompatibleLlmClient implements LlmClient {
    private final RestClient http; private final String model; private final boolean configured;

    public OpenAiCompatibleLlmClient(LlmProperties properties) {
        var timeout = Duration.ofSeconds(properties.getTimeoutSeconds());
        var factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder().connectTimeout(timeout).build());
        factory.setReadTimeout(timeout);
        configured = properties.getApiKey() != null && !properties.getApiKey().isBlank();
        http = RestClient.builder().baseUrl(properties.getUrl()).requestFactory(factory).defaultHeader("Authorization", "Bearer " + properties.getApiKey()).build();
        model = properties.getModel();
    }

    @Override public String generate(String systemPrompt, String userMessage, Map<String, Object> jsonSchema) {
        // Sin clave no se envía nada: el relato no sale de la máquina para recibir un 401.
        if (!configured) throw new IllegalStateException("Falta PUNTOSEGURO_LLM_API_KEY.");
        var body = Map.of("model", model, "temperature", 0,
                "messages", List.of(Map.of("role", "system", "content", systemPrompt), Map.of("role", "user", "content", userMessage)),
                "response_format", Map.of("type", "json_schema", "json_schema", Map.of("name", "respuesta", "strict", true, "schema", jsonSchema)));
        JsonNode response = http.post().uri("/chat/completions").body(body).retrieve().body(JsonNode.class);
        if (response == null) throw new IllegalStateException("El proveedor del LLM no ha devuelto respuesta.");
        return response.path("choices").path(0).path("message").path("content").asText();
    }
}

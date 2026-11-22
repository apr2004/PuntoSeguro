package com.puntoseguro.llm;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.junit.jupiter.api.Assertions.*;

class OpenAiCompatibleLlmClientTest {
    @Test
    void sendsApiKeyToOpenAiCompatibleChatCompletionsEndpoint() {
        var builder = RestClient.builder();
        var mockServer = MockRestServiceServer.bindTo(builder).build();
        mockServer.expect(requestTo("https://llm.example/v1/chat/completions"))
                .andExpect(header("Authorization", "Bearer test-secret"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"model\":\"test-model\"")))
                .andRespond(withSuccess("{\"choices\":[{\"message\":{\"content\":\"{\\\"ok\\\":true}\"}}]}",
                        org.springframework.http.MediaType.APPLICATION_JSON));
        var properties = new LlmProperties();
        properties.setUrl("https://llm.example/v1");
        properties.setModel("test-model");
        properties.setApiKey("test-secret");
        properties.setTimeoutSeconds(2);

        var result = new OpenAiCompatibleLlmClient(properties, builder).generate("system", "user", Map.of());

        assertEquals("{\"ok\":true}", result);
        mockServer.verify();
    }

    @Test
    void doesNotCallProviderWhenApiKeyIsMissing() {
        var builder = RestClient.builder();
        MockRestServiceServer.bindTo(builder).build();
        var properties = new LlmProperties();
        properties.setUrl("https://llm.example/v1");
        properties.setModel("test-model");
        properties.setTimeoutSeconds(1);

        var error = assertThrows(IllegalStateException.class,
                () -> new OpenAiCompatibleLlmClient(properties, builder).generate("system", "user", Map.of()));

        assertEquals("Falta PUNTOSEGURO_LLM_API_KEY.", error.getMessage());
    }
}

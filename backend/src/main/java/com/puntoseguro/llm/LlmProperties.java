package com.puntoseguro.llm;
import jakarta.validation.constraints.Min; import jakarta.validation.constraints.NotBlank; import lombok.Getter; import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties; import org.springframework.validation.annotation.Validated;
/** Proveedor del LLM con API compatible con OpenAI (Groq por defecto u Ollama). Ver docs/adr/0001. */
@Getter @Setter @Validated @ConfigurationProperties("puntoseguro.llm")
public class LlmProperties {
    @NotBlank private String url;
    @NotBlank private String model;
    /** Opcional: sin clave el backend arranca y la Categorización responde 503. Ollama la ignora, pero hay que darle un valor (p. ej. "ollama"). */
    private String apiKey;
    @Min(1) private int timeoutSeconds;
}

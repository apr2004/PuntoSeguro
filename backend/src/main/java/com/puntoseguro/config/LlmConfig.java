package com.puntoseguro.config;
import com.puntoseguro.llm.LlmProperties; import org.springframework.boot.context.properties.EnableConfigurationProperties; import org.springframework.context.annotation.Configuration;
@Configuration @EnableConfigurationProperties(LlmProperties.class) public class LlmConfig {}

package com.puntoseguro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Respuesta orientativa del chatbot; no es una Categorización ni un diagnóstico. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    @NotBlank
    private String respuesta;

    @NotNull
    private Boolean puedeContinuar;
}

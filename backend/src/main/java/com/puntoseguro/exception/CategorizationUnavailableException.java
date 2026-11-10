package com.puntoseguro.exception;
/** El LLM no ha podido categorizar el Reporte y no hay señal de Riesgo inminente. */
public class CategorizationUnavailableException extends RuntimeException {
    public CategorizationUnavailableException(String message, Throwable cause) { super(message, cause); }
}

package com.puntoseguro.exception;

/** El proveedor no ha podido generar una respuesta orientativa para el chat. */
public class ChatbotUnavailableException extends RuntimeException {
    public ChatbotUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

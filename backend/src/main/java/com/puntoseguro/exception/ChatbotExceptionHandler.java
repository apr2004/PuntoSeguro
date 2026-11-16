package com.puntoseguro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ChatbotExceptionHandler {
    @ExceptionHandler(ChatbotUnavailableException.class)
    ProblemDetail unavailable() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "No se ha podido responder en este momento. Inténtalo de nuevo más tarde.");
        problem.setProperty("code", "CHATBOT_UNAVAILABLE");
        return problem;
    }
}

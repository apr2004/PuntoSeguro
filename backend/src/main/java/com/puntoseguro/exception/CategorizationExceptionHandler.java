package com.puntoseguro.exception;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice public class CategorizationExceptionHandler {
 private static final Logger log = LoggerFactory.getLogger(CategorizationExceptionHandler.class);
 @ExceptionHandler(CategorizationUnavailableException.class) ProblemDetail unavailable(CategorizationUnavailableException e) {
  log.warn("Categorización no disponible: {}", e.getCause() == null ? "sin causa" : e.getCause().getClass().getName()); // sin el contenido: puede incluir el relato
  ProblemDetail p=ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,"No se ha podido categorizar el Reporte en este momento. Inténtalo de nuevo más tarde.");
  p.setProperty("code","CATEGORIZATION_UNAVAILABLE"); return p;
 }
}

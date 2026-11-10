package com.puntoseguro.exception;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice public class AuthExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class) ProblemDetail badCredentials() { return problem(HttpStatus.UNAUTHORIZED,"Credenciales no válidas.","UNAUTHENTICATED"); }
    @ExceptionHandler(MethodArgumentNotValidException.class) ProblemDetail invalid(MethodArgumentNotValidException e) {
        ProblemDetail p=problem(HttpStatus.BAD_REQUEST,"Revisa los campos indicados.","VALIDATION_ERROR");
        p.setProperty("fields",e.getBindingResult().getFieldErrors().stream().map(x -> x.getField()).distinct().toList()); return p;
    }
    // JSON mal formado o valor de enum desconocido: mensaje fijo, sin repetir el cuerpo (puede contener un relato).
    @ExceptionHandler(HttpMessageNotReadableException.class) ProblemDetail unreadable() { return problem(HttpStatus.BAD_REQUEST,"El cuerpo de la petición no es válido.","VALIDATION_ERROR"); }
    @ExceptionHandler(ResponseStatusException.class) ProblemDetail status(ResponseStatusException e) { return problem(HttpStatus.valueOf(e.getStatusCode().value()),e.getReason(),"AUTH_ERROR"); }
    private ProblemDetail problem(HttpStatus status,String detail,String code) { ProblemDetail p=ProblemDetail.forStatusAndDetail(status,detail); p.setProperty("code",code); return p; }
}

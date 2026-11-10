package com.puntoseguro.exception;
import org.springframework.transaction.CannotCreateTransactionException; import org.springframework.dao.DataAccessResourceFailureException; import org.springframework.dao.DataIntegrityViolationException; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice public class StudentDataExceptionHandler {
 @ExceptionHandler(DataIntegrityViolationException.class) ProblemDetail conflict() { return problem(HttpStatus.CONFLICT,"No se puede guardar o eliminar: existen datos relacionados o duplicados.","DATA_CONFLICT"); }
 @ExceptionHandler({DataAccessResourceFailureException.class, CannotCreateTransactionException.class}) ProblemDetail database() { return problem(HttpStatus.SERVICE_UNAVAILABLE,"No se ha podido conectar con la base de datos. Los últimos datos guardados se conservan.","DATABASE_UNAVAILABLE"); }
 private ProblemDetail problem(HttpStatus s,String message,String code) { ProblemDetail p=ProblemDetail.forStatusAndDetail(s,message); p.setProperty("code",code); return p; }
}

package com.puntoseguro.controller;
import com.puntoseguro.dto.ObservationRequest; import com.puntoseguro.dto.ObservationResponse; import com.puntoseguro.security.PuntoSeguroPrincipal; import com.puntoseguro.service.ObservationService;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.responses.ApiResponse; import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*; import java.util.List;
/** Observaciones de un alumno: información de seguimiento, que no constituye un Caso confirmado. */
@RestController @RequestMapping("/api") @RequiredArgsConstructor @SecurityRequirement(name="bearerAuth")
@ApiResponse(responseCode="401",description="Autenticación necesaria (UNAUTHENTICATED)") @ApiResponse(responseCode="403",description="Rol no permitido (ACCESS_DENIED)") @ApiResponse(responseCode="404",description="Estudiante no encontrado") @ApiResponse(responseCode="503",description="Base de datos no disponible (DATABASE_UNAVAILABLE)")
public class ObservationController {
 private final ObservationService observations;
 @Operation(summary="Listar observaciones de un alumno",description="De la más reciente a la más antigua, con autor y fecha de registro. Roles: PROFESOR y CENTRO.")
 @ApiResponse(responseCode="200",description="Observaciones del alumno")
 @GetMapping("/students/{sid}/observations") public List<ObservationResponse> list(@PathVariable Integer sid) { return observations.list(sid); }
 @Operation(summary="Añadir una observación a un alumno",description="El autor es el usuario autenticado y la fecha la pone la plataforma; cualquier autor o fecha del cuerpo se ignora. El texto, sin espacios al principio y al final, debe tener entre 1 y 2000 caracteres. Rol: PROFESOR.")
 @ApiResponse(responseCode="201",description="Observación registrada") @ApiResponse(responseCode="400",description="Texto no válido (VALIDATION_ERROR, campo texto)")
 @PostMapping("/students/{sid}/observations") @ResponseStatus(HttpStatus.CREATED)
 public ObservationResponse create(@PathVariable Integer sid,@AuthenticationPrincipal PuntoSeguroPrincipal principal,@Valid @RequestBody ObservationRequest r) { return observations.create(sid,principal.getId(),r); }
}

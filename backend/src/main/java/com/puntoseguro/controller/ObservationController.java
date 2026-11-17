package com.puntoseguro.controller;

import com.puntoseguro.dto.ObservationHistoryResponse;
import com.puntoseguro.dto.ObservationRequest;
import com.puntoseguro.dto.ObservationResponse;
import com.puntoseguro.dto.AuthorResponse;
import com.puntoseguro.security.PuntoSeguroPrincipal;
import com.puntoseguro.service.ObservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Observaciones de un alumno: información de seguimiento, nunca un Caso confirmado. */
@RestController @RequestMapping("/api") @RequiredArgsConstructor @SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "Autenticación necesaria (UNAUTHENTICATED)")
@ApiResponse(responseCode = "403", description = "Rol o asignación no permitidos (ACCESS_DENIED)")
@ApiResponse(responseCode = "404", description = "Recurso no encontrado")
@ApiResponse(responseCode = "503", description = "Base de datos no disponible (DATABASE_UNAVAILABLE)")
public class ObservationController {
    private final ObservationService observations;

    @Operation(summary = "Listar docentes disponibles", description = "Solo el centro; devuelve usuarios activos con rol PROFESOR.")
    @ApiResponse(responseCode = "200", description = "Docentes disponibles")
    @GetMapping("/teachers")
    public List<AuthorResponse> teachers(@AuthenticationPrincipal PuntoSeguroPrincipal principal) {
        return observations.teachers(principal);
    }

    @Operation(summary = "Listar docentes asignados a un alumno", description = "Solo el centro.")
    @ApiResponse(responseCode = "200", description = "Docentes asignados")
    @GetMapping("/students/{sid}/teachers")
    public List<AuthorResponse> assignedTeachers(@PathVariable Integer sid, @AuthenticationPrincipal PuntoSeguroPrincipal principal) {
        return observations.assignedTeachers(sid, principal);
    }

    @Operation(summary = "Listar observaciones de un alumno", description = "El centro puede consultar todas; un docente solo las de alumnos asignados. Orden de más reciente a más antigua.")
    @ApiResponse(responseCode = "200", description = "Observaciones con autor y fecha de registro")
    @GetMapping("/students/{sid}/observations")
    public List<ObservationResponse> list(@PathVariable Integer sid, @AuthenticationPrincipal PuntoSeguroPrincipal principal) {
        return observations.list(sid, principal);
    }

    @Operation(summary = "Consultar historial de una observación", description = "Versiones de la más antigua a la más reciente, con el usuario que registró cada texto. El centro puede consultar todas; un docente solo las de alumnos asignados.")
    @ApiResponse(responseCode = "200", description = "Historial de la observación")
    @GetMapping("/students/{sid}/observations/{id}/history")
    public List<ObservationHistoryResponse> history(@PathVariable Integer sid, @PathVariable Integer id,
            @AuthenticationPrincipal PuntoSeguroPrincipal principal) {
        return observations.history(sid, id, principal);
    }

    @Operation(summary = "Añadir observación", description = "Solo un docente asignado al alumno. Autor y fecha se toman del servidor; el texto tiene entre 1 y 2000 caracteres tras recortarlo.")
    @ApiResponse(responseCode = "201", description = "Observación registrada")
    @ApiResponse(responseCode = "400", description = "Texto no válido (VALIDATION_ERROR, campo texto)")
    @PostMapping("/students/{sid}/observations") @ResponseStatus(HttpStatus.CREATED)
    public ObservationResponse create(@PathVariable Integer sid, @AuthenticationPrincipal PuntoSeguroPrincipal principal,
            @Valid @RequestBody ObservationRequest request) {
        return observations.create(sid, principal, request);
    }

    @Operation(summary = "Corregir observación", description = "El autor puede corregir su texto mientras siga asignado; el centro puede corregirlo. Se conservan autor y fecha originales y cada versión queda en el historial.")
    @ApiResponse(responseCode = "200", description = "Observación corregida")
    @ApiResponse(responseCode = "400", description = "Texto no válido (VALIDATION_ERROR, campo texto)")
    @PutMapping("/students/{sid}/observations/{id}")
    public ObservationResponse update(@PathVariable Integer sid, @PathVariable Integer id,
            @AuthenticationPrincipal PuntoSeguroPrincipal principal, @Valid @RequestBody ObservationRequest request) {
        return observations.update(sid, id, principal, request);
    }

    @Operation(summary = "Asignar docente al alumno", description = "Solo el centro. Asignación idempotente de un usuario activo con rol PROFESOR.")
    @ApiResponse(responseCode = "204", description = "Docente asignado")
    @PutMapping("/students/{sid}/teachers/{teacherId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assign(@PathVariable Integer sid, @PathVariable UUID teacherId,
            @AuthenticationPrincipal PuntoSeguroPrincipal principal) {
        observations.assign(sid, teacherId, principal);
    }

    @Operation(summary = "Retirar asignación de docente", description = "Solo el centro. La retirada es idempotente y revoca el acceso a las observaciones.")
    @ApiResponse(responseCode = "204", description = "Asignación retirada")
    @DeleteMapping("/students/{sid}/teachers/{teacherId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassign(@PathVariable Integer sid, @PathVariable UUID teacherId,
            @AuthenticationPrincipal PuntoSeguroPrincipal principal) {
        observations.unassign(sid, teacherId, principal);
    }
}

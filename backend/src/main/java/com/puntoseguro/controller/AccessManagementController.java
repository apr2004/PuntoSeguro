package com.puntoseguro.controller;
import com.puntoseguro.dto.*;
import com.puntoseguro.security.PuntoSeguroPrincipal;
import com.puntoseguro.service.StudentAccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api") @RequiredArgsConstructor @SecurityRequirement(name="bearerAuth")
public class AccessManagementController {
 private final StudentAccessService access;
 @Operation(summary="Listar usuarios activos",description="Solo el centro.")
 @GetMapping("/users") public List<UserResponse> users(@AuthenticationPrincipal PuntoSeguroPrincipal p) { return access.users(p); }
 @Operation(summary="Listar cuentas vinculadas al alumno",description="Solo el centro.")
 @GetMapping("/students/{sid}/links") public List<UserResponse> links(@PathVariable Integer sid,@AuthenticationPrincipal PuntoSeguroPrincipal p) { return access.linked(p,sid); }
 @Operation(summary="Asignar rol",description="Solo el centro. Un cambio de rol retira las relaciones anteriores; se requiere iniciar sesión de nuevo.")
 @PutMapping("/users/{uid}/role") @ResponseStatus(HttpStatus.NO_CONTENT) public void role(@PathVariable UUID uid,@Valid @RequestBody RoleRequest r,@AuthenticationPrincipal PuntoSeguroPrincipal p) { access.changeRole(p,uid,r.getRole()); }
 @Operation(summary="Vincular cuenta con alumno",description="Solo el centro; admite estudiantes y tutores legales activos.")
 @PutMapping("/students/{sid}/links/{uid}") @ResponseStatus(HttpStatus.NO_CONTENT) public void link(@PathVariable Integer sid,@PathVariable UUID uid,@AuthenticationPrincipal PuntoSeguroPrincipal p) { access.link(p,sid,uid); }
 @Operation(summary="Retirar vínculo con alumno",description="Solo el centro.")
 @DeleteMapping("/students/{sid}/links/{uid}") @ResponseStatus(HttpStatus.NO_CONTENT) public void unlink(@PathVariable Integer sid,@PathVariable UUID uid,@AuthenticationPrincipal PuntoSeguroPrincipal p) { access.unlink(p,sid,uid); }
}

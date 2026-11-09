package com.puntoseguro.controller;
import com.puntoseguro.dto.*;
import com.puntoseguro.security.PuntoSeguroPrincipal;
import com.puntoseguro.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor public class AuthController {
    private final AuthService auth;
    @Operation(summary="Iniciar sesión", responses={@ApiResponse(responseCode="200",description="Sesión iniciada"),@ApiResponse(responseCode="400",description="Datos inválidos"),@ApiResponse(responseCode="401",description="Credenciales inválidas")})
    @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest request) { return auth.login(request); }
    @Operation(summary="Registrar estudiante", responses={@ApiResponse(responseCode="201",description="Cuenta creada"),@ApiResponse(responseCode="400",description="Datos inválidos"),@ApiResponse(responseCode="409",description="Correo registrado")})
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public UserResponse register(@Valid @RequestBody RegisterRequest request) { return auth.register(request); }
    @Operation(summary="Consultar sesión", security=@SecurityRequirement(name="bearerAuth"), responses={@ApiResponse(responseCode="200",description="Usuario actual"),@ApiResponse(responseCode="401",description="Credencial inválida")})
    @GetMapping("/me") public UserResponse me(@AuthenticationPrincipal PuntoSeguroPrincipal principal) { return auth.current(principal); }
}

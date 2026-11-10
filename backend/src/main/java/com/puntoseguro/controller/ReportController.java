package com.puntoseguro.controller;
import com.puntoseguro.dto.CategorizationResponse; import com.puntoseguro.dto.ReportRequest; import com.puntoseguro.service.ReportCategorizationService;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.responses.ApiResponse; import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api") @RequiredArgsConstructor @SecurityRequirement(name="bearerAuth")
public class ReportController {
 private final ReportCategorizationService categorization;
 @Operation(summary="Categorizar un Reporte", description="Interpreta la conversación completa del alumno y devuelve una Categorización orientativa. No guarda nada. Si el LLM falla pero la red de seguridad detecta Riesgo inminente, devuelve igualmente una Categorización de RIESGO_INMINENTE.",
  responses={@ApiResponse(responseCode="200",description="Categorización del Reporte"),@ApiResponse(responseCode="400",description="Conversación no válida"),@ApiResponse(responseCode="401",description="Autenticación necesaria"),@ApiResponse(responseCode="503",description="LLM no disponible (CATEGORIZATION_UNAVAILABLE)")})
 @PostMapping("/reports/categorization") public CategorizationResponse categorize(@Valid @RequestBody ReportRequest request) { return categorization.categorize(request.getConversacion()); }
}

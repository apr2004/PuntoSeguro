package com.puntoseguro.controller;
import com.puntoseguro.dto.*; import com.puntoseguro.security.PuntoSeguroPrincipal; import com.puntoseguro.service.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.responses.ApiResponse; import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api") @RequiredArgsConstructor @SecurityRequirement(name="bearerAuth")
public class ReportController {
 private final ReportCategorizationService categorization;
 private final ChatbotService chatbot;
 private final ReportService reports;
 @Operation(summary="Categorizar un Reporte", description="Interpreta la conversación completa del alumno y devuelve una Categorización orientativa. No guarda nada. Si el LLM falla pero la red de seguridad detecta Riesgo inminente, devuelve igualmente una Categorización de RIESGO_INMINENTE.",
  responses={@ApiResponse(responseCode="200",description="Categorización del Reporte"),@ApiResponse(responseCode="400",description="Conversación no válida"),@ApiResponse(responseCode="401",description="Autenticación necesaria"),@ApiResponse(responseCode="503",description="LLM no disponible (CATEGORIZATION_UNAVAILABLE)")})
 @PostMapping("/reports/categorization") public CategorizationResponse categorize(@Valid @RequestBody ReportRequest request) { return categorization.categorize(request.getConversacion()); }

 @Operation(summary="Responder en el chat de apoyo", description="Genera una respuesta empática para el Informante. No presupone que la situación le ocurra a quien escribe y no crea un Reporte por sí sola.")
 @ApiResponse(responseCode="200",description="Respuesta del chatbot") @ApiResponse(responseCode="400",description="Conversación no válida") @ApiResponse(responseCode="503",description="Chatbot no disponible (CHATBOT_UNAVAILABLE)")
 @PostMapping("/reports/chat") public ChatResponse chat(@Valid @RequestBody ReportRequest request) { return chatbot.reply(request.getConversacion()); }

 @Operation(summary="Enviar un Reporte al centro", description="Persiste la conversación y crea una alerta interna con estado PENDIENTE_REVISION. La alerta no confirma automáticamente un Caso.")
 @ApiResponse(responseCode="201",description="Reporte guardado y alerta creada") @ApiResponse(responseCode="400",description="Conversación no válida") @ApiResponse(responseCode="401",description="Autenticación necesaria")
 @PostMapping("/reports") @ResponseStatus(HttpStatus.CREATED)
 public ReportResponse create(@AuthenticationPrincipal PuntoSeguroPrincipal principal, @Valid @RequestBody ReportRequest request) { return reports.create(principal.getId(), request); }

 @Operation(summary="Listar Reportes", description="El alumnado consulta sus propios Reportes; PROFESOR y CENTRO consultan los Reportes recibidos por el centro.")
 @GetMapping("/reports") public List<ReportResponse> list(@AuthenticationPrincipal PuntoSeguroPrincipal principal) { return reports.list(principal.getId(), principal.getRole()); }

 @Operation(summary="Consultar un Reporte")
 @GetMapping("/reports/{id}") public ReportResponse get(@PathVariable Integer id, @AuthenticationPrincipal PuntoSeguroPrincipal principal) { return reports.get(id, principal.getId(), principal.getRole()); }
}

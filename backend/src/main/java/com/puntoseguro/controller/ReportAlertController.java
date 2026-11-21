package com.puntoseguro.controller;

import com.puntoseguro.dto.AlertResponse;
import com.puntoseguro.security.PuntoSeguroPrincipal;
import com.puntoseguro.service.ReportAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReportAlertController {
    private final ReportAlertService alerts;

    @Operation(summary = "Listar alertas de Reportes", description = "Solo PROFESOR y CENTRO. Todas aparecen como PENDIENTE_REVISION hasta que el centro las revise.")
    @ApiResponse(responseCode = "200", description = "Alertas ordenadas de la más reciente a la más antigua")
    @ApiResponse(responseCode = "403", description = "Rol no permitido")
    @GetMapping
    public List<AlertResponse> list(@AuthenticationPrincipal PuntoSeguroPrincipal principal) {
        return alerts.list(principal.getRole());
    }
}

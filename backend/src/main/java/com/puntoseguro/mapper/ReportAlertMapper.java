package com.puntoseguro.mapper;

import com.puntoseguro.dto.AlertResponse;
import com.puntoseguro.entity.ReportAlertEntity;
import org.springframework.stereotype.Component;

@Component
public class ReportAlertMapper {
    public AlertResponse toResponse(ReportAlertEntity alert) {
        var report = alert.getReport();
        return new AlertResponse(alert.getId(), report.getId(), report.getEsAcoso(), alert.getNivelUrgencia(),
                alert.getEstado(), report.getResumen(), alert.getFechaCreacion());
    }
}

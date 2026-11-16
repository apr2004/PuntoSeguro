package com.puntoseguro.mapper;

import com.puntoseguro.dto.CategorizationResponse;
import com.puntoseguro.dto.ReportMessage;
import com.puntoseguro.dto.ReportResponse;
import com.puntoseguro.entity.ReportEntity;
import com.puntoseguro.entity.ReportMessageEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportMapper {
    public ReportResponse toResponse(ReportEntity report) {
        var categorization = new CategorizationResponse(report.getEsAcoso(), report.getTipoAcoso(),
                report.getRelacionInformante(), report.getReiteracion(), report.getNivelUrgencia(),
                report.getResumen(), report.getEvidence().stream().map(e -> e.getTexto()).toList());
        List<ReportMessage> conversation = report.getMessages().stream().map(this::toMessage).toList();
        return new ReportResponse(report.getId(), report.getFechaCreacion(), report.getEstadoRevision(),
                categorization, conversation);
    }

    private ReportMessage toMessage(ReportMessageEntity message) {
        return new ReportMessage(message.getRol(), message.getTexto());
    }
}

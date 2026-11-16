package com.puntoseguro.service;

import com.puntoseguro.dto.ReportRequest;
import com.puntoseguro.dto.ReportResponse;
import com.puntoseguro.entity.ReportEntity;
import com.puntoseguro.entity.ReportEvidenceEntity;
import com.puntoseguro.entity.ReportMessageEntity;
import com.puntoseguro.entity.ReportAlertEntity;
import com.puntoseguro.entity.UserEntity;
import com.puntoseguro.entity.enums.ReviewStatus;
import com.puntoseguro.entity.enums.UserRole;
import com.puntoseguro.mapper.ReportMapper;
import com.puntoseguro.repository.ReportAlertRepository;
import com.puntoseguro.repository.ReportRepository;
import com.puntoseguro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/** Persiste Reportes y crea una alerta interna pendiente de revisión. */
@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private final ReportRepository reports;
    private final ReportAlertRepository alerts;
    private final UserRepository users;
    private final ReportCategorizationService categorization;
    private final ReportMapper mapper;

    public ReportResponse create(UUID informantId, ReportRequest request) {
        UserEntity informant = users.findById(informantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicia sesión con una credencial válida."));
        var result = categorization.categorize(request.getConversacion());

        ReportEntity report = new ReportEntity();
        report.setInformant(informant);
        report.setFechaCreacion(LocalDateTime.now(ZoneOffset.UTC));
        report.setEsAcoso(result.getEsAcoso());
        report.setTipoAcoso(result.getTipoAcoso());
        report.setRelacionInformante(result.getRelacionInformante());
        report.setReiteracion(result.getReiteracion());
        report.setNivelUrgencia(result.getNivelUrgencia());
        report.setResumen(result.getResumen());
        report.setEstadoRevision(ReviewStatus.PENDIENTE_REVISION);

        for (int i = 0; i < request.getConversacion().size(); i++) {
            var source = request.getConversacion().get(i);
            var message = new ReportMessageEntity();
            message.setReport(report);
            message.setRol(source.getRol());
            message.setTexto(source.getTexto());
            message.setOrden(i);
            report.getMessages().add(message);
        }
        for (int i = 0; i < result.getIndicios().size(); i++) {
            var evidence = new ReportEvidenceEntity();
            evidence.setReport(report);
            evidence.setTexto(result.getIndicios().get(i));
            evidence.setOrden(i);
            report.getEvidence().add(evidence);
        }

        ReportEntity saved = reports.saveAndFlush(report);
        ReportAlertEntity alert = new ReportAlertEntity();
        alert.setReport(saved);
        alert.setNivelUrgencia(saved.getNivelUrgencia());
        alert.setEstado(ReviewStatus.PENDIENTE_REVISION);
        alert.setFechaCreacion(saved.getFechaCreacion());
        alerts.saveAndFlush(alert);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> list(UUID userId, UserRole role) {
        List<ReportEntity> found = role == UserRole.ESTUDIANTE
                ? reports.findByInformantIdOrderByFechaCreacionDescIdDesc(userId)
                : reports.findAllByOrderByFechaCreacionDescIdDesc();
        return found.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ReportResponse get(Integer id, UUID userId, UserRole role) {
        ReportEntity report = reports.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado"));
        if (role == UserRole.ESTUDIANTE && !report.getInformant().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para consultar este Reporte");
        }
        return mapper.toResponse(report);
    }
}

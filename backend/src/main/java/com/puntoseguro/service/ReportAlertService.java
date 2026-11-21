package com.puntoseguro.service;

import com.puntoseguro.dto.AlertResponse;
import com.puntoseguro.entity.enums.UserRole;
import com.puntoseguro.mapper.ReportAlertMapper;
import com.puntoseguro.repository.ReportAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportAlertService {
    private final ReportAlertRepository alerts;
    private final ReportAlertMapper mapper;

    @Transactional(readOnly = true)
    public List<AlertResponse> list(UserRole role) {
        if (role != UserRole.PROFESOR && role != UserRole.CENTRO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para consultar alertas");
        }
        return alerts.findAllByOrderByFechaCreacionDescIdDesc().stream().map(mapper::toResponse).toList();
    }
}

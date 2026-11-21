package com.puntoseguro.dto;

import com.puntoseguro.entity.enums.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/** DTO HTTP de un Reporte, sin exponer entidades JPA. */
@Getter
@AllArgsConstructor
public class ReportResponse {
    private Integer id;
    private LocalDateTime fechaCreacion;
    private ReviewStatus estadoRevision;
    private CategorizationResponse categorizacion;
    private List<ReportMessage> conversacion;
}

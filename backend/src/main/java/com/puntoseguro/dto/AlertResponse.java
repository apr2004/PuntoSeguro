package com.puntoseguro.dto;

import com.puntoseguro.entity.enums.HarassmentAssessment;
import com.puntoseguro.entity.enums.ReviewStatus;
import com.puntoseguro.entity.enums.UrgencyLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/** Alerta interna para el equipo del centro. No implica un Caso confirmado. */
@Getter
@AllArgsConstructor
public class AlertResponse {
    private Integer id;
    private Integer reportId;
    private HarassmentAssessment esAcoso;
    private UrgencyLevel nivelUrgencia;
    private ReviewStatus estado;
    private String resumen;
    private LocalDateTime fechaCreacion;
}

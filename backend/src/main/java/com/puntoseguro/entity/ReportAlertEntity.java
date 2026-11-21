package com.puntoseguro.entity;

import com.puntoseguro.entity.enums.ReviewStatus;
import com.puntoseguro.entity.enums.UrgencyLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Notificación interna para que el equipo del centro revise un Reporte. */
@Entity
@Table(name = "alerta_reporte")
@Getter
@Setter
public class ReportAlertEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reporte", unique = true)
    private ReportEntity report;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_urgencia", nullable = false, length = 30)
    private UrgencyLevel nivelUrgencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReviewStatus estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}

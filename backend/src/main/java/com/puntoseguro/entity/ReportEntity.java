package com.puntoseguro.entity;

import com.puntoseguro.entity.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Registro persistente de una conversación comunicada por un Informante. */
@Entity
@Table(name = "reporte")
@Getter
@Setter
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_informante")
    private UserEntity informant;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "es_acoso", nullable = false, length = 20)
    private HarassmentAssessment esAcoso;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acoso", nullable = false, length = 20)
    private HarassmentType tipoAcoso;

    @Enumerated(EnumType.STRING)
    @Column(name = "relacion_informante", nullable = false, length = 30)
    private InformantRelation relacionInformante;

    @Enumerated(EnumType.STRING)
    @Column(name = "reiteracion", nullable = false, length = 20)
    private Recurrence reiteracion;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_urgencia", nullable = false, length = 30)
    private UrgencyLevel nivelUrgencia;

    @Column(nullable = false, columnDefinition = "text")
    private String resumen;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_revision", nullable = false, length = 30)
    private ReviewStatus estadoRevision;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<ReportMessageEntity> messages = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<ReportEvidenceEntity> evidence = new ArrayList<>();
}

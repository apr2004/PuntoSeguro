package com.puntoseguro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reporte_indicio")
@Getter
@Setter
public class ReportEvidenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reporte")
    private ReportEntity report;

    @Column(nullable = false, columnDefinition = "text")
    private String texto;

    @Column(nullable = false)
    private Integer orden;
}

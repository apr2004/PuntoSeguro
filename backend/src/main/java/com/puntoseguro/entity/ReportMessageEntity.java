package com.puntoseguro.entity;

import com.puntoseguro.entity.enums.MessageRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reporte_mensaje")
@Getter
@Setter
public class ReportMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reporte")
    private ReportEntity report;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageRole rol;

    @Column(nullable = false, columnDefinition = "text")
    private String texto;

    @Column(nullable = false)
    private Integer orden;
}

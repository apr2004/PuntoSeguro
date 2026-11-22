package com.puntoseguro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** Versión inmutable del texto de una Observación, con el usuario que la registró. */
@Entity
@Table(name = "observacion_historial")
@Getter
@Setter
public class ObservationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_observacion")
    private Observation observation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_editor")
    private UserEntity editor;

    @Column(nullable = false, columnDefinition = "text")
    private String texto;

    @Column(name = "fecha_edicion", nullable = false)
    private LocalDateTime fechaEdicion;

    /** true si la versión se copió de una observación anterior a la existencia del historial. */
    @Column(nullable = false)
    private boolean importado;
}

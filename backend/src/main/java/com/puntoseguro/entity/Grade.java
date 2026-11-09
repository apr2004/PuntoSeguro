package com.puntoseguro.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime;
@Entity @Table(name="calificacion") @Getter @Setter public class Grade {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_alumno") private Student student;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_asignatura") private Subject subject;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_plataforma_origen") private SourcePlatform platform;
 @Column(nullable=false,precision=4,scale=2) private BigDecimal nota;
 @Column(nullable=false,length=50) private String evaluacion;
 @Column(nullable=false) private LocalDate fecha;
 @Column(name="fecha_sincronizacion",nullable=false) private LocalDateTime fechaSincronizacion;
 @Column(columnDefinition="text") private String observaciones;
}

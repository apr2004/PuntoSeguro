package com.puntoseguro.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter;
import java.time.LocalDateTime;
/** Observación: anotación de seguimiento de un docente sobre un alumno. Nunca constituye un Caso confirmado. */
@Entity @Table(name="observacion") @Getter @Setter public class Observation {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_alumno") private Student student;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_autor") private UserEntity author;
 @Column(nullable=false,columnDefinition="text") private String texto;
 @Column(name="fecha_creacion",nullable=false) private LocalDateTime fechaCreacion;
}

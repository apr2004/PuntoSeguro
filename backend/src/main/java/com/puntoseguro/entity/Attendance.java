package com.puntoseguro.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter;
import java.time.LocalDate; import java.time.LocalDateTime;
@Entity @Table(name="asistencia") @Getter @Setter public class Attendance {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_alumno") private Student student;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_asignatura") private Subject subject;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_tipo_asistencia") private AttendanceType type;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_plataforma_origen") private SourcePlatform platform;
 @Column(nullable=false) private LocalDate dia;
 @Column(name="fecha_sincronizacion",nullable=false) private LocalDateTime fechaSincronizacion;
 @Column(columnDefinition="text") private String observaciones;
}

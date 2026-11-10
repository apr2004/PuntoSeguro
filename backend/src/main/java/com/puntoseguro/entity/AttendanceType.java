package com.puntoseguro.entity;
import jakarta.persistence.*; import lombok.Getter; import lombok.Setter;
@Entity @Table(name="tipo_asistencia") @Getter @Setter public class AttendanceType {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(nullable=false,length=50) private String nombre;
}

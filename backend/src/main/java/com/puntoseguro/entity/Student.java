package com.puntoseguro.entity;
import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
@Entity @Table(name="alumno") @Getter @Setter public class Student {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @Column(nullable=false,length=100) private String nombre;
 @Column(nullable=false,length=150) private String apellidos;
 @Column(length=20) private String curso;
 @Column(length=10) private String grupo;
}

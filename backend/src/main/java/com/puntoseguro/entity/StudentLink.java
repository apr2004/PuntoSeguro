package com.puntoseguro.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity @Table(name="vinculo_alumno") @Getter @Setter
public class StudentLink {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_alumno") private Student student;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="id_usuario") private UserEntity user;
}

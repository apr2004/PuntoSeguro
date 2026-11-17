package com.puntoseguro.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** Asignación expresa del centro para el seguimiento de un alumno. */
@Entity @Table(name = "asignacion_docente", uniqueConstraints = @UniqueConstraint(columnNames = {"id_alumno", "id_docente"}))
@Getter @Setter public class TeacherAssignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "id_alumno") private Student student;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "id_docente") private UserEntity teacher;
}

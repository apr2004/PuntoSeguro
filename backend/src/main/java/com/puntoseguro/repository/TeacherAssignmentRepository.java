package com.puntoseguro.repository;

import com.puntoseguro.entity.TeacherAssignment;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Integer> {
    boolean existsByStudentIdAndTeacherId(Integer studentId, UUID teacherId);
    Optional<TeacherAssignment> findByStudentIdAndTeacherId(Integer studentId, UUID teacherId);
    @Query("SELECT a FROM TeacherAssignment a JOIN FETCH a.teacher WHERE a.student.id = :studentId ORDER BY a.teacher.nombre")
    List<TeacherAssignment> findByStudentWithTeacher(@Param("studentId") Integer studentId);
}

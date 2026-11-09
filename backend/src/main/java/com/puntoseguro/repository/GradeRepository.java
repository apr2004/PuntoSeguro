package com.puntoseguro.repository;
import com.puntoseguro.entity.Grade; import org.springframework.data.jpa.repository.JpaRepository;
public interface GradeRepository extends JpaRepository<Grade, Integer> { java.util.List<Grade> findByStudentIdOrderByIdDesc(Integer studentId);
 java.util.Optional<Grade> findByIdAndStudentId(Integer id,Integer studentId); }

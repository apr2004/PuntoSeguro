package com.puntoseguro.repository;
import com.puntoseguro.entity.Student; import org.springframework.data.jpa.repository.JpaRepository;
public interface StudentRepository extends JpaRepository<Student, Integer> {  }

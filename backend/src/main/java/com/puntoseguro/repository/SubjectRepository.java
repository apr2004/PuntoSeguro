package com.puntoseguro.repository;
import com.puntoseguro.entity.Subject; import org.springframework.data.jpa.repository.JpaRepository;
public interface SubjectRepository extends JpaRepository<Subject, Integer> {  }

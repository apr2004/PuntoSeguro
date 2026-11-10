package com.puntoseguro.repository;
import com.puntoseguro.entity.AttendanceType; import org.springframework.data.jpa.repository.JpaRepository;
public interface AttendanceTypeRepository extends JpaRepository<AttendanceType, Integer> {  }

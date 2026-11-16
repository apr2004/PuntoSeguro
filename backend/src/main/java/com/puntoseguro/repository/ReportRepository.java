package com.puntoseguro.repository;

import com.puntoseguro.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {
    List<ReportEntity> findByInformantIdOrderByFechaCreacionDescIdDesc(java.util.UUID informantId);
    List<ReportEntity> findAllByOrderByFechaCreacionDescIdDesc();
}

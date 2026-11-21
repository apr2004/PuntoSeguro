package com.puntoseguro.repository;

import com.puntoseguro.entity.ReportAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportAlertRepository extends JpaRepository<ReportAlertEntity, Integer> {
    List<ReportAlertEntity> findAllByOrderByFechaCreacionDescIdDesc();
}

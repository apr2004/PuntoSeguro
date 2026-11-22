package com.puntoseguro.repository;

import com.puntoseguro.entity.ObservationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObservationHistoryRepository extends JpaRepository<ObservationHistory, Integer> {
    @Query("SELECT h FROM ObservationHistory h JOIN FETCH h.editor WHERE h.observation.id = :observationId ORDER BY h.fechaEdicion ASC, h.id ASC")
    List<ObservationHistory> findByObservationIdOldestFirst(@Param("observationId") Integer observationId);
}

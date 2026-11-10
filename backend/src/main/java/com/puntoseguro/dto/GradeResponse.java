package com.puntoseguro.dto;
import java.math.BigDecimal; import java.time.*; import lombok.AllArgsConstructor; import lombok.Getter;
@Getter @AllArgsConstructor public class GradeResponse { private Integer id; private CatalogResponse asignatura, plataforma; private BigDecimal nota; private String evaluacion; private LocalDate fecha; private LocalDateTime fechaSincronizacion; private String observaciones; }

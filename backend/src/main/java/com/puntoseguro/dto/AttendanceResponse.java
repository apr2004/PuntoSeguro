package com.puntoseguro.dto;
import java.time.*; import lombok.AllArgsConstructor; import lombok.Getter;
@Getter @AllArgsConstructor public class AttendanceResponse { private Integer id; private CatalogResponse asignatura, tipo, plataforma; private LocalDate dia; private LocalDateTime fechaSincronizacion; private String observaciones; }

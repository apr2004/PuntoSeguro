package com.puntoseguro.dto;
import java.time.LocalDate; import jakarta.validation.constraints.*; import lombok.Getter; import lombok.Setter;
@Getter @Setter public class AttendanceRequest {
 private Integer asignaturaId;
 @NotNull private Integer tipoId;
 @NotNull private Integer plataformaId;
 @NotNull private LocalDate dia;
 private String observaciones;
}

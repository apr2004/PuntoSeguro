package com.puntoseguro.dto;
import java.math.BigDecimal; import java.time.LocalDate; import jakarta.validation.constraints.*; import lombok.Getter; import lombok.Setter;
@Getter @Setter public class GradeRequest {
 @NotNull private Integer asignaturaId;
 @NotNull private Integer plataformaId;
 @NotNull @DecimalMin("0") @DecimalMax("10") @Digits(integer=2,fraction=2) private BigDecimal nota;
 @NotBlank @Size(max=50) private String evaluacion;
 @NotNull private LocalDate fecha;
 private String observaciones;
}

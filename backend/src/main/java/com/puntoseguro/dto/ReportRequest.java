package com.puntoseguro.dto;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import lombok.Getter; import lombok.Setter; import java.util.List;
/** Un Reporte es la conversación completa: se vuelve a categorizar con cada mensaje nuevo. */
@Getter @Setter public class ReportRequest {
 @NotEmpty @Size(max=100) private List<@NotNull @Valid ReportMessage> conversacion;
}

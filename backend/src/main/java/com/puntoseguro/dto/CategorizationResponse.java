package com.puntoseguro.dto;
import com.puntoseguro.entity.enums.*; import jakarta.validation.constraints.*; import lombok.*; import java.util.List;
/** Categorización orientativa de un Reporte. Nunca es un diagnóstico. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode @ToString public class CategorizationResponse {
 @NotNull private HarassmentAssessment esAcoso;
 @NotNull private HarassmentType tipoAcoso;
 @NotNull private InformantRelation relacionInformante;
 @NotNull private Recurrence reiteracion;
 @NotNull private UrgencyLevel nivelUrgencia;
 @NotBlank private String resumen;
 @NotNull private List<@NotNull String> indicios;
}

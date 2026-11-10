package com.puntoseguro.dto;
import com.puntoseguro.entity.enums.MessageRole; import jakarta.validation.constraints.*; import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor public class ReportMessage {
 @NotNull private MessageRole rol;
 @NotBlank @Size(max=4000) private String texto;
}

package com.puntoseguro.dto;
import java.time.LocalDateTime; import lombok.AllArgsConstructor; import lombok.Getter;
@Getter @AllArgsConstructor public class ObservationResponse { private Integer id; private String texto; private AuthorResponse autor; private LocalDateTime fechaCreacion; }

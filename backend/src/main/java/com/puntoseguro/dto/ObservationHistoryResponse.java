package com.puntoseguro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ObservationHistoryResponse {
    private Integer id;
    private String texto;
    private AuthorResponse autor;
    private LocalDateTime fechaEdicion;
    private boolean importado;
}

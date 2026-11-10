package com.puntoseguro.dto;
import jakarta.validation.constraints.*; import lombok.Getter;
/** Solo el texto: el autor y la fecha los pone la plataforma. Cualquier otro campo del cuerpo se ignora. */
@Getter public class ObservationRequest {
 @NotBlank @Size(max=2000) private String texto;
 /** Se recorta al recibirlo, así la longitud máxima no cuenta los espacios del principio y del final. */
 public void setTexto(String texto) { this.texto = texto == null ? null : texto.strip(); }
}

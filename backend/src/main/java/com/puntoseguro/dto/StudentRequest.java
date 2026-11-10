package com.puntoseguro.dto;
import jakarta.validation.constraints.*; import lombok.Getter; import lombok.Setter;
@Getter @Setter public class StudentRequest {
 @NotBlank @Size(max=100) private String nombre;
 @NotBlank @Size(max=150) private String apellidos;
 @Size(max=20) private String curso;
 @Size(max=10) private String grupo;
}

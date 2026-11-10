package com.puntoseguro.dto;
import jakarta.validation.constraints.*;
import lombok.Getter; import lombok.Setter;
@Getter @Setter public class RegisterRequest {
    @NotBlank @Size(max=100) private String nombre;
    @NotBlank @Email @Size(max=254) private String email;
    @NotBlank @Size(min=8,max=72) private String password;
}

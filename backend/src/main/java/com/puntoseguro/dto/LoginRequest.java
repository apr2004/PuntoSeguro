package com.puntoseguro.dto;
import jakarta.validation.constraints.*;
import lombok.Getter; import lombok.Setter;
@Getter @Setter public class LoginRequest {
    @NotBlank @Email @Size(max=254) private String email;
    @NotBlank @Size(max=72) private String password;
}

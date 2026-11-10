package com.puntoseguro.dto;
import lombok.AllArgsConstructor; import lombok.Getter;
@Getter @AllArgsConstructor public class AuthResponse {
    private String accessToken; private String tokenType; private int expiresIn; private UserResponse user;
}

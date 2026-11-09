package com.puntoseguro.dto;
import com.puntoseguro.entity.enums.UserRole;
import java.util.UUID;
import lombok.AllArgsConstructor; import lombok.Getter;
@Getter @AllArgsConstructor public class UserResponse {
    private UUID id; private String nombre; private String email; private UserRole role;
}

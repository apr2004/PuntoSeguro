package com.puntoseguro.mapper;
import com.puntoseguro.entity.UserEntity;
import com.puntoseguro.dto.UserResponse;
import org.springframework.stereotype.Component;
@Component public class UserMapper {
    public UserResponse toDTO(UserEntity user) { return new UserResponse(user.getId(), user.getNombre(), user.getEmail(), user.getRole()); }
}

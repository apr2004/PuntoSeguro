package com.puntoseguro.dto;
import com.puntoseguro.entity.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter public class RoleRequest { @NotNull private UserRole role; }

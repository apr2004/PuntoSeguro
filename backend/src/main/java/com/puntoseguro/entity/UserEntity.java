package com.puntoseguro.entity;
import com.puntoseguro.entity.enums.UserRole;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "usuario") @Getter @Setter
public class UserEntity {
    @Id private UUID id;
    @Column(nullable=false, length=100) private String nombre;
    @Column(nullable=false, length=254) private String email;
    @Column(name="password_hash", nullable=false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private UserRole role;
    @Column(nullable=false) private boolean activo;
}

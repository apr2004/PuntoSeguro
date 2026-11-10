package com.puntoseguro.config;
import com.puntoseguro.entity.UserEntity;
import com.puntoseguro.entity.enums.UserRole;
import com.puntoseguro.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration @Profile("dev") @RequiredArgsConstructor public class DevUsers {
    private final UserRepository users; private final PasswordEncoder encoder;
    @Bean CommandLineRunner seedUsers() { return args -> {
        for (UserRole role : UserRole.values()) {
            String email=role.name().toLowerCase(java.util.Locale.ROOT)+"@puntoseguro.local";
            if (users.findByEmailIgnoreCase(email).isEmpty()) {
                UserEntity u=new UserEntity(); u.setId(UUID.randomUUID()); u.setNombre("Usuario " + role.name().toLowerCase(java.util.Locale.ROOT)); u.setEmail(email); u.setPasswordHash(encoder.encode("puntoseguro")); u.setRole(role); u.setActivo(true); users.save(u);
            }
        }
    }; }
}

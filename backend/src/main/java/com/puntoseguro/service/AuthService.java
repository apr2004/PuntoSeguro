package com.puntoseguro.service;
import com.puntoseguro.dto.*;
import com.puntoseguro.entity.UserEntity;
import com.puntoseguro.entity.enums.UserRole;
import com.puntoseguro.mapper.UserMapper;
import com.puntoseguro.repository.UserRepository;
import com.puntoseguro.security.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service @RequiredArgsConstructor public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt; private final UserMapper mapper;
    @Transactional(readOnly=true) public AuthResponse login(LoginRequest request) {
        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) throw new BadCredentialsException("Credenciales no validas");
        UserEntity user = users.findByEmailIgnoreCase(request.getEmail().trim()).filter(UserEntity::isActivo)
            .filter(u -> encoder.matches(request.getPassword(), u.getPasswordHash())).orElseThrow(() -> new BadCredentialsException("Credenciales no validas"));
        return new AuthResponse(jwt.issue(user.getId(), user.getRole()), "Bearer", jwt.expiresIn(), mapper.toDTO(user));
    }
    @Transactional public UserResponse register(RegisterRequest request) {
        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es demasiado larga");
        String email=request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (users.findByEmailIgnoreCase(email).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Correo ya registrado");
        UserEntity user=new UserEntity(); user.setId(UUID.randomUUID()); user.setNombre(request.getNombre().trim()); user.setEmail(email); user.setPasswordHash(encoder.encode(request.getPassword())); user.setRole(UserRole.ESTUDIANTE); user.setActivo(true);
        try { return mapper.toDTO(users.saveAndFlush(user)); } catch (DataIntegrityViolationException e) { throw new ResponseStatusException(HttpStatus.CONFLICT, "Correo ya registrado"); }
    }
    @Transactional(readOnly=true) public UserResponse current(PuntoSeguroPrincipal principal) {
        UserEntity user=users.findById(principal.getId()).filter(UserEntity::isActivo).filter(u -> u.getRole()==principal.getRole()).orElseThrow(() -> new BadCredentialsException("Credencial no valida"));
        return mapper.toDTO(user);
    }
}

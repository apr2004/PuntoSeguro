package com.puntoseguro.repository;
import com.puntoseguro.entity.UserEntity;
import com.puntoseguro.entity.enums.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    List<UserEntity> findByRoleAndActivoTrueOrderByNombre(UserRole role);
}

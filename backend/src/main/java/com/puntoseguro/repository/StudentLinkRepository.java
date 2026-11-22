package com.puntoseguro.repository;
import com.puntoseguro.entity.StudentLink;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StudentLinkRepository extends JpaRepository<StudentLink,Integer> {
 boolean existsByStudentIdAndUserId(Integer studentId, UUID userId);
 Optional<StudentLink> findByStudentIdAndUserId(Integer studentId, UUID userId);
 boolean existsByUserId(UUID userId);
 void deleteByUserId(UUID userId);
 @Query("SELECT l FROM StudentLink l JOIN FETCH l.user WHERE l.student.id = :sid")
 List<StudentLink> findForStudent(@Param("sid") Integer sid);
}

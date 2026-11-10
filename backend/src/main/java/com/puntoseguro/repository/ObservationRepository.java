package com.puntoseguro.repository;
import com.puntoseguro.entity.Observation; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.List;
public interface ObservationRepository extends JpaRepository<Observation, Integer> {
 @Query("SELECT o FROM Observation o JOIN FETCH o.author WHERE o.student.id = :studentId ORDER BY o.fechaCreacion DESC, o.id DESC")
 List<Observation> findByStudentNewestFirst(@Param("studentId") Integer studentId);
}

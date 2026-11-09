package com.puntoseguro.repository;
import com.puntoseguro.entity.Attendance; import org.springframework.data.jpa.repository.JpaRepository;
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> { java.util.List<Attendance> findByStudentIdOrderByIdDesc(Integer studentId);
 java.util.Optional<Attendance> findByIdAndStudentId(Integer id,Integer studentId); }

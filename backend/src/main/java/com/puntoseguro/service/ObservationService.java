package com.puntoseguro.service;
import com.puntoseguro.dto.ObservationRequest; import com.puntoseguro.dto.ObservationResponse; import com.puntoseguro.entity.*; import com.puntoseguro.mapper.ObservationMapper; import com.puntoseguro.repository.*;
import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime; import java.time.ZoneOffset; import java.util.List; import java.util.UUID;
@Service @RequiredArgsConstructor @Transactional public class ObservationService {
 private final ObservationRepository observations; private final StudentRepository students; private final UserRepository users; private final ObservationMapper mapper;
 private Student student(Integer id) { return students.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Estudiante no encontrado")); }
 @Transactional(readOnly=true) public List<ObservationResponse> list(Integer sid) { student(sid); return observations.findByStudentNewestFirst(sid).stream().map(mapper::toDTO).toList(); }
 /** El autor es siempre el usuario autenticado; la fecha de registro la pone la plataforma. */
 public ObservationResponse create(Integer sid,UUID authorId,ObservationRequest r) {
  Observation o=new Observation(); o.setStudent(student(sid));
  o.setAuthor(users.findById(authorId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Inicia sesión con una credencial válida.")));
  o.setTexto(r.getTexto()); o.setFechaCreacion(LocalDateTime.now(ZoneOffset.UTC));
  return mapper.toDTO(observations.saveAndFlush(o));
 }
}

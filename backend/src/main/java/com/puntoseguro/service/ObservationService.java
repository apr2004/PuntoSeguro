package com.puntoseguro.service;
import com.puntoseguro.dto.ObservationHistoryResponse; import com.puntoseguro.dto.ObservationRequest; import com.puntoseguro.dto.ObservationResponse; import com.puntoseguro.entity.*; import com.puntoseguro.mapper.ObservationMapper; import com.puntoseguro.repository.*;
import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime; import java.time.ZoneOffset; import java.util.List; import java.util.UUID;
@Service @RequiredArgsConstructor @Transactional public class ObservationService {
 private final ObservationRepository observations; private final ObservationHistoryRepository history; private final StudentRepository students; private final UserRepository users; private final ObservationMapper mapper;
 private Student student(Integer id) { return students.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Estudiante no encontrado")); }
 private Observation observation(Integer sid,Integer id) { student(sid); return observations.findByIdAndStudentId(id,sid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Observación no encontrada")); }
 @Transactional(readOnly=true) public List<ObservationResponse> list(Integer sid) { student(sid); return observations.findByStudentNewestFirst(sid).stream().map(mapper::toDTO).toList(); }
 @Transactional(readOnly=true) public List<ObservationHistoryResponse> history(Integer sid,Integer id) { return history.findByObservationIdOldestFirst(observation(sid,id).getId()).stream().map(mapper::historyToDTO).toList(); }
 /** El autor es siempre el usuario autenticado; la fecha de registro la pone la plataforma. */
 public ObservationResponse create(Integer sid,UUID authorId,ObservationRequest r) {
  Observation o=new Observation(); o.setStudent(student(sid));
  o.setAuthor(users.findById(authorId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Inicia sesión con una credencial válida.")));
  LocalDateTime now=LocalDateTime.now(ZoneOffset.UTC); o.setTexto(r.getTexto()); o.setFechaCreacion(now);
  Observation saved=observations.saveAndFlush(o); saveHistory(saved,authorId,now); return mapper.toDTO(saved);
 }
 public ObservationResponse update(Integer sid,Integer id,UUID editorId,ObservationRequest r) {
  Observation o=observation(sid,id); o.setTexto(r.getTexto());
  Observation saved=observations.saveAndFlush(o); saveHistory(saved,editorId,LocalDateTime.now(ZoneOffset.UTC)); return mapper.toDTO(saved);
 }
 private void saveHistory(Observation observation,UUID editorId,LocalDateTime date) {
  ObservationHistory version=new ObservationHistory(); version.setObservation(observation);
  version.setEditor(users.findById(editorId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Inicia sesión con una credencial válida.")));
  version.setTexto(observation.getTexto()); version.setFechaEdicion(date); history.saveAndFlush(version);
 }
}

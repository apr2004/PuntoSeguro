package com.puntoseguro.service;

import com.puntoseguro.dto.ObservationHistoryResponse;
import com.puntoseguro.dto.ObservationRequest;
import com.puntoseguro.dto.ObservationResponse;
import com.puntoseguro.dto.AuthorResponse;
import com.puntoseguro.entity.Observation;
import com.puntoseguro.entity.ObservationHistory;
import com.puntoseguro.entity.Student;
import com.puntoseguro.entity.TeacherAssignment;
import com.puntoseguro.entity.UserEntity;
import com.puntoseguro.entity.enums.UserRole;
import com.puntoseguro.mapper.ObservationMapper;
import com.puntoseguro.repository.ObservationHistoryRepository;
import com.puntoseguro.repository.ObservationRepository;
import com.puntoseguro.repository.StudentRepository;
import com.puntoseguro.repository.TeacherAssignmentRepository;
import com.puntoseguro.repository.UserRepository;
import com.puntoseguro.security.PuntoSeguroPrincipal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service @RequiredArgsConstructor @Transactional
public class ObservationService {
    private final ObservationRepository observations;
    private final ObservationHistoryRepository history;
    private final StudentRepository students;
    private final UserRepository users;
    private final TeacherAssignmentRepository assignments;
    private final ObservationMapper mapper;

    private Student student(Integer id) {
        return students.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
    }

    private Observation observation(Integer sid, Integer id) {
        student(sid);
        return observations.findByIdAndStudentId(id, sid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Observación no encontrada"));
    }

    private UserEntity authorizedUser(PuntoSeguroPrincipal principal) {
        UserEntity user = users.findById(principal.getId()).orElseThrow(() -> new AccessDeniedException("Usuario sin acceso"));
        if (!user.isActivo() || user.getRole() != principal.getRole()) throw new AccessDeniedException("Usuario sin acceso");
        return user;
    }

    private void canRead(Integer sid, PuntoSeguroPrincipal principal) {
        UserEntity user = authorizedUser(principal);
        if (user.getRole() != UserRole.CENTRO &&
                (user.getRole() != UserRole.PROFESOR || !assignments.existsByStudentIdAndTeacherId(sid, user.getId()))) {
            throw new AccessDeniedException("No tienes permiso para consultar estas observaciones");
        }
    }

    private UserEntity canWrite(Integer sid, PuntoSeguroPrincipal principal) {
        UserEntity user = authorizedUser(principal);
        if (user.getRole() != UserRole.PROFESOR || !assignments.existsByStudentIdAndTeacherId(sid, user.getId())) {
            throw new AccessDeniedException("No tienes permiso para escribir estas observaciones");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public List<ObservationResponse> list(Integer sid, PuntoSeguroPrincipal principal) {
        student(sid);
        canRead(sid, principal);
        return observations.findByStudentNewestFirst(sid).stream().map(mapper::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ObservationHistoryResponse> history(Integer sid, Integer id, PuntoSeguroPrincipal principal) {
        canRead(sid, principal);
        Observation observation = observation(sid, id);
        return history.findByObservationIdOldestFirst(observation.getId()).stream().map(mapper::historyToDTO).toList();
    }

    /** El autor es siempre el usuario autenticado; la fecha de registro la pone la plataforma. */
    public ObservationResponse create(Integer sid, PuntoSeguroPrincipal principal, ObservationRequest request) {
        Student student = student(sid);
        UserEntity author = canWrite(sid, principal);
        Observation observation = new Observation();
        observation.setStudent(student);
        observation.setAuthor(author);
        observation.setTexto(request.getTexto());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS);
        observation.setFechaCreacion(now);
        Observation saved = observations.saveAndFlush(observation);
        saveHistory(saved, author, now);
        return mapper.toDTO(saved);
    }

    /** El autor puede corregir su texto mientras siga asignado; el centro puede corregir cualquier observación. */
    public ObservationResponse update(Integer sid, Integer id, PuntoSeguroPrincipal principal, ObservationRequest request) {
        student(sid);
        canRead(sid, principal);
        Observation observation = observations.findByIdAndStudentId(id, sid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Observación no encontrada"));
        if (principal.getRole() == UserRole.PROFESOR && !observation.getAuthor().getId().equals(principal.getId())) {
            throw new AccessDeniedException("No puedes modificar la observación de otro docente");
        }
        observation.setTexto(request.getTexto());
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS);
        Observation saved = observations.saveAndFlush(observation);
        saveHistory(saved, users.getReferenceById(principal.getId()), now);
        return mapper.toDTO(saved);
    }

    private void saveHistory(Observation observation, UserEntity editor, LocalDateTime date) {
        ObservationHistory version = new ObservationHistory();
        version.setObservation(observation);
        version.setEditor(editor);
        version.setTexto(observation.getTexto());
        version.setFechaEdicion(date);
        history.saveAndFlush(version);
    }

    public void assign(Integer sid, UUID teacherId, PuntoSeguroPrincipal principal) {
        requireCentre(principal);
        Student student = student(sid);
        UserEntity teacher = users.findById(teacherId)
                .filter(user -> user.isActivo() && user.getRole() == UserRole.PROFESOR)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado"));
        if (assignments.existsByStudentIdAndTeacherId(sid, teacherId)) return;
        TeacherAssignment assignment = new TeacherAssignment();
        assignment.setStudent(student);
        assignment.setTeacher(teacher);
        assignments.saveAndFlush(assignment);
    }

    public void unassign(Integer sid, UUID teacherId, PuntoSeguroPrincipal principal) {
        requireCentre(principal);
        student(sid);
        assignments.findByStudentIdAndTeacherId(sid, teacherId).ifPresent(assignments::delete);
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> teachers(PuntoSeguroPrincipal principal) {
        requireCentre(principal);
        return users.findByRoleAndActivoTrueOrderByNombre(UserRole.PROFESOR).stream()
                .map(user -> new AuthorResponse(user.getId(), user.getNombre())).toList();
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> assignedTeachers(Integer sid, PuntoSeguroPrincipal principal) {
        requireCentre(principal);
        student(sid);
        return assignments.findByStudentWithTeacher(sid).stream()
                .map(assignment -> new AuthorResponse(assignment.getTeacher().getId(), assignment.getTeacher().getNombre())).toList();
    }

    private void requireCentre(PuntoSeguroPrincipal principal) {
        if (authorizedUser(principal).getRole() != UserRole.CENTRO) {
            throw new AccessDeniedException("Solo el centro puede gestionar las asignaciones");
        }
    }
}

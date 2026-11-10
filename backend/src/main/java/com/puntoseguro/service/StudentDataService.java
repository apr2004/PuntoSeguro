package com.puntoseguro.service;
import com.puntoseguro.dto.*; import com.puntoseguro.entity.*; import com.puntoseguro.repository.*; import com.puntoseguro.mapper.StudentDataMapper;
import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime; import java.time.ZoneOffset; import java.util.List;
@Service @RequiredArgsConstructor @Transactional public class StudentDataService {
 private final StudentRepository students; private final SubjectRepository subjects; private final SourcePlatformRepository platforms; private final AttendanceTypeRepository types; private final GradeRepository grades; private final AttendanceRepository attendance; private final StudentDataMapper mapper;
 private ResponseStatusException missing(String what) { return new ResponseStatusException(HttpStatus.NOT_FOUND,what+" no encontrado"); }
 private Student student(Integer id) { return students.findById(id).orElseThrow(() -> missing("Estudiante")); }
 private Subject subject(Integer id) { return subjects.findById(id).orElseThrow(() -> missing("Asignatura")); }
 private SourcePlatform platform(Integer id) { return platforms.findById(id).orElseThrow(() -> missing("Plataforma")); }
 private AttendanceType type(Integer id) { return types.findById(id).orElseThrow(() -> missing("Tipo de asistencia")); }
 private Grade grade(Integer sid,Integer id) { return grades.findByIdAndStudentId(id,sid).orElseThrow(() -> missing("Calificación")); }
 private Attendance attendance(Integer sid,Integer id) { return attendance.findByIdAndStudentId(id,sid).orElseThrow(() -> missing("Asistencia")); }
 @Transactional(readOnly=true) public List<StudentResponse> listStudents() { return students.findAll().stream().map(mapper::student).toList(); }
 @Transactional(readOnly=true) public StudentResponse getStudent(Integer id) { return mapper.student(student(id)); }
 public StudentResponse saveStudent(Integer id,StudentRequest r) { Student s=id==null?new Student():student(id); s.setNombre(r.getNombre().trim()); s.setApellidos(r.getApellidos().trim()); s.setCurso(r.getCurso()); s.setGrupo(r.getGrupo()); return mapper.student(students.saveAndFlush(s)); }
 public void deleteStudent(Integer id) { students.delete(student(id)); students.flush(); }
 @Transactional(readOnly=true) public List<CatalogResponse> subjects() { return subjects.findAll().stream().map(mapper::subject).toList(); }
 @Transactional(readOnly=true) public List<CatalogResponse> platforms() { return platforms.findAll().stream().map(mapper::platform).toList(); }
 @Transactional(readOnly=true) public List<CatalogResponse> types() { return types.findAll().stream().map(mapper::type).toList(); }
 public CatalogResponse addSubject(CatalogRequest r) { Subject s=new Subject(); s.setNombre(r.getNombre().trim()); return mapper.subject(subjects.saveAndFlush(s)); }
 public CatalogResponse addPlatform(CatalogRequest r) { SourcePlatform s=new SourcePlatform(); s.setNombre(r.getNombre().trim()); return mapper.platform(platforms.saveAndFlush(s)); }
 public CatalogResponse addType(CatalogRequest r) { AttendanceType s=new AttendanceType(); s.setNombre(r.getNombre().trim()); return mapper.type(types.saveAndFlush(s)); }
 @Transactional(readOnly=true) public List<GradeResponse> grades(Integer sid) { student(sid); return grades.findByStudentIdOrderByIdDesc(sid).stream().map(mapper::grade).toList(); }
 @Transactional(readOnly=true) public GradeResponse gradeDetail(Integer sid,Integer id) { return mapper.grade(grade(sid,id)); }
 public GradeResponse saveGrade(Integer sid,Integer id,GradeRequest r) { Student s=student(sid); Grade g=id==null?new Grade():grade(sid,id); g.setStudent(s); g.setSubject(subject(r.getAsignaturaId())); g.setPlatform(platform(r.getPlataformaId())); g.setNota(r.getNota()); g.setEvaluacion(r.getEvaluacion().trim()); g.setFecha(r.getFecha()); g.setObservaciones(r.getObservaciones()); g.setFechaSincronizacion(LocalDateTime.now(ZoneOffset.UTC)); return mapper.grade(grades.saveAndFlush(g)); }
 public void deleteGrade(Integer sid,Integer id) { grades.delete(grade(sid,id)); grades.flush(); }
 @Transactional(readOnly=true) public List<AttendanceResponse> attendance(Integer sid) { student(sid); return attendance.findByStudentIdOrderByIdDesc(sid).stream().map(mapper::attendance).toList(); }
 @Transactional(readOnly=true) public AttendanceResponse attendanceDetail(Integer sid,Integer id) { return mapper.attendance(attendance(sid,id)); }
 public AttendanceResponse saveAttendance(Integer sid,Integer id,AttendanceRequest r) { Student s=student(sid); Attendance a=id==null?new Attendance():attendance(sid,id); a.setStudent(s); a.setSubject(r.getAsignaturaId()==null?null:subject(r.getAsignaturaId())); a.setType(type(r.getTipoId())); a.setPlatform(platform(r.getPlataformaId())); a.setDia(r.getDia()); a.setObservaciones(r.getObservaciones()); a.setFechaSincronizacion(LocalDateTime.now(ZoneOffset.UTC)); return mapper.attendance(attendance.saveAndFlush(a)); }
 public void deleteAttendance(Integer sid,Integer id) { attendance.delete(attendance(sid,id)); attendance.flush(); }
}

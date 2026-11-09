package com.puntoseguro.mapper;
import com.puntoseguro.dto.*; import com.puntoseguro.entity.*; import org.springframework.stereotype.Component;
@Component public class StudentDataMapper {
 public StudentResponse student(Student s) { return new StudentResponse(s.getId(),s.getNombre(),s.getApellidos(),s.getCurso(),s.getGrupo()); }
 public CatalogResponse subject(Subject s) { return s==null?null:new CatalogResponse(s.getId(),s.getNombre()); }
 public CatalogResponse platform(SourcePlatform s) { return new CatalogResponse(s.getId(),s.getNombre()); }
 public CatalogResponse type(AttendanceType s) { return new CatalogResponse(s.getId(),s.getNombre()); }
 public GradeResponse grade(Grade g) { return new GradeResponse(g.getId(),subject(g.getSubject()),platform(g.getPlatform()),g.getNota(),g.getEvaluacion(),g.getFecha(),g.getFechaSincronizacion(),g.getObservaciones()); }
 public AttendanceResponse attendance(Attendance a) { return new AttendanceResponse(a.getId(),subject(a.getSubject()),type(a.getType()),platform(a.getPlatform()),a.getDia(),a.getFechaSincronizacion(),a.getObservaciones()); }
}

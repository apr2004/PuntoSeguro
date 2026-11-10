package com.puntoseguro.controller;
import com.puntoseguro.dto.*; import com.puntoseguro.service.StudentDataService; import io.swagger.v3.oas.annotations.*; import io.swagger.v3.oas.annotations.responses.ApiResponse; import io.swagger.v3.oas.annotations.security.SecurityRequirement; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api") @RequiredArgsConstructor @SecurityRequirement(name="bearerAuth")
@ApiResponse(responseCode="400",description="Datos no válidos") @ApiResponse(responseCode="401",description="Autenticación necesaria") @ApiResponse(responseCode="404",description="Recurso no encontrado") @ApiResponse(responseCode="409",description="Conflicto con datos existentes") @ApiResponse(responseCode="503",description="Base de datos no disponible")
public class StudentDataController {
 private final StudentDataService data;
 @Operation(summary="Listar estudiantes") @GetMapping("/students") public List<StudentResponse> students() { return data.listStudents(); }
 @Operation(summary="Consultar estudiante") @GetMapping("/students/{sid}") public StudentResponse student(@PathVariable Integer sid) { return data.getStudent(sid); }
 @Operation(summary="Crear estudiante") @ApiResponse(responseCode="201",description="Estudiante creado") @PostMapping("/students") @ResponseStatus(HttpStatus.CREATED) public StudentResponse createStudent(@Valid @RequestBody StudentRequest r) { return data.saveStudent(null,r); }
 @Operation(summary="Editar estudiante") @PutMapping("/students/{sid}") public StudentResponse updateStudent(@PathVariable Integer sid,@Valid @RequestBody StudentRequest r) { return data.saveStudent(sid,r); }
 @Operation(summary="Eliminar estudiante sin datos asociados") @ApiResponse(responseCode="204",description="Eliminado") @DeleteMapping("/students/{sid}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteStudent(@PathVariable Integer sid) { data.deleteStudent(sid); }
 @Operation(summary="Listar asignaturas") @GetMapping("/catalogs/subjects") public List<CatalogResponse> subjects() { return data.subjects(); }
 @Operation(summary="Listar plataformas") @GetMapping("/catalogs/platforms") public List<CatalogResponse> platforms() { return data.platforms(); }
 @Operation(summary="Listar tipos de asistencia") @GetMapping("/catalogs/attendance-types") public List<CatalogResponse> types() { return data.types(); }
 @Operation(summary="Crear asignatura") @PostMapping("/catalogs/subjects") @ResponseStatus(HttpStatus.CREATED) public CatalogResponse addSubject(@Valid @RequestBody CatalogRequest r) { return data.addSubject(r); }
 @Operation(summary="Crear plataforma") @PostMapping("/catalogs/platforms") @ResponseStatus(HttpStatus.CREATED) public CatalogResponse addPlatform(@Valid @RequestBody CatalogRequest r) { return data.addPlatform(r); }
 @Operation(summary="Crear tipo de asistencia") @PostMapping("/catalogs/attendance-types") @ResponseStatus(HttpStatus.CREATED) public CatalogResponse addType(@Valid @RequestBody CatalogRequest r) { return data.addType(r); }
 @Operation(summary="Listar calificaciones") @GetMapping("/students/{sid}/grades") public List<GradeResponse> grades(@PathVariable Integer sid) { return data.grades(sid); }
 @Operation(summary="Consultar calificación") @GetMapping("/students/{sid}/grades/{id}") public GradeResponse grade(@PathVariable Integer sid,@PathVariable Integer id) { return data.gradeDetail(sid,id); }
 @Operation(summary="Crear calificación") @PostMapping("/students/{sid}/grades") @ResponseStatus(HttpStatus.CREATED) public GradeResponse createGrade(@PathVariable Integer sid,@Valid @RequestBody GradeRequest r) { return data.saveGrade(sid,null,r); }
 @Operation(summary="Editar calificación") @PutMapping("/students/{sid}/grades/{id}") public GradeResponse updateGrade(@PathVariable Integer sid,@PathVariable Integer id,@Valid @RequestBody GradeRequest r) { return data.saveGrade(sid,id,r); }
 @Operation(summary="Eliminar calificación") @DeleteMapping("/students/{sid}/grades/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteGrade(@PathVariable Integer sid,@PathVariable Integer id) { data.deleteGrade(sid,id); }
 @Operation(summary="Listar asistencias") @GetMapping("/students/{sid}/attendance") public List<AttendanceResponse> attendance(@PathVariable Integer sid) { return data.attendance(sid); }
 @Operation(summary="Consultar asistencia") @GetMapping("/students/{sid}/attendance/{id}") public AttendanceResponse attendanceDetail(@PathVariable Integer sid,@PathVariable Integer id) { return data.attendanceDetail(sid,id); }
 @Operation(summary="Crear asistencia") @PostMapping("/students/{sid}/attendance") @ResponseStatus(HttpStatus.CREATED) public AttendanceResponse createAttendance(@PathVariable Integer sid,@Valid @RequestBody AttendanceRequest r) { return data.saveAttendance(sid,null,r); }
 @Operation(summary="Editar asistencia") @PutMapping("/students/{sid}/attendance/{id}") public AttendanceResponse updateAttendance(@PathVariable Integer sid,@PathVariable Integer id,@Valid @RequestBody AttendanceRequest r) { return data.saveAttendance(sid,id,r); }
 @Operation(summary="Eliminar asistencia") @DeleteMapping("/students/{sid}/attendance/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteAttendance(@PathVariable Integer sid,@PathVariable Integer id) { data.deleteAttendance(sid,id); }
}

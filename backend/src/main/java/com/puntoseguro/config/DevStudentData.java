package com.puntoseguro.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
@Configuration @Profile("dev") public class DevStudentData {
 @Bean CommandLineRunner seedStudentData(JdbcTemplate jdbc, TransactionTemplate transactions) { return args -> transactions.executeWithoutResult(status -> {
  if (jdbc.update("INSERT INTO development_seed_history(name) VALUES (?) ON CONFLICT DO NOTHING", "student-fixtures-v1") == 0) return;
  jdbc.update("INSERT INTO asignatura(nombre) VALUES ('Matemáticas'),('Lengua Castellana'),('Inglés') ON CONFLICT (nombre) DO NOTHING");
  jdbc.update("INSERT INTO plataforma_origen(nombre) VALUES ('Manual'),('PASEN'),('Moodle') ON CONFLICT (nombre) DO NOTHING");
  jdbc.update("INSERT INTO tipo_asistencia(nombre) VALUES ('Presente'),('Falta'),('Falta justificada'),('Retraso') ON CONFLICT (nombre) DO NOTHING");
  if (jdbc.queryForObject("SELECT count(*) FROM alumno",Long.class)==0) {
   jdbc.update("INSERT INTO alumno(nombre,apellidos,curso,grupo) VALUES ('Lucía','Prueba Uno','2º ESO','A'),('Mario','Prueba Dos','2º ESO','A'),('Sara','Prueba Tres','3º ESO','B')");
   jdbc.update("INSERT INTO calificacion(id_alumno,id_asignatura,id_plataforma_origen,nota,evaluacion,fecha) SELECT a.id,s.id,p.id,8.50,'Primera evaluación',CURRENT_DATE FROM alumno a CROSS JOIN asignatura s CROSS JOIN plataforma_origen p WHERE a.nombre='Lucía' AND a.apellidos='Prueba Uno' AND s.nombre='Matemáticas' AND p.nombre='Manual'");
   jdbc.update("INSERT INTO asistencia(id_alumno,id_asignatura,id_tipo_asistencia,id_plataforma_origen,dia) SELECT a.id,s.id,t.id,p.id,CURRENT_DATE FROM alumno a CROSS JOIN asignatura s CROSS JOIN tipo_asistencia t CROSS JOIN plataforma_origen p WHERE a.nombre='Lucía' AND a.apellidos='Prueba Uno' AND s.nombre='Matemáticas' AND t.nombre='Presente' AND p.nombre='Manual'");
  }
 }); }
}

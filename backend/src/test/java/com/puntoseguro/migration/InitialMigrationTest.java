package com.puntoseguro.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class InitialMigrationTest {
    @Container static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:17");

    @Test void createsSchemaAndEnforcesConstraints() throws Exception {
        var flyway = Flyway.configure().dataSource(db.getJdbcUrl(), db.getUsername(), db.getPassword()).load();
        flyway.migrate();
        assertEquals("1", flyway.info().current().getVersion().getVersion());
        try (var c = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword()); var s = c.createStatement()) {
            s.executeUpdate("INSERT INTO alumno(nombre, apellidos) VALUES ('Prueba', 'Alumno')");
            s.executeUpdate("INSERT INTO asignatura(nombre) VALUES ('Matematicas')");
            s.executeUpdate("INSERT INTO plataforma_origen(nombre) VALUES ('Manual')");
            s.executeUpdate("INSERT INTO calificacion(id_alumno,id_asignatura,id_plataforma_origen,nota,evaluacion,fecha) VALUES (1,1,1,8,'Primera',CURRENT_DATE)");
            assertThrows(SQLException.class, () -> s.executeUpdate("INSERT INTO calificacion(id_alumno,id_asignatura,id_plataforma_origen,nota,evaluacion,fecha) VALUES (1,1,1,11,'Primera',CURRENT_DATE)"));
            assertThrows(SQLException.class, () -> s.executeUpdate("INSERT INTO asistencia(id_alumno,id_tipo_asistencia,id_plataforma_origen,dia) VALUES (999,1,1,CURRENT_DATE)"));
        }
    }
}

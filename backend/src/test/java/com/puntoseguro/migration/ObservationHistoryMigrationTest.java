package com.puntoseguro.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/** El historial no puede reconstruir las ediciones anteriores a su creación. */
@Testcontainers
class ObservationHistoryMigrationTest {
    @Container static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:17");

    private Flyway flyway(String target) {
        var configuration=Flyway.configure().dataSource(db.getJdbcUrl(),db.getUsername(),db.getPassword());
        return (target==null?configuration:configuration.target(target)).load();
    }

    @Test void marksOnlyVersionsThatPredateTheHistoryAsImported() throws Exception {
        flyway("6").migrate();
        UUID author=UUID.randomUUID();
        try (var connection=DriverManager.getConnection(db.getJdbcUrl(),db.getUsername(),db.getPassword()); var statement=connection.createStatement()) {
            statement.executeUpdate("INSERT INTO usuario(id,nombre,email,password_hash,role,activo) VALUES ('"+author+"','Autora','autora@puntoseguro.local','hash','PROFESOR',true)");
            statement.executeUpdate("INSERT INTO alumno(nombre,apellidos) VALUES ('Alumno','Anterior')");
            statement.executeUpdate("INSERT INTO observacion(id_alumno,id_autor,texto,fecha_creacion) VALUES (1,'"+author+"','Texto corregido antes del historial', TIMESTAMP '2024-02-03 10:15:00')");
        }
        flyway("7").migrate();
        try (var connection=DriverManager.getConnection(db.getJdbcUrl(),db.getUsername(),db.getPassword()); var statement=connection.createStatement()) {
            statement.executeUpdate("INSERT INTO observacion(id_alumno,id_autor,texto,fecha_creacion) VALUES (1,'"+author+"','Observación nueva',CURRENT_TIMESTAMP)");
            statement.executeUpdate("INSERT INTO observacion_historial(id_observacion,id_editor,texto,fecha_edicion) VALUES (2,'"+author+"','Observación nueva',CURRENT_TIMESTAMP)");
        }
        flyway(null).migrate();
        try (var connection=DriverManager.getConnection(db.getJdbcUrl(),db.getUsername(),db.getPassword()); var statement=connection.createStatement();
             var rows=statement.executeQuery("SELECT id_editor,texto,fecha_edicion,importado FROM observacion_historial ORDER BY id")) {
            assertTrue(rows.next());
            assertEquals(author.toString(),rows.getString("id_editor"));
            assertEquals("Texto corregido antes del historial",rows.getString("texto"));
            assertEquals(Timestamp.valueOf("2024-02-03 10:15:00"),rows.getTimestamp("fecha_edicion"));
            assertTrue(rows.getBoolean("importado"));
            assertTrue(rows.next());
            assertEquals("Observación nueva",rows.getString("texto"));
            assertFalse(rows.getBoolean("importado"));
            assertFalse(rows.next());
        }
    }
}

-- =====================================================================
-- PuntoSeguro - Esquema de base de datos (PostgreSQL)
-- HU01 - Integración de calificaciones y asistencia
--   * Crear tabla Asistencia en BD.
--   * Crear tabla Calificaciones en BD.
--
-- Convenciones (igual que en Proyecto-Bancosol):
--   - Tablas en singular y snake_case.
--   - Clave primaria "id SERIAL".
--   - Claves foráneas con el nombre "id_<tabla>".
--   - Catálogos en tablas propias (tipo_*) en vez de enums.
-- =====================================================================

DROP TABLE IF EXISTS calificacion;
DROP TABLE IF EXISTS asistencia;
DROP TABLE IF EXISTS tipo_asistencia;
DROP TABLE IF EXISTS asignatura;
DROP TABLE IF EXISTS plataforma_origen;
DROP TABLE IF EXISTS alumno;

-- ---------------------------------------------------------------------
-- Alumno: mínimo necesario para asociar asistencia y calificaciones.
-- ---------------------------------------------------------------------
CREATE TABLE alumno (
    id         SERIAL PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    apellidos  VARCHAR(150) NOT NULL,
    curso      VARCHAR(20),
    grupo      VARCHAR(10)
);

-- ---------------------------------------------------------------------
-- Plataforma de la que procede cada dato (PASEN, Moodle, manual...).
-- ---------------------------------------------------------------------
CREATE TABLE plataforma_origen (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE asignatura (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL UNIQUE
);

-- Presente, Falta, Falta justificada, Retraso...
CREATE TABLE tipo_asistencia (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(50) NOT NULL UNIQUE
);

-- ---------------------------------------------------------------------
-- Asistencia: un registro por alumno, día y (opcionalmente) asignatura.
-- ---------------------------------------------------------------------
CREATE TABLE asistencia (
    id                    SERIAL PRIMARY KEY,
    id_alumno             INTEGER NOT NULL REFERENCES alumno(id),
    id_asignatura         INTEGER REFERENCES asignatura(id),
    id_tipo_asistencia    INTEGER NOT NULL REFERENCES tipo_asistencia(id),
    id_plataforma_origen  INTEGER NOT NULL REFERENCES plataforma_origen(id),
    dia                   DATE NOT NULL,
    fecha_sincronizacion  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones         TEXT
);

-- ---------------------------------------------------------------------
-- Calificación: nota de un alumno en una asignatura y evaluación.
-- ---------------------------------------------------------------------
CREATE TABLE calificacion (
    id                    SERIAL PRIMARY KEY,
    id_alumno             INTEGER NOT NULL REFERENCES alumno(id),
    id_asignatura         INTEGER NOT NULL REFERENCES asignatura(id),
    id_plataforma_origen  INTEGER NOT NULL REFERENCES plataforma_origen(id),
    nota                  NUMERIC(4,2) NOT NULL CHECK (nota >= 0 AND nota <= 10),
    evaluacion            VARCHAR(50) NOT NULL,
    fecha                 DATE NOT NULL,
    fecha_sincronizacion  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones         TEXT
);

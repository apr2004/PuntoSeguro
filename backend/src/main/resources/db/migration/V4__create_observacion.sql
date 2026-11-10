-- ---------------------------------------------------------------------
-- HU02 - Observaciones del profesorado
-- Observación: anotación de seguimiento que un docente registra sobre un
-- alumno. No tiene estado ni referencia a casos: nunca es un Caso confirmado.
-- ---------------------------------------------------------------------
CREATE TABLE observacion (
    id              SERIAL PRIMARY KEY,
    id_alumno       INTEGER NOT NULL REFERENCES alumno(id),
    id_autor        UUID NOT NULL REFERENCES usuario(id),
    texto           TEXT NOT NULL,
    fecha_creacion  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_observacion_id_alumno ON observacion (id_alumno);

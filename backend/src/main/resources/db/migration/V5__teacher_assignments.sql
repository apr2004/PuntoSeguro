-- La asignación expresa del centro determina qué docentes pueden seguir a cada alumno.
CREATE TABLE asignacion_docente (
    id SERIAL PRIMARY KEY,
    id_alumno INTEGER NOT NULL REFERENCES alumno(id) ON DELETE CASCADE,
    id_docente UUID NOT NULL REFERENCES usuario(id),
    CONSTRAINT uq_asignacion_docente UNIQUE (id_alumno, id_docente)
);

CREATE INDEX idx_asignacion_docente_id_docente ON asignacion_docente (id_docente);

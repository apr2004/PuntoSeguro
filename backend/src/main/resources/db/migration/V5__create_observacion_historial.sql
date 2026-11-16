-- HU02 - Historial de ediciones de Observaciones.
-- Cada fila es una versión inmutable, incluida la versión inicial.
CREATE TABLE observacion_historial (
    id              SERIAL PRIMARY KEY,
    id_observacion  INTEGER NOT NULL REFERENCES observacion(id),
    id_editor       UUID NOT NULL REFERENCES usuario(id),
    texto           TEXT NOT NULL,
    fecha_edicion   TIMESTAMP NOT NULL
);

CREATE INDEX idx_observacion_historial_observacion
    ON observacion_historial (id_observacion, fecha_edicion, id);

-- Las Observaciones existentes reciben su versión inicial al actualizar el esquema.
INSERT INTO observacion_historial (id_observacion, id_editor, texto, fecha_edicion)
SELECT id, id_autor, texto, fecha_creacion
FROM observacion;

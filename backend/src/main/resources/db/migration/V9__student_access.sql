ALTER TABLE usuario DROP CONSTRAINT usuario_role_check;
ALTER TABLE usuario ADD CONSTRAINT usuario_role_check CHECK (role IN ('ESTUDIANTE', 'PROFESOR', 'CENTRO', 'TUTOR_LEGAL'));
ALTER TABLE usuario ADD COLUMN version_seguridad BIGINT NOT NULL DEFAULT 0;
CREATE TABLE vinculo_alumno (
    id SERIAL PRIMARY KEY,
    id_alumno INTEGER NOT NULL REFERENCES alumno(id) ON DELETE CASCADE,
    id_usuario UUID NOT NULL REFERENCES usuario(id),
    CONSTRAINT uq_vinculo_alumno UNIQUE (id_alumno, id_usuario)
);
CREATE INDEX idx_vinculo_alumno_usuario ON vinculo_alumno(id_usuario);

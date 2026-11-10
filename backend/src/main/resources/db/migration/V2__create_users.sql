CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ESTUDIANTE', 'PROFESOR', 'CENTRO')),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE UNIQUE INDEX uq_usuario_email_ignore_case ON usuario (lower(email));

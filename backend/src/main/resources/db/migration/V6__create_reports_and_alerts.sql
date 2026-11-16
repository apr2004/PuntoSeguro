-- HU05 - Reportes conversacionales y alertas de revisión.
-- Un Reporte nunca equivale a un Caso confirmado: el centro debe revisarlo.
CREATE TABLE reporte (
    id                    SERIAL PRIMARY KEY,
    id_informante         UUID NOT NULL REFERENCES usuario(id),
    fecha_creacion        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    es_acoso              VARCHAR(20) NOT NULL CHECK (es_acoso IN ('SI', 'NO', 'DUDOSO')),
    tipo_acoso            VARCHAR(20) NOT NULL CHECK (tipo_acoso IN ('FISICO', 'VERBAL', 'SOCIAL', 'CIBERACOSO', 'OTRO')),
    relacion_informante   VARCHAR(30) NOT NULL CHECK (relacion_informante IN ('AFECTADO_DIRECTO', 'TESTIGO', 'NO_ESPECIFICADO')),
    reiteracion           VARCHAR(20) NOT NULL CHECK (reiteracion IN ('SI', 'NO', 'DESCONOCIDO')),
    nivel_urgencia        VARCHAR(30) NOT NULL CHECK (nivel_urgencia IN ('NORMAL', 'PREOCUPANTE', 'RIESGO_INMINENTE')),
    resumen               TEXT NOT NULL,
    estado_revision       VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_REVISION' CHECK (estado_revision IN ('PENDIENTE_REVISION'))
);

CREATE INDEX idx_reporte_informante_fecha ON reporte(id_informante, fecha_creacion DESC);
CREATE INDEX idx_reporte_fecha ON reporte(fecha_creacion DESC);

CREATE TABLE reporte_mensaje (
    id          SERIAL PRIMARY KEY,
    id_reporte  INTEGER NOT NULL REFERENCES reporte(id) ON DELETE CASCADE,
    rol         VARCHAR(20) NOT NULL CHECK (rol IN ('ALUMNO', 'ASISTENTE')),
    texto       TEXT NOT NULL,
    orden       INTEGER NOT NULL CHECK (orden >= 0),
    UNIQUE (id_reporte, orden)
);

CREATE TABLE reporte_indicio (
    id          SERIAL PRIMARY KEY,
    id_reporte  INTEGER NOT NULL REFERENCES reporte(id) ON DELETE CASCADE,
    texto       TEXT NOT NULL,
    orden       INTEGER NOT NULL CHECK (orden >= 0),
    UNIQUE (id_reporte, orden)
);

CREATE TABLE alerta_reporte (
    id             SERIAL PRIMARY KEY,
    id_reporte     INTEGER NOT NULL UNIQUE REFERENCES reporte(id) ON DELETE CASCADE,
    nivel_urgencia VARCHAR(30) NOT NULL CHECK (nivel_urgencia IN ('NORMAL', 'PREOCUPANTE', 'RIESGO_INMINENTE')),
    estado         VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_REVISION' CHECK (estado IN ('PENDIENTE_REVISION')),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_alerta_reporte_fecha ON alerta_reporte(fecha_creacion DESC);

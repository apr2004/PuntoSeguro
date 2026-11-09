-- =====================================================================
-- PuntoSeguro - Datos ficticios de prueba
-- Ejecutar después de schema.sql. Ningún dato corresponde a personas reales.
-- =====================================================================

INSERT INTO plataforma_origen (nombre) VALUES
    ('PASEN'),
    ('Moodle'),
    ('Manual');

INSERT INTO asignatura (nombre) VALUES
    ('Matemáticas'),
    ('Lengua Castellana'),
    ('Inglés'),
    ('Biología');

INSERT INTO tipo_asistencia (nombre) VALUES
    ('Presente'),
    ('Falta'),
    ('Falta justificada'),
    ('Retraso');

INSERT INTO alumno (nombre, apellidos, curso, grupo) VALUES
    ('Lucía',  'Prueba Uno',  '2º ESO', 'A'),
    ('Mario',  'Prueba Dos',  '2º ESO', 'A'),
    ('Sara',   'Prueba Tres', '3º ESO', 'B');

-- Asistencia (alumno 2 acumula faltas para poder probar alertas de riesgo)
INSERT INTO asistencia (id_alumno, id_asignatura, id_tipo_asistencia, id_plataforma_origen, dia) VALUES
    (1, 1, 1, 1, '2026-10-05'),
    (1, 2, 1, 1, '2026-10-05'),
    (2, 1, 2, 1, '2026-10-05'),
    (2, 2, 2, 1, '2026-10-05'),
    (2, 1, 2, 1, '2026-10-06'),
    (2, 3, 4, 1, '2026-10-06'),
    (3, 1, 3, 3, '2026-10-06'),
    (3, 4, 1, 1, '2026-10-06');

INSERT INTO calificacion (id_alumno, id_asignatura, id_plataforma_origen, nota, evaluacion, fecha) VALUES
    (1, 1, 1, 8.50, '1ª Evaluación', '2026-10-15'),
    (1, 2, 1, 7.25, '1ª Evaluación', '2026-10-15'),
    (2, 1, 1, 3.00, '1ª Evaluación', '2026-10-15'),
    (2, 2, 1, 4.50, '1ª Evaluación', '2026-10-15'),
    (2, 3, 2, 2.75, '1ª Evaluación', '2026-10-15'),
    (3, 4, 2, 6.00, '1ª Evaluación', '2026-10-15');

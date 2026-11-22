-- HU02 - Historial de ediciones de Observaciones.
-- Las versiones que V7 creó a partir de Observaciones anteriores copian el texto
-- vigente en ese momento. Se marcan como importadas porque las ediciones que ya se
-- hubieran hecho antes de activar el historial no se pueden reconstruir.
ALTER TABLE observacion_historial
    ADD COLUMN importado BOOLEAN NOT NULL DEFAULT FALSE;

-- Solo las versiones anteriores a la ejecución de V7 son importadas. La fecha se
-- compara en UTC, que es la zona en la que la aplicación guarda fecha_edicion.
UPDATE observacion_historial h
SET importado = TRUE
FROM flyway_schema_history f
WHERE f.version = '7'
  AND h.fecha_edicion < (f.installed_on AT TIME ZONE 'UTC');

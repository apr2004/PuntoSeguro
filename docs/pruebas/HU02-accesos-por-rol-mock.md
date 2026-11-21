# HU02: matriz mock de acceso por rol

**Issue:** [#11](https://github.com/apr2004/PuntoSeguro/issues/11)<br>
**Historia padre:** [#8](https://github.com/apr2004/PuntoSeguro/issues/8)<br>
**Tipo de comprobación:** escenarios manuales con usuarios, relaciones y observaciones ficticios

> El contrato ya está implementado en `main` mediante #9/PR #30 y #10/PR #33. Ejecuté `ObservationIntegrationTest` contra el commit `66536f0` de `main`: 7 pruebas pasaron. Esta suite no cubre todos los perfiles ni todos los límites definidos originalmente en esta matriz; los resultados siguientes distinguen lo comprobado de lo pendiente.

## Comportamiento implementado comprobado

- Un docente asignado puede consultar y crear observaciones, y editar las propias mientras mantenga la asignación.
- Un docente sin asignación y el alumnado reciben `403` al intentar acceder a observaciones.
- `CENTRO` puede consultar y editar observaciones, pero no crearlas (`403`). El alcance multi-centro no está implementado ni comprobado.
- El servidor establece autor y fecha de creación; al editar, conserva ambos valores.
- Las peticiones sin sesión reciben `401`.
- El modelo de Observación no la convierte automáticamente en un Reporte ni en un caso confirmado; este punto no se prueba como flujo de negocio en `ObservationIntegrationTest`.

**Límites del modelo actual:** solo existen los roles `PROFESOR`, `CENTRO` y `ESTUDIANTE`; no hay rol de tutor legal ni una relación que permita comprobar el aislamiento entre centros. Por tanto, esos dos requisitos no se pueden validar con el contrato actual.

## Datos ficticios

- Centro: `centro-A`.
- Alumno: `alumno-001`, vinculado a `centro-A`.
- Docente asignado: `profesor-001`, asignado a `alumno-001`.
- Docente no asignado: `profesor-002`, sin asignación a `alumno-001`.
- Tutor legal: `tutor-001`, vinculado a `alumno-001`.
- Administración: `centro-usuario-001`, autorizado en `centro-A`.
- Observación: `obs-001`, texto ficticio «Se mantuvo apartado del grupo durante el recreo», creada por `profesor-001` el `2026-11-16T10:30:00Z`.

No se usan datos de personas reales.

## Matriz de escenarios

| Caso | Perfil y acción | Resultado esperado del mock | Comprobación asociada |
|---|---|---|---|
| R1 | Docente asignado consulta `obs-001` | Permitido (`200`); devuelve solo la observación de `alumno-001` | La asignación coincide con el alumno |
| R2 | Docente asignado crea una observación para `alumno-001` | Permitido (`201`); autor `profesor-001` y fecha UTC generados por el servidor | El cliente no puede elegir otro autor ni alterar la fecha de creación |
| R3 | Docente asignado modifica una observación propia | Permitido (`200`); se conservan autor y fecha de creación | La edición está cubierta; la nueva entrada en historial queda pendiente de #12/PR #35 |
| R4 | Docente no asignado consulta, crea o modifica para `alumno-001` | Denegado (`403`); no devuelve texto ni modifica datos | Cambiar el identificador de alumno no evita la autorización |
| R5 | Alumno intenta consultar, crear o modificar observaciones | Denegado (`403`); no devuelve texto ni modifica datos | El acceso autenticado no equivale a permiso por rol |
| R6 | Tutor legal consulta, crea o modifica observaciones | No ejecutable: el rol y la relación tutor-alumno no existen | Definir el perfil antes de implementar este caso |
| R7 | `CENTRO` consulta, intenta crear o edita una observación | Consulta y edición permitidas; creación denegada (`403`) | La suite no verifica aislamiento multi-centro; no se debe afirmar que `centro-A` está aislado |
| R8 | Un usuario llama sin token | Denegado (`401`) y cubierto por la suite | Un token inválido no figura como caso independiente en la suite actual |
| R9 | Se registra un texto con hechos no verificados | No hay verificación automatizada de la valoración del texto | La Observación está separada del Reporte; hace falta una prueba específica si se exige este criterio |

## Resultado de la ejecución

- **Resultado automatizado:** `ObservationIntegrationTest` — 7 ejecutadas, 7 pasaron, 0 fallos, contra PostgreSQL 17 con datos ficticios.
- R1, R2, R4 y R5 están cubiertos; R3 verifica edición y conservación de autor/fecha, pero no historial.
- `#9` y `#10` están integradas. `#12` sigue abierta en la PR #35; al integrarse, ejecutar y registrar la comprobación de historial.
- R6 no se puede ejecutar hasta que exista un rol de tutor. R7 requiere definir e implementar el aislamiento entre centros. R9 necesita una prueba específica del criterio de no-confirmación.
- **La issue #11 sigue abierta** hasta resolver esos límites y registrar los casos aplicables contra el producto.

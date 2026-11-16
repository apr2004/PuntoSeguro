# HU02: matriz mock de acceso por rol

**Issue:** [#11](https://github.com/apr2004/PuntoSeguro/issues/11)<br>
**Historia padre:** [#8](https://github.com/apr2004/PuntoSeguro/issues/8)<br>
**Tipo de comprobación:** escenarios manuales con usuarios, relaciones y observaciones ficticios

> Esto documenta una simulación del contrato de HU02, no una ejecución contra la aplicación. En el `main` revisado no hay modelo, endpoint ni servicio de observaciones. Las tareas de implementación de permisos (#10) y autoría/historial (#12) siguen abiertas. No se debe tomar esta matriz como evidencia de que el producto ya aplica estos permisos.

## Política simulada

- El docente asignado al alumno puede consultar, crear y modificar sus observaciones.
- El docente no asignado, el alumnado y el tutor legal no pueden consultar ni modificar observaciones de HU02. La historia restringe la consulta a personal autorizado del centro.
- La administración autorizada del centro puede consultar y gestionar observaciones dentro de su centro.
- Toda creación registra como autor al usuario autenticado y una fecha UTC; una edición conserva su historial.
- Un texto de observación no convierte automáticamente los hechos en un caso confirmado de acoso.
- Sin sesión válida, la API devuelve `401`. Para una sesión válida sin permiso se espera `403`, sin contenido de la observación.

**Suposición de la simulación:** el perfil `CENTRO` representa a administración autorizada. El modelo actual no distingue administración de otros usuarios del centro ni incluye el rol de tutor legal. Si ClickUp o el Product Owner define permisos distintos para tutores o administración, habrá que ajustar esta expectativa.

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
| R3 | Docente asignado modifica `obs-001` | Permitido (`200`); se añade una entrada al historial | La autoría inicial y la edición quedan identificables |
| R4 | Docente no asignado consulta, crea o modifica para `alumno-001` | Denegado (`403`); no devuelve texto ni modifica datos | Cambiar el identificador de alumno no evita la autorización |
| R5 | Alumno intenta consultar, crear o modificar observaciones | Denegado (`403`); no devuelve texto ni modifica datos | El acceso autenticado no equivale a permiso por rol |
| R6 | Tutor legal consulta, crea o modifica observaciones | Denegado (`403`) según la política actual de HU02 | Validar este supuesto con el Product Owner si HU06 amplía el acceso de tutores |
| R7 | Administración autorizada consulta o gestiona una observación de `centro-A` | Permitido (`200`/`201`); solo dentro de `centro-A` | El alcance del centro se comprueba además del rol |
| R8 | Cualquier perfil llama sin token o con token inválido | Denegado (`401`) | No se revela si `obs-001` existe |
| R9 | Un usuario crea una observación con hechos no verificados | Se conserva como observación pendiente de valoración; nunca se etiqueta automáticamente como caso confirmado | El texto de la observación no dispara una confirmación automática |

## Resultado de la simulación

- **5/5 perfiles** incluidos: docente asignado, docente no asignado, alumno, tutor legal y administración.
- Las acciones permitidas y denegadas quedan definidas para consulta, creación y modificación.
- Se cubren autenticación, alcance del centro, autoría, historial y la distinción entre observación y caso confirmado.
- **Ejecución contra la aplicación: pendiente.** No se puede obtener un resultado HTTP real hasta integrar observaciones y permisos (#10, #12). Repetir R1–R9 con datos ficticios en esa integración y registrar las respuestas observadas antes de cerrar la issue.

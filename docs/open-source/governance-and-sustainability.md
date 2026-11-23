# Gobernanza y sostenibilidad de PuntoSeguro

PuntoSeguro es un módulo de asistencia para PASEN que ayuda a prevenir y detectar el acoso escolar y el abandono académico. Trata información sensible de menores, así que la gobernanza responde a una pregunta más exigente que "¿quién decide?": **¿quién garantiza que ningún cambio ponga en riesgo a un alumno?**

## 1. Modelo de gobernanza: steering council de tres mantenedores

**Decisión:** un consejo de gobierno (*steering council*) de **3 mantenedores**, cada uno responsable de un área y con un suplente. Las decisiones se toman por **consenso perezoso** y solo se vota cuando hay bloqueo (ver §3).

**Por qué:**

- El proyecto es pequeño (8 personas) y está en una fase temprana. Un grupo reducido decide con rapidez sin depender de una sola persona.
- Hemos descartado un **BDFL** porque el riesgo que más nos preocupa es depender de una persona (§5). Además, un único líder no puede tener criterio experto a la vez en IA, permisos y protección de datos.
- Hemos descartado un **consejo con los 8 miembros** porque con voluntarios y disponibilidad variable acabaría paralizando las decisiones.
- No necesitamos neutralidad entre empresas competidoras **todavía**. Cuando entren administraciones u otros adoptantes, la custodia pasará a una fundación (§4).

## 2. Quién revisa y acepta cambios

### Roles

| Rol | Quién | Qué puede hacer | Cómo se llega |
|---|---|---|---|
| Usuario | Centros, administraciones, desarrolladores que lo prueban | Abrir issues | — |
| Contributor | Cualquiera que envía un PR | Proponer cambios | Enviar un PR |
| Reviewer | Contributors con trayectoria | Aprobar PRs en áreas **no** sensibles | 3 PRs fusionados y 5 revisiones útiles; lo propone un mantenedor |
| Maintainer | Miembros del consejo | Aprobar y fusionar cualquier PR, publicar releases y votar | Elección por mayoría del consejo; mandato de 1 año renovable |

Si un mantenedor o reviewer pasa **3 meses sin actividad**, pasa a **emérito**: conserva el reconocimiento, pero deja su puesto libre y su suplente lo asume hasta que se elija a otra persona.

### Composición inicial del consejo

| Área | Titular | Suplente |
|---|---|---|
| IA, prompts y dominio (Categorización, palabras de riesgo) | Alfonso Ramos (@alraro) | Andrea Rodríguez (@apr2004) |
| Backend, permisos y seguridad de datos | Javier Urbaneja (@Javierub32) | María Muñoz (@mmartinmaria) |
| Infraestructura, CI, releases y frontend | Andrea Rodríguez (@apr2004) | Paula Jiménez (@Paaula21) |

Antonio Martín (@antoniomarting05, base de datos), Daniel Robles (@daniiroblees, documentación) y Marina Fernández (@mafese3) empiezan como reviewers.

### Reglas para fusionar un PR

1. `main` está protegida: nadie hace push directo y **nadie fusiona su propio PR**.
2. El CI (`.github/workflows/tests.yml`) tiene que estar en verde.
3. **Cambios normales:** 1 aprobación de un reviewer o un mantenedor del área, según `CODEOWNERS`.
4. **Áreas sensibles:** 2 aprobaciones, y al menos una del mantenedor del área. Estas áreas son:
   - `backend/.../security/` (control de acceso por rol);
   - todo lo que exponga datos del alumnado u Observaciones;
   - `backend/src/main/resources/prompts/`;
   - `backend/src/main/resources/categorization/palabras_riesgo.txt`;
   - `backend/.../llm/` y las migraciones de `db/migration/`.

**Estado actual:** estas reglas todavía no se aplican. `main` no tiene protección y, de los últimos 15 PRs, 8 se fusionaron sin revisión y 6 los fusionó su propio autor. Hay que corregirlo antes de publicar (§8).

### Tareas que no se ven pero necesitan responsable

| Tarea | Responsable |
|---|---|
| Triage de issues (respuesta en menos de 72 h) | Turno semanal rotatorio entre mantenedores y reviewers |
| Releases y notas de versión | Titular de Infraestructura |
| Documentación (README, guías) | Daniel Robles |
| Avisos de seguridad (canal privado) | Titular de Backend y seguridad |

## 3. Resolución de desacuerdos

1. **Se discute** en el issue o PR, con argumentos técnicos y casos concretos.
2. **Consenso perezoso:** si un mantenedor propone una decisión y en 72 h nadie presenta una objeción razonada, queda aprobada.
3. **Votación:** si hay objeción y no se llega a un acuerdo, vota el consejo por mayoría simple (2 de 3).
4. **Registro:** las decisiones difíciles de revertir se documentan como ADR en `docs/adr/`, con el contexto y las alternativas.

**Regla de protección del menor:** en cualquier desacuerdo que afecte a la seguridad o a la privacidad de los menores, **prevalece la opción más protectora**. Cualquier mantenedor puede vetar un cambio de ese tipo si justifica el riesgo por escrito, y el veto solo se levanta por unanimidad del consejo. Así un debate sobre funcionalidad no acaba debilitando la protección de datos.

Los cambios en estas reglas de gobernanza requieren unanimidad del consejo y un periodo de comentarios público de 2 semanas.

## 4. Custodia del proyecto

- **Ahora:** el repositorio está en la cuenta personal de una sola persona, que además es la única administradora. **Antes de publicar** se transfiere a una **organización de GitHub** con los 3 mantenedores como propietarios. Al transferirlo se conservan los issues, los PRs y el historial, y GitHub redirige las URLs antiguas.
- **A largo plazo:** si una administración educativa (por ejemplo, la Consejería, para integrarlo en PASEN) u otros centros lo adoptan, cederemos la custodia (repositorio, nombre y gobernanza) a una **fundación o entidad neutral**. La licencia sigue siendo abierta: lo que cambia es quién garantiza la continuidad. Un adoptante institucional necesita que detrás haya una entidad estable y no un grupo de voluntarios, igual que PyTorch pasó de Meta a la Linux Foundation.

## 5. ¿Qué pasa si se va un desarrollador principal?

| Escenario | Impacto hoy | Con las medidas de este documento |
|---|---|---|
| **Se va Andrea (@apr2004)**, única administradora del repositorio | **Riesgo de acceso.** Nadie más puede cambiar la configuración, los secretos del CI ni los permisos, ni transferir el repositorio. Si su cuenta deja de estar disponible, el proyecto queda congelado y la única salida es un fork que fragmenta la comunidad. | Con la organización y 3 propietarios, su salida no bloquea nada. Paula, su suplente en Infraestructura, asume las releases. |
| **Se va Alfonso (@alraro)**, que concentra el conocimiento de los prompts, la red de seguridad de Riesgo inminente y la visión de producto | **Riesgo de conocimiento.** Solo dos personas han tocado los prompts. Cambiar un prompt o la lista de palabras de riesgo sin entender el motivo puede hacer que se pierdan alertas de Riesgo inminente, que es el fallo más grave posible del producto. | Andrea, su suplente, ya conoce los prompts. Las decisiones están en los ADR y `GLOSSARY.md`. Los casos ficticios de prueba (`FictitiousCasesTest`) detectan regresiones, y la doble revisión obliga a que el conocimiento se comparta en cada cambio. |

**Principio general:** ningún área tiene una sola persona con conocimiento y acceso. Cada titular tiene suplente, revisar es la forma de repartir conocimiento y la cláusula de inactividad libera los puestos vacíos.

## 6. Riesgos de continuidad

| # | Riesgo | Impacto | Mitigación |
|---|---|---|---|
| 1 | **Uso indebido con datos reales de menores.** Un centro lo despliega con datos reales enviando relatos a un LLM externo, o con los permisos mal configurados. | **Muy alto:** daño a menores, sanciones por el RGPD y pérdida de confianza en el proyecto. | Configuración segura por defecto (aviso o bloqueo si hay datos reales con un LLM externo); guía de despliegue y protección de datos; `SECURITY.md` con un canal privado; doble revisión de las áreas sensibles (§2) y regla de protección del menor (§3). |
| 2 | **Dependencia de un único adoptante institucional** (PASEN y la Junta de Andalucía). Si no lo adoptan o cambian de política, el proyecto pierde su uso principal y su vía de financiación. | Alto: es lo que le pasó a OpenSolaris con su único patrocinador. | Separar el núcleo de la integración con PASEN para que otros sistemas educativos puedan usarlo; buscar más de un centro o administración piloto. |
| 3 | **Equipo fundador voluntario sin financiación.** Un cambio de trabajo o de prioridades deja el proyecto sin mantenimiento. | Alto: un mantenimiento sin financiar, como el de OpenSSL antes de Heartbleed. | Modelo de sostenibilidad (§7); cláusula de inactividad y suplentes (§2); cesión a una fundación (§4); seguimiento de la actividad de mantenimiento como indicador de salud. |

## 7. Modelo de sostenibilidad

**Decisión:** la fuente principal será la **financiación pública o institucional**, complementada con **servicios profesionales**.

| Fuente | Papel | Por qué |
|---|---|---|
| Financiación pública (convenios con administraciones educativas, convocatorias de innovación educativa, fondos europeos de digitalización) | **Principal** | Los beneficiarios son centros públicos y su alumnado. Quien se beneficia del servicio público es quien puede financiarlo. |
| Servicios: implantación en centros o administraciones, formación del profesorado y orientadores, soporte y adaptación de protocolos | **Complementaria** | Monetiza la experiencia sin cerrar el código y escala con el número de adoptantes (modelo Red Hat). |
| Donaciones y patrocinios (GitHub Sponsors, fundaciones sociales de protección de la infancia) | Menor | Cubren costes pequeños, pero no bastan para sostener un mantenimiento serio. |
| ~~Open Core~~ | Descartada | Cobrar por funciones avanzadas de detección o protección supondría que solo los centros que pagan protegen mejor a su alumnado. Éticamente no es aceptable. |
| ~~SaaS alojado~~ | Descartada | Nos convertiría en custodios de datos de menores, con la carga legal y el riesgo del §6.1. Cada administración debe desplegarlo en su propia infraestructura. |

El modelo no depende de la licencia que se elija: los servicios y la financiación pública son compatibles con cualquier licencia abierta. Solo si se eligiera una licencia *copyleft* habría que valorar además una licencia dual, que **no** proponemos.

## 8. Acciones pendientes

| Momento | Acción | Responsable |
|---|---|---|
| Antes de publicar | Crear la organización de GitHub y transferir el repositorio con 3 propietarios | Andrea Rodríguez |
| Antes de publicar | Proteger `main`: CI obligatorio, al menos 1 revisión y nada de auto-merge | Titular de Infraestructura |
| Antes de publicar | Añadir `CODEOWNERS` con las áreas y las áreas sensibles del §2 | Consejo |
| Antes de publicar | Pasar los roles y el proceso de decisión (§1–§3) a un `GOVERNANCE.md` | Consejo |
| Antes de publicar | `SECURITY.md` con un canal privado y la guía de despliegue y protección de datos | Titular de Backend y seguridad |
| Primera publicación | Arrancar el turno rotatorio de triage | Consejo |
| Después de publicar | Revisar anualmente la composición del consejo y aplicar la cláusula de inactividad | Consejo |
| Después de publicar | Buscar un piloto institucional y una vía de financiación pública | Titular de IA y dominio (visión de producto) |

## 9. Dependencias con otros entregables

- **Licencia (`community-readiness.md`):** pendiente. Hay que comprobar que es coherente con el §7 y con la cesión a una fundación (§4).
- **Componentes publicados (`open-source-strategy.md`):** las áreas sensibles del §2 tienen que coincidir con lo que finalmente se publique.
- **Indicadores de salud (`open-source-launch-plan.md`):** el tiempo de respuesta a issues depende del turno de triage (§2), y la concentración de contribuciones y la actividad de mantenimiento miden los riesgos del §5 y del §6.3.

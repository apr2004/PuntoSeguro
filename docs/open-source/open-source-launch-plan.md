# Plan de Lanzamiento Open Source — PuntoSeguro
 
**Proyecto:** [PuntoSeguro](https://github.com/apr2004/PuntoSeguro)  
**Asignatura:** Gestión de Proyectos Software (E.T.S.I. Informática — Universidad de Málaga)  
**Entregable común:** `open-source-launch-plan.md` (Actividades 1, 3 y 4)

## Actividad 1. Análisis inicial y visión compartida del equipo

- **¿Por qué queremos abrir el proyecto?**  
  Queremos abrir PuntoSeguro para que la comunidad educativa y técnica pueda auditar, probar y mejorar una herramienta destinada a apoyar la prevención y detección temprana del acoso escolar y el abandono académico. La revisión externa aportará mayor transparencia sobre cómo funcionan los algoritmos de categorización y control de acceso, mejorará la accesibilidad y garantizará que el software pueda evolucionar más allá del curso académico. La apertura se plantea inicialmente con fines de aprendizaje, auditoría y experimentación, dejando explícito que el sistema aún no está validado para su despliegue con casos reales en centros educativos.

- **¿Qué tipo de comunidad o colaboradores queremos atraer?**  
  Buscamos atraer tres perfiles complementarios: desarrolladores con experiencia en Java/Spring Boot, React y PostgreSQL; especialistas en pruebas, ciberseguridad, privacidad y accesibilidad web; y profesionales de la orientación escolar y la docencia que puedan validar los flujos funcionales y enriquecer los criterios de detección. Esperamos recibir reportes de errores bien documentados, mejoras en la cobertura de pruebas, traducciones y contribuciones de código revisadas por el equipo.

- **¿Qué partes del proyecto se publicarán y cuáles deberían quedar fuera?**  
  Se publicará el código fuente completo del backend (`backend/`), el frontend (`frontend/`), los scripts y migraciones de base de datos (`database/` y `db/migration/`), los prompts del modelo de lenguaje (`prompts/`), la documentación técnica (`docs/`, `README.md`, `GLOSSARY.md`) y los juegos de pruebas con relatos ficticios. Quedarán estrictamente fuera credenciales reales, claves de API, archivos `.env` locales, volcados de bases de datos, actas internas con datos personales o conversaciones y expedientes reales de alumnado y centros. Esta exclusión implica auditar tanto los archivos actuales como todo el historial previo de commits del repositorio.

- **¿En qué estado de madurez se encuentra el proyecto?**  
  PuntoSeguro es un prototipo funcional en fase de desarrollo activo. Cuenta con despliegue mediante Docker Compose (`compose.yml`), autenticación JWT y control de acceso por roles (`SecurityConfig`, `StudentAccessInterceptor`), gestión de observaciones académicas, chatbot de reporte y sistema de alertas. Sin embargo, carece de integración real con plataformas institucionales como PASEN y depende de relatos ficticios. Además, la categorización automática mediante LLM tiene carácter meramente orientativo y exige siempre la revisión humana de un profesional del centro antes de confirmar cualquier caso.

- **¿Qué dificultades podría encontrar una persona externa que quisiera utilizarlo o contribuir?**  
  En primer lugar, necesita familiarizarse con una arquitectura multicontenedor (PostgreSQL, backend Spring Boot y frontend React/Nginx) y configurar una clave propia de Groq o desplegar Ollama en local para probar los flujos de inteligencia artificial. En segundo lugar, debe comprender el dominio pedagógico y legal del proyecto (diferencia entre reporte inicial y caso confirmado, y límites de privacidad). Por último, a nivel organizativo el repositorio aún carece de archivo `LICENSE`, guía de contribución (`CONTRIBUTING.md`) y plantillas de issues o *Pull Requests*.

## Actividad 3. Integración y plan de lanzamiento

### 1. Revisión de coherencia entre subequipos

Tras poner en común los entregables de los cuatro subequipos (`open-source-strategy.md`, `technical-readiness.md`, `community-readiness.md` y `governance-and-sustainability.md`), se garantiza la alineación en tres ejes clave:

- **Seguridad y alcance:** El aviso de «uso exclusivo con datos ficticios» definido por Javier Urbaneja Benítez y Paula Fernández Jiménez se materializa mediante la auditoría de actas e historial de Andrea Pérez Rodríguez y Marina Fernández Serrano y la sección de advertencia obligatoria en el `README.md` redactada por Daniel Robles Cantos y Antonio Martín García.
- **Licencia y dependencias:** La licencia **Apache 2.0** elegida por Daniel Robles Cantos y Antonio Martín García es plenamente compatible con el análisis de licencias de terceros de `pom.xml` y `package.json` realizado por Andrea Pérez Rodríguez y Marina Fernández Serrano.
- **Gobernanza y flujo de trabajo:** Las reglas de revisión doble en cambios críticos de Alfonso Ramos Rojas y María Muñoz Martín se reflejan directamente en el `CONTRIBUTING.md` de Daniel Robles Cantos y Antonio Martín García y se automatizan mediante las comprobaciones obligatorias de CI/CD de Andrea Pérez Rodríguez y Marina Fernández Serrano.

### 2. Roadmap de apertura

| Fase | Acciones concretas a ejecutar |
| :--- | :--- |
| **1. Antes de publicar (Preparación)** | • Auditar historial de Git y revisar `docs/reuniones/2026-11-10-transcripcion.pdf` para eliminar cualquier dato personal.<br>• Añadir `LICENSE` (Apache 2.0), `CODE_OF_CONDUCT.md` y `CONTRIBUTING.md`.<br>• Actualizar `README.md` con el aviso de prototipo, credenciales ficticias y guía con Ollama.<br>• Configurar `.github/ISSUE_TEMPLATE/`, `PULL_REQUEST_TEMPLATE.md` y ampliar `.github/workflows/tests.yml` con el build del frontend y escaneo de secretos.<br>• Activar reglas de protección en la rama `main` (mínimo 1 revisión y CI en verde). |
| **2. Primera publicación (Lanzamiento `v0.1.0-alpha`)** | • Crear el tag y la primera release oficial `v0.1.0-alpha` con notas de versión claras sobre su estado experimental.<br>• Publicar los tres primeros issues etiquetados como `good first issue` y `help wanted`.<br>• Habilitar GitHub Discussions como canal abierto para dudas, sugerencias pedagógicas y soporte de instalación.<br>• Difundir el proyecto en entornos universitarios y comunidades de software libre educativo. |
| **3. Después de publicar (Operación y comunidad)** | • Realizar triaje semanal de nuevos issues aplicando las etiquetas de `docs/agents/triage-labels.md`.<br>• Revisar *Pull Requests* cumpliendo los plazos y criterios de aprobación del Comité de Mantenimiento.<br>• Publicar releases menores periódicas (`v0.1.x`) conforme se cierren mejoras de seguridad o accesibilidad.<br>• Evaluar trimestralmente los indicadores de salud del proyecto. |

### 3. Indicadores de salud del proyecto

| Indicador | Qué permite detectar | Umbral de alerta | Actuación si empeora |
| :--- | :--- | :--- | :--- |
| **Tiempo de primera respuesta a issues y PRs** | Si la comunidad externa se siente ignorada o si existe cuello de botella en las revisiones. | Superior a 5 días laborables sin respuesta o triaje. | Establecer turnos rotatorios semanales de triaje entre los miembros del Comité de Mantenimiento. |
| **Concentración de contribuciones (*Bus Factor*)** | Dependencia excesiva de uno o dos autores originales en los commits y revisiones. | Más del 70 % de los commits o aprobaciones recaen en una sola persona. | Forzar revisiones cruzadas entre subequipos y documentar módulos complejos mediante ADRs. |
| **Número de nuevos colaboradores externos** | Barreras de entrada técnicas (dificultad al levantar Docker/LLM) o falta de visibilidad. | 0 contribuciones o issues externos tras 2 meses desde la release `v0.1.0-alpha`. | Simplificar el arranque local (perfil Ollama por defecto), mejorar el `README.md` y crear más `good first issue`. |
| **Actividad de mantenimiento y estado del CI** | Riesgo de abandono del proyecto o acumulación de deuda técnica y dependencias vulnerables. | Más de 30 días sin commits de mantenimiento ni resolución de alertas de Dependabot. | Reajustar el alcance de la hoja de ruta a tareas asumibles por el equipo o buscar nuevos co-mantenedores en la universidad. |

## Actividad 4. Cierre y reflexión conjunta

- **¿Está el proyecto preparado para publicarse hoy?**  
  **No.** Aunque el repositorio ya es técnicamente accesible en GitHub y cuenta con una base funcional sólida y tests en el backend, todavía no cumple los requisitos mínimos para una apertura responsable como proyecto Open Source: carece de archivo `LICENSE`, no tiene definidas las guías y plantillas de contribución (`CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`), falta validar el frontend en integración continua y es necesario auditar los archivos de reuniones e historial para garantizar la ausencia total de datos personales.

- **¿Cuál es el principal riesgo de abrirlo en su estado actual?**  
  El principal riesgo es doble: por un lado, la **exposición involuntaria de datos personales o secretos** en el historial de commits o documentos auxiliares; por otro, que un tercero **interprete el prototipo como una herramienta lista para usar con casos reales de acoso escolar**, introduciendo información sensible de menores en un sistema que aún no ha sido auditado ni cumple con las garantías legales de producción.

- **¿Cuál sería la primera tarea que realizaríamos mañana?**  
  Ejecutar una **auditoría completa de seguridad y privacidad** sobre el repositorio y su historial de Git (revisando especialmente las variables de entorno, los datos de prueba y el archivo `docs/reuniones/2026-11-10-transcripcion.pdf`) e incorporar inmediatamente en la raíz el archivo `LICENSE` (Apache 2.0) junto con el aviso de uso exclusivo con datos ficticios en el `README.md`.

- **¿Qué podría provocar que el proyecto fracasara aunque el código estuviese disponible públicamente?**  
  Que el repositorio se convierta en «código abandonado» (*abandonware*) tras finalizar la asignatura: si el entorno resulta difícil de levantar para alguien externo (por depender de claves externas de API), si no existen tareas iniciales claras ni documentación de arquitectura y si los issues o *Pull Requests* quedan sin respuesta durante semanas, ninguna comunidad podrá adoptar ni mantener el proyecto.
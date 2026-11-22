# PuntoSeguro
Asistente conversacional inteligente integrado en PASEN para la prevención, detección y gestión de casos de acoso escolar y abandono académico.

## Arranque local

Requisitos: Docker y Docker Compose. Desde la raíz:

```sh
docker compose up --build
```

La web estará en http://localhost:3000, la API en http://localhost:8080 y PostgreSQL en el puerto 5432. La base se conserva en el volumen `puntoseguro-db`. La contraseña de desarrollo puede cambiarse mediante `POSTGRES_PASSWORD` en un archivo `.env` (ver `.env.example`).

Flyway crea el esquema al arrancar el backend a partir de `backend/src/main/resources/db/migration/V1__create_initial_schema.sql`, basado en `database/schema.sql`. Los datos ficticios de `database/datos_prueba.sql` son opcionales y no se cargan automáticamente. El esquema original contiene sentencias DROP: no lo ejecutes sobre una base con datos que quieras conservar. Para cambios futuros, añade migraciones Flyway nuevas.

El backend sigue la arquitectura Spring Boot de UrbanPulse y el frontend es React web con Vite. Las carpetas `.agents` se han copiado de UrbanPulse; algunas skills conservan referencias a su stack original y deben interpretarse para este proyecto. Esta estructura no implementa todavía la issue 2.

## Autenticación en desarrollo

El Compose local activa el perfil `dev`, que crea, si no existen, las cuentas `estudiante@puntoseguro.local`, `profesor@puntoseguro.local`, `centro@puntoseguro.local` y `tutor_legal@puntoseguro.local`. La contraseña inicial es `puntoseguro`. El registro público crea únicamente estudiantes; los otros roles los asigna el centro. Estos usuarios y la clave JWT predeterminada de Compose son exclusivos de desarrollo y no deben usarse en producción.

La API expone `POST /api/auth/register`, `POST /api/auth/login` y `GET /api/auth/me` (este último requiere `Authorization: Bearer <token>`). El frontend guarda el token en `sessionStorage` hasta cerrar sesión o la pestaña. La documentación OpenAPI está disponible en http://localhost:8080/swagger-ui.html. Para ejecutar el backend fuera de Compose, configura `PUNTOSEGURO_JWT_SECRET` con una clave aleatoria codificada en Base64 de al menos 32 bytes; el backend no arranca sin ella. La migración V2 añade la tabla `usuario`.

## Datos del alumnado

Tras iniciar sesión, la pestaña **Estudiantes** permite crear y editar estudiantes y gestionar sus calificaciones y asistencias. Un estudiante con registros asociados no se puede eliminar hasta retirar esos registros. Los formularios permiten añadir opciones a los catálogos de asignaturas, plataformas y tipos de asistencia. Los cambios se guardan en PostgreSQL; las operaciones fallidas muestran un mensaje y las transacciones conservan los datos previamente guardados.

En el perfil `dev`, si no hay estudiantes, se cargan una sola vez estudiantes y registros ficticios para probar la interfaz. El centro puede consultar y gestionar todos los datos, crear y eliminar fichas y administrar catálogos, roles y vínculos. Los docentes acceden a los alumnos asignados y pueden modificar sus datos académicos; estudiantes y tutores legales solo pueden consultar los alumnos vinculados. El centro debe crear esos vínculos expresamente. El cambio de rol elimina vínculos y asignaciones anteriores y requiere volver a iniciar sesión. No se ha implementado una sincronización externa con PASEN u otra plataforma.

En la ficha de cada estudiante, la sección **Observaciones** recoge las Observaciones de los docentes con su autor y fecha. El docente asignado puede consultarlas, añadirlas y corregir las propias; el centro puede consultarlas y corregirlas. El alumnado y los tutores legales no tienen acceso. Una Observación es información de seguimiento y nunca constituye un Caso confirmado.

## Categorización de Reportes

El chatbot interpreta la conversación del alumno y devuelve una Categorización orientativa (si trata de acoso, Tipo de acoso, Relación del informante, Reiteración, Nivel de urgencia, resumen e indicios). La API expone `POST /api/reports/categorization`, que requiere sesión, no guarda nada y puede probarse desde Swagger UI. Otros servicios del backend pueden inyectar `ReportCategorizationService` directamente. El vocabulario está en `GLOSSARY.md` y la elección del LLM, en `docs/adr/0001-llm-externo-gratuito.md`.

Por defecto usa el plan gratuito de Groq. Crea tu propia clave en https://console.groq.com/keys (sin tarjeta) y ponla en tu `.env` como `PUNTOSEGURO_LLM_API_KEY`; usa la tuya, porque el límite diario es por cuenta. Sin clave, el backend arranca igual y la Categorización responde 503 (`CATEGORIZATION_UNAVAILABLE`). Envía solo relatos ficticios: el proveedor es externo.

Para usar un LLM local en lugar de Groq, añade a tu `.env` `PUNTOSEGURO_LLM_URL=http://ollama:11434/v1`, `PUNTOSEGURO_LLM_MODELO=qwen3:1.7b` y `PUNTOSEGURO_LLM_API_KEY=ollama` (Ollama no usa la clave, pero sin ella no se envía nada), con las tres a la vez, y arranca con `docker compose --profile ollama up --build`. La primera vez descarga el modelo (unos minutos). El Ollama del proyecto se expone en el puerto 11435 para no chocar con uno instalado en tu máquina. `PUNTOSEGURO_LLM_TIMEOUT` (60 s por defecto) limita cada llamada.

Los prompts en español están en `backend/src/main/resources/prompts/` (`categorizacion.md` y `chatbot.md`, el system prompt base del chatbot) y la lista editable de la red de seguridad de Riesgo inminente, en `backend/src/main/resources/categorization/palabras_riesgo.txt`. El frontend ofrece el **Chat de apoyo** para que el alumno pueda contar algo que le afecta o que ha observado y enviar el Reporte al centro. `POST /api/reports/chat` genera orientación sin suponer que el Informante sea la persona afectada; `POST /api/reports` persiste la conversación y crea una alerta `PENDIENTE_REVISION`. El equipo con rol `PROFESOR` o `CENTRO` consulta las alertas en `GET /api/alerts` y el detalle de los Reportes en `GET /api/reports/{id}`. Una alerta nunca confirma automáticamente un Caso confirmado.

Los casos ficticios contra el LLM real se ejecutan a mano con `PUNTOSEGURO_LLM_API_KEY=... ./mvnw test -Dtest=FictitiousCasesTest` y se saltan si no hay clave. Las pruebas de chat, persistencia, permisos y alertas usan un LLM falso y no transmiten relatos reales.

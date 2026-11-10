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

El Compose local activa el perfil `dev`, que crea, si no existen, las cuentas `estudiante@puntoseguro.local`, `profesor@puntoseguro.local` y `centro@puntoseguro.local`. La contraseña inicial de las tres es `puntoseguro`. El registro público crea únicamente estudiantes; los otros roles no se pueden seleccionar en el formulario. Estos usuarios y la clave JWT predeterminada de Compose son exclusivos de desarrollo y no deben usarse en producción.

La API expone `POST /api/auth/register`, `POST /api/auth/login` y `GET /api/auth/me` (este último requiere `Authorization: Bearer <token>`). El frontend guarda el token en `sessionStorage` hasta cerrar sesión o la pestaña. La documentación OpenAPI está disponible en http://localhost:8080/swagger-ui.html. Para ejecutar el backend fuera de Compose, configura `PUNTOSEGURO_JWT_SECRET` con una clave aleatoria codificada en Base64 de al menos 32 bytes; el backend no arranca sin ella. La migración V2 añade la tabla `usuario`.

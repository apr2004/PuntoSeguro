# Backend de PuntoSeguro

Java 17, Spring Boot, JPA, Flyway y PostgreSQL. Paquete base: `com.puntoseguro`.

Organiza el código en `controller`, `dto`, `service`, `repository`, `entity` y `mapper`, siguiendo la arquitectura de UrbanPulse. Los controladores delegan en servicios y no exponen entidades; los mappers convierten entre entidades y DTO. Gestiona el esquema con migraciones Flyway y mantén `ddl-auto=validate`.

Para tareas de backend consulta las skills de `backend/.agents/skills/`. Ejecuta `./mvnw test` (o `mvnw.cmd test` en Windows).

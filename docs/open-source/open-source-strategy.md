# Estrategia y alcance Open Source — PuntoSeguro

## 1. Objetivo y beneficios esperados

- **Decisión:** Mantener público el repositorio de PuntoSeguro y estructurar una apertura responsable orientada a la reutilización educativa y a la mejora colaborativa del prototipo.
- **Justificación:** Al tratarse de un sistema sensible que gestiona alertas de convivencia escolar, la apertura permite que terceros auditen la lógica de permisos y los prompts de categorización, repartiendo el conocimiento técnico y reduciendo la dependencia exclusiva del equipo original. No obstante, somos conscientes de que la participación externa no surge de forma automática y requerirá dedicar tiempo del equipo a revisar *Pull Requests* y responder incidencias.

## 2. Delimitación de componentes publicados y excluidos

| Decisión | Componentes del repositorio | Justificación |
| :--- | :--- | :--- |
| **Publicar tras revisión** | Código de `frontend/`, `backend/`, esquemas SQL y migraciones Flyway, tests, `README.md`, `GLOSSARY.md`, `prompts/` y `.env.example`. | Permiten comprender, desplegar y extender la arquitectura completa sin dependencias ocultas. |
| **Publicar solo con datos sintéticos** | `database/datos_prueba.sql`, `DevUsers.java`, `DevStudentData.java` y casos de `docs/pruebas/`. | Facilitan el arranque inmediato para desarrolladores externos sin comprometer la privacidad de personas reales. |
| **Mantener fuera** | Archivos `.env` privados, claves de API (Groq/JWT), volcados reales, logs de producción y conversaciones o datos de menores y centros. | Evitan brechas de seguridad y cumplen con la normativa de protección de datos de menores. |
| **Condicionar a autorización** | Futuros conectores con plataformas externas (como PASEN) o recursos gráficos/documentales de terceros. | Su redistribución exige verificar previamente las licencias y permisos institucionales correspondientes. |

## 3. Usuarios y colaboradores potenciales

- **Usuarios iniciales:** Estudiantes de ingeniería del software, docentes e investigadores interesados en desplegar el entorno con datos ficticios para evaluar su arquitectura o adaptarlo a otros contextos formativos. A medio plazo, equipos directivos y departamentos de orientación podrían valorar su uso piloto tras superar las auditorías legales y de seguridad pertinentes.
- **Colaboradores objetivo:** Desarrolladores *full-stack* (Java/Spring Boot y React), auditores de seguridad y privacidad, especialistas en accesibilidad (WCAG) y profesionales de la orientación educativa.
- **Canales de participación:** Reporte de incidencias y propuestas mediante GitHub Issues y envío de mejoras mediante *Pull Requests*, con revisión obligatoria por parte de los mantenedores en cambios que afecten a seguridad, roles o prompts del chatbot.

## 4. Valoración de madurez y funcionalidades no preparadas

- **Entorno de desarrollo frente a producción:** Las cuentas precargadas en `DevUsers.java` y las configuraciones por defecto de `compose.yml` están pensadas exclusivamente para pruebas locales y no son seguras para un entorno real.
- **Integración institucional inexistente:** La conexión con PASEN figura como línea futura en el `README.md`; no debe presentarse como una capacidad operativa.
- **Limitaciones del módulo de IA:** Tanto `ReportCategorizationService` como `ChatbotService` utilizan por defecto un [proveedor LLM externo](docs/adr/0001-llm-externo-gratuito.md) restringido a textos ficticios. Sus resultados son de apoyo y nunca sustituyen el criterio profesional.
- **Falta de marco legal y auditoría:** El repositorio carece actualmente de archivo `LICENSE` en la raíz y no ha pasado una auditoría externa de seguridad sobre sus endpoints.

## 5. Tres prioridades para una apertura efectiva

| Prioridad | Acción concreta y criterio de éxito | Riesgo que mitiga |
| :--- | :--- | :--- |
| **1. Higienizar datos y delimitar el alcance de uso** | Auditar archivos e historial de Git para descartar secretos o datos personales, y añadir un aviso destacado en [README](README.md) indicando que el prototipo solo admite datos ficticios. | Exposición de información sensible y uso indebido del sistema con menores reales. |
| **2. Establecer el marco legal y la guía de entrada** | Incorporar el archivo `LICENSE` en la raíz y publicar una  [guía básica de contribución](CONTRIBUTING.md) con instrucciones claras para ejecutar los tests. | Incertidumbre legal sobre la reutilización del código y abandono de colaboradores por falta de guía. |
| **3. Blindar flujos críticos y repartir revisiones** | Ampliar los tests de control de acceso por rol (`StudentAccessInterceptor`) y asignar al menos dos revisores responsables de validar cada *Pull Request*. | Regresiones de seguridad en datos sensibles y cuello de botella en un único mantenedor. |

## 6. Riesgos identificados y acciones pendientes

| Riesgo | Acción pendiente |
| :--- | :--- |
| Filtración de credenciales o datos personales en el repositorio o su historial. | Ejecutar una revisión completa de archivos e historial de commits, documentar el resultado y rotar cualquier secreto expuesto. |
| Confusión entre un prototipo académico y una herramienta validada para centros reales. | Añadir advertencias visibles en [README](README.md) y en la interfaz, y definir los requisitos legales y técnicos previos a cualquier piloto real. |
| Incertidumbre jurídica por ausencia de licencia en la raíz. | Incorporar el archivo `LICENSE` (Apache 2.0) tras verificar la compatibilidad de todas las dependencias. |
| Falta de respuesta a contribuciones externas o concentración del conocimiento. | Asignar revisores titulares y suplentes por área y documentar las decisiones técnicas en `docs/adr/`. |
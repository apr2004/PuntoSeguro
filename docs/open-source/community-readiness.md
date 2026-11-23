# Preparación de la comunidad: PuntoSeguro (`community-readiness.md`)
 
## 1. Licencia

Se adopta **Apache License 2.0** como licencia oficial del proyecto, cuyo texto íntegro se incluirá en el archivo `LICENSE` de la raíz del repositorio y se referenciará en el `README.md`.

- **Justificación:** Es una licencia permisiva que facilita la adopción institucional y las contribuciones externas al permitir usar, modificar y redistribuir el código (incluidas obras derivadas), añadiendo además una concesión expresa de patentes que aporta seguridad jurídica a centros educativos y terceros colaboradores.
- **Condiciones y límites:** La licencia regula únicamente el uso del software; no exime del cumplimiento de la normativa vigente en materia de privacidad, seguridad y protección de datos de menores (RGPD).

---

## 2. Contenido mínimo del README (`README.md`)

El archivo `README.md` funcionará como guía principal de incorporación (*onboarding*) para permitir que cualquier persona externa comprenda y despliegue el entorno en menos de 5 minutos. Contendrá:

1. **Descripción y estado del proyecto:** Qué es PuntoSeguro, qué problemática de detección temprana y apoyo escolar resuelve, y su estado actual de madurez (prototipo funcional académico).
2. **Alcance funcional disponible:** Gestión de alumnado, calificaciones y asistencias; registro de observaciones; chat de apoyo; gestión de reportes y alertas pendientes de revisión.
3. **Limitaciones conocidas:** Funcionalidades aún no implementadas o simuladas, especificando expresamente que la sincronización real con **PASEN** requiere autorización institucional y actualmente opera mediante datos ficticios.
4. **Despliegue y ejecución local:** Requisitos previos, puesta en marcha mediante Docker Compose, URLs locales de la interfaz web y de la API, configuración de variables a partir de `.env.example` y comandos para ejecutar las baterías de pruebas.
5. **Pruebas y exploración de la API:** Instrucciones de acceso a la documentación interactiva OpenAPI y credenciales de las cuentas ficticias preconfiguradas para el perfil de desarrollo.
6. **Guía de colaboración:** Enlaces directos a `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md` y al flujo de reporte de incidencias y envío de *Pull Requests*.
7. **Advertencias críticas de seguridad:** Prohibición explícita de utilizar las credenciales de desarrollo en entornos de producción, de subir secretos o datos reales de estudiantes al repositorio, y aviso sobre el envío de consultas a un proveedor LLM externo.

---

## 3. Reporte de errores y propuestas de funcionalidad

La gestión de incidencias y mejoras se centralizará en **GitHub Issues** mediante formularios estructurados (`.github/ISSUE_TEMPLATE/`):

- **Plantilla de reporte de errores (*Bug Report*):** Solicitará versión o *commit* afectado, entorno de ejecución, pasos exactos para reproducir el fallo, comportamiento esperado y comportamiento observado, junto con instrucciones para adjuntar trazas técnicas anonimizadas.
- **Plantilla de propuesta de mejora (*Feature Request*):** Solicitará la necesidad educativa o técnica que resuelve, los roles afectados (docente, orientación, administración) y los criterios de aceptación esperados.
- **Protección de datos y seguridad:** Queda terminantemente prohibido incluir nombres reales, centros educativos identificables, capturas con datos reales, conversaciones de alumnado o *tokens* en los *Issues* o *Pull Requests*. Las vulnerabilidades de seguridad no se reportarán públicamente; deberán notificarse por el canal confidencial definido en `SECURITY.md`.

---

## 4. Normas para Pull Requests (`CONTRIBUTING.md`)

Toda contribución de código o documentación deberá seguir el flujo documentado en `CONTRIBUTING.md`:

1. **Ramas vinculadas:** Trabajar sobre una rama corta creada a partir de la rama principal con el formato `issue/<número>-descripcion-breve`.
2. **Alcance acotado:** Limitar cada *Pull Request* (PR) a un único cambio lógico o incidencia para facilitar su revisión en una sola sesión, enlazando siempre la *Issue* correspondiente.
3. **Descripción y documentación:** Detallar el propósito del cambio, las modificaciones realizadas y el método de verificación empleado, actualizando la documentación si se altera el comportamiento o la configuración.
4. **Validación automatizada (CI):** Ejecutar localmente las pruebas del backend y la compilación del frontend, asegurando que todas las comprobaciones de Integración Continua (GitHub Actions) pasen antes de solicitar revisión.
5. **Revisión por pares:** Requerir la aprobación de al menos una persona mantenedora (y doble revisión en cambios que afecten a permisos, anonimización o seguridad) y resolver todas las observaciones antes de integrar.
6. **Higiene del cambio:** Verificar que la PR no contiene secretos, configuraciones locales ni modificaciones ajenas a su objetivo.
7. **Integración:** Realizar la fusión siguiendo la estrategia de *commits* acordada por el equipo para conservar un historial limpio y trazable.

---

## 5. Código de conducta (`CODE_OF_CONDUCT.md`)

Se adopta el estándar **Contributor Covenant 2.1**, que se publicará en el archivo `CODE_OF_CONDUCT.md`.

- **Normas de convivencia:** Se exige una comunicación respetuosa, inclusiva y orientada al trabajo técnico. No se tolerará el acoso, la discriminación ni la exposición de información privada de terceros.
- **Gestión de denuncias:** Las incidencias se comunicarán de forma confidencial al correo de contacto habilitado en el documento. Las personas mantenedoras evaluarán cada caso y aplicarán medidas proporcionales (advertencia, retirada de contenido o expulsión temporal/permanente). Para garantizar la imparcialidad, existirá un segundo contacto independiente en caso de que la denuncia involucre a una de las personas mantenedoras.

---

## 6. Tareas iniciales etiquetadas como `good first issue`

Con el fin de reducir la barrera de entrada a nuevos colaboradores, se prepararán las siguientes tres tareas iniciales:

1. **Configuración de plantillas de GitHub Issues:** Crear los archivos YAML/Markdown en `.github/ISSUE_TEMPLATE/` para errores y propuestas, incorporando avisos explícitos contra la publicación de datos escolares sensibles o credenciales.
2. **Pruebas de interfaz para estados vacíos y de error:** Implementar tests de componentes en una pantalla existente del frontend para verificar el renderizado correcto ante respuestas vacías o fallos de red, empleando exclusivamente datos ficticios.
3. **Mejora de accesibilidad en formulario del frontend:** Añadir etiquetas (`label`) asociadas correctamente a los campos de entrada, mensajes de error descriptivos y soporte completo de navegación por teclado en un formulario de registro o reporte.

*Criterio previo al etiquetado:* Antes de asignar la etiqueta `good first issue`, una persona mantenedora verificará que la tarea sigue vigente, acotará los archivos afectados y detallará los pasos exactos para validar la solución.

---

## 7. Riesgos identificados y acciones pendientes antes de publicar

Para cumplir con los requisitos de apertura del repositorio, se registran los siguientes riesgos y tareas previas obligatorias:

- **Riesgos:**
  - *R1 – Exposición accidental de datos o secretos en contribuciones externas:* Que un colaborador externo suba trazas con datos reales o claves de API en una *Issue* o *PR*.
  - *R2 – Incompatibilidad de licencias de terceros:* Que alguna dependencia actual del backend o frontend utilice una licencia incompatible con Apache 2.0 (por ejemplo, GPL estricta).
  - *R3 – Abandono de contribuciones por falta de respuesta:* Que las primeras *Issues* o *PRs* externas queden desatendidas por falta de tiempos de respuesta definidos.
- **Acciones pendientes:**
  1. Recabar la conformidad explícita de todas las personas autoras del equipo para publicar el código bajo Apache 2.0 y auditar las licencias de las dependencias.
  2. Redactar y subir a la raíz del repositorio los archivos `LICENSE`, `README.md`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md` y `SECURITY.md`.
  3. Definir en el plan de lanzamiento un rol rotatorio de *first responder* (compromiso de respuesta inicial en menos de 48 horas) y publicar las tres `good first issue`.
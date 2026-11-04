# Proceso de desarrollo ágil para el sistema de detección temprana

# **1\. Definición del proceso Scrum**

## **Roles y responsabilidades**

La siguiente distribución es un ejemplo porque el ConOps identifica al grupo y a las partes interesadas, pero no asigna Product Owner y Scrum Master.

| Rol | Responsabilidad | Responsable |
| :---- | :---- | :---- |
| Product Owner | Ordena el Product Backlog, aclara el valor y los criterios de aceptación, y propone el objetivo del Sprint. | Alfonso Ramos Rojas |
| Scrum Master | Facilita los eventos y ayuda a resolver bloqueos en el Scrum Meeting. | Javier Urbaneja Benítez |
| Developers | El equipo completo. Diseña, implementa, prueba e integra. El grupo comparte la responsabilidad de cumplir la Definition of Done. | Todos |
| Partes interesadas | Interactúa con el sistema como si fuese un docente, alumno o coordinador e intenta sugerir mejoras en el próximo Scrum Meeting. | Daniel Robles Cantos |

Ya que todos somos Developers por el contexto de la asignatura, las personas que asumen más roles además del de Developer tendrán una carga de trabajo de desarrollo menor. Así, facilitamos que el rol de Product Owner, Scrum Master y el de las Partes interesadas no se deje de lado.

## 

## **Duración y eventos del Sprint**

| Evento | Propuesta de organización |
| :---- | :---- |
| Duración del Sprint | 2 semanas |
| Sprint Planning | Al inicio del Sprint, el equipo acuerda un objetivo, selecciona HU priorizadas que pueda terminar y las descompone en tareas, vinculadas a criterios de aceptación. |
| Scrum Meeting | Reunión cada 3 días de 15 minutos para inspeccionar el avance hacia el objetivo, coordinar el trabajo y hacer visibles los bloqueos. |
| Refinamiento | Revisar durante el Sprint las HU candidatas, aclarar criterios, dependencias y riesgos, y dividir las historias demasiado grandes antes de seleccionarlas. |
| Sprint Review | Al final del Sprint, probar la app con datos ficticios y ver si todo funciona correctamente. |
| Retrospectiva | Reservar 30 minutos para identificar una práctica que ayudó, un problema concreto y una mejora que el equipo probará en el siguiente Sprint. |

## **Artefactos y compromisos**

El responsable de decidir qué tareas se asignarán al Sprint Backlog será Javier Urbaneja Benítez.

El trabajo que se asignará para el Sprint Backlog se elegirá teniendo en cuenta el coste de realizar las tareas (pues disponemos de personas y tiempo muy limitados) y del interés que podrían tener los StakeHolders

# 

# 

# 

# **2\. Selección de prácticas de XP**

| Práctica | Decisión y justificación propuesta | Aplicación, beneficio y evidencia propuesta |
| :---- | :---- | :---- |
| Planificación incremental | **Adoptar.** El ConOps prioriza funciones *Must Have* y deja otras para versiones posteriores. El acceso a PASEN también depende de obtener autorización. | Al comienzo de cada Sprint, el Product Owner ordenará las historias por prioridad y el equipo seleccionará las que contribuyan al objetivo. Así se podrá entregar primero lo esencial y adaptar el plan según lo aprendido. Evidencia: backlog priorizado, objetivo del Sprint y cambios de prioridad registrados. |
| Pequeñas versiones | **Adoptar.** Conviene validar pronto los flujos y criterios de aceptación, aunque el acceso a PASEN todavía no esté disponible. | Al final de cada Sprint, preparar un incremento ejecutable en un entorno de prueba, inicialmente con datos ficticios y servicios externos simulados. Esto permitirá detectar problemas pronto y obtener feedback. Evidencia: demostración del incremento y registro de las historias y criterios que cumple. |
| Diseño simple | **Adoptar.** El sistema combina chat, datos contextuales, casos históricos, permisos y alertas; algunas dependencias externas están por confirmar. | Implementar lo necesario para las historias priorizadas y separar la lógica de permisos, anonimización, orientación y conexión externa. Esto reducirá complejidad y facilitará los cambios. Evidencia: revisión del diseño frente a las historias actuales y comprobación de que los componentes pueden probarse sin acceso a PASEN. |
| Test-Driven Development (TDD) | **Adoptar; obligatorio.** Un fallo en permisos o anonimización podría exponer datos de menores. También hay que evitar confundir un indicio con un caso confirmado. | Antes de implementar cada regla, escribir una prueba que falle; hacer el cambio mínimo para que pase y refactorizar manteniendo las pruebas. Incluir pruebas de permisos por rol, accesos no autorizados, anonimización, casos no confirmados y escalado urgente. Evidencia: pruebas vinculadas a criterios de aceptación y resultados registrados en la integración continua. |
| Refactorización | **Adoptar.** Los requisitos y las interfaces externas pueden cambiar; las pruebas ayudan a mejorar el diseño sin alterar el comportamiento esperado. | Refactorizar en pasos pequeños cuando haya duplicación o resulte difícil probar permisos, anonimización, respuestas o alertas. Evidencia: pruebas verdes antes y después de cada cambio. |
| Pair Programming | **No adoptar como práctica habitual.** En un equipo de ocho personas, coordinar parejas en todas las tareas puede añadir carga de organización. | El equipo hará revisiones de código antes de integrar cambios. Si una tarea especialmente delicada se bloquea, sus responsables podrán trabajar juntos puntualmente, sin establecer turnos de pareja como norma. Beneficio esperado: mantener revisión y apoyo técnico con una organización más sencilla. Evidencia: revisiones registradas y bloqueos técnicos comunicados en el seguimiento del Sprint. |
| Integración continua | **Adoptar.** Frontend, backend y servicios simulados deben seguir siendo compatibles, y los fallos deben detectarse pronto. | Integrar cambios con frecuencia y ejecutar compilación y pruebas automatizadas de los componentes. Evidencia: historial de ejecuciones de la canalización y estado de compilación y pruebas por integración. |
| El cliente es miembro del equipo | **No adoptar en modalidad.** El centro debe validar casos y actuaciones, pero su disponibilidad y el acceso a PASEN dependen de permisos y acuerdos. | El Product Owner actuará como interlocutor, agrupará dudas y acordará momentos de validación con el centro, por ejemplo, en la Sprint Review. Beneficio esperado: recoger decisiones sin depender de la disponibilidad diaria del personal externo. Evidencia: preguntas, respuestas y validaciones registradas con fecha y responsable. |
| Test de cliente | **Adoptar.** La orientación debe ser comprensible para alumnado, familias y profesorado, y las recomendaciones deben presentarse como orientativas. | Preparar escenarios ficticios para los tres perfiles, incluidos casos sin antecedentes, consultas con precedentes, accesos no autorizados y situaciones urgentes. Evidencia: escenarios ejecutados, criterios revisados y observaciones recogidas en las revisiones del incremento. |
| Estándares de codificación | **Adoptar.** Las convenciones comunes facilitan leer y revisar el código entre integrantes. | Acordar formato, nombres, estructura de carpetas y tratamiento de errores. Configurar formato automático o lint si es posible. Evidencia: convenciones documentadas y comprobaciones de formato o lint. |
| Propiedad colectiva del código | **No adoptar.** Para un prototipo académico, no hace falta que cualquier integrante pueda modificar inmediatamente cualquier módulo; además, algunas partes sensibles requieren conocer bien las decisiones tomadas. | Asignar responsables iniciales por módulo y permitir cambios mediante revisión de otro integrante. Documentar las decisiones importantes para evitar dependencias personales. Beneficio esperado: conservar una responsabilidad clara sin dejar el conocimiento encerrado en una sola persona. Evidencia: responsables y revisores identificados y decisiones técnicas relevantes documentadas. |
| Paso sostenible | **Adoptar.** Hay que reservar tiempo para pruebas, revisiones y bloqueos, en lugar de dejar la calidad para el final. | Al planificar cada Sprint, considerar la disponibilidad real y reservar capacidad para pruebas, revisión y bloqueos. Revisar el trabajo arrastrado en la retrospectiva. Evidencia: historias terminadas frente a comprometidas, trabajo arrastrado y causas revisadas. |

# **3\. Proceso de desarrollo y calidad**

# El flujo conecta cada historia con sus criterios, tareas y pruebas. Así, el equipo puede comprobar por qué existe cada cambio y qué evidencia permite considerarlo terminado.

| Etapa | Qué hace el equipo |
| :---- | :---- |
| Refinar la historia | Confirmar usuario, necesidad, criterios de aceptación y dependencias. Si la integración con PASEN no está autorizada, registrar el límite y no suponer acceso. |
| Descomponer el trabajo | Crear tareas de backend, frontend, datos y pruebas. Enlazar cada tarea con uno o más criterios de aceptación; evitar subtareas genéricas como “revisar” sin resultado comprobable. |
| Escribir primero la prueba | Aplicar TDD a la lógica de negocio y permisos. La prueba inicial debe fallar por la razón esperada y utilizar datos ficticios. |
| Implementar la parte mínima | Añadir en Spring Boot y React el cambio mínimo que permita pasar la prueba. El backend validaría identidad, rol y relación con el alumno; la interfaz mostraría estados autorizados, vacíos, desactualizados o con error. |
| Refactoriza | Mejorar el diseño con las pruebas verdes. Mantener API y mensajes consistentes, sin ampliar el enlace de la HU |
| Revisar e integrar | Otra persona revisa el cambio y su relación con los criterios. La integración continua compila y ejecuta pruebas de backend y frontend |
| Comprobar la aceptación | Recorrer los escenarios desde la perspectiva del usuario con datos sintéticos; verificar los casos permitidos, denegados, vacíos y de error |
| Demostrar el incremento | En Sprint Review, demostrar únicamente lo que está integrado y cumple la Definition of Done. Anotar feedback y actualizar el Product Backlog. |

## **Pruebas por nivel**

1. Probar reglas y servicios y hacer pruebas de integración de API, persistencia y autorización. Comprobar que una persona no consulta un alumno fuera de su ámbito.  
2. En frontend, probar componentes y recorridos para alumnado, tutores y profesorado, incluidos estados vacíos, datos desactualizados, errores y acciones no disponibles.  
3. Validar orientación general sin antecedentes, excluir relatos no confirmados del repositorio reutilizable y comprobar que las respuestas no revelen datos que permitan reconocer a terceros.  
4. Probar primero con un contrato o datos simulados. La sincronización real requiere autorización e interfaces disponibles; la demostración indicará con claridad si la conexión aún no es real.

## **Ejemplo de trazabilidad**

**Ciberacoso:** Criterio: si una conversación presenta indicios según lo definido en la HU, el sistema crea una alerta pendiente de revisión y no confirma automáticamente que exista acoso → Tarea: analizar mensajes y mostrar la alerta con estado «pendiente de revisión» → Pruebas: usar conversaciones ficticias con indicios, sin indicios y ambiguas; comprobar que ninguna se confirma automáticamente → Evidencia: pruebas en integración continua y demostración con datos ficticios.

**Riesgo de abandono escolar:** Criterio: cuando los indicadores y umbrales definidos en la HU señalen riesgo, un docente autorizado puede ver la alerta y los factores que la originan → Tarea: calcular los indicadores y mostrar la alerta con esos factores → Pruebas: comprobar casos que cumplen y no cumplen las condiciones, datos incompletos y acceso no autorizado → Evidencia: resultados de pruebas y demostración con perfiles ficticios.

**Anonimización de casos:** Criterio: los casos previos solo se reutilizan para generar recomendaciones si están anonimizados y confirmados por el centro → Tarea: filtrar los casos no confirmados y eliminar o sustituir identificadores antes de reutilizarlos → Pruebas: verificar que se excluyen casos no confirmados y que los datos reutilizados no identifican al alumnado → Evidencia: pruebas automatizadas y demostración con casos ficticios.

# **4\. Acuerdo de trabajo del equipo**

| Tema | Acuerdo de ejemplo por revisar |
| :---- | :---- |
| Comunicación | ClickUp será el lugar de referencia para las tareas, su estado y las decisiones del proyecto.  Usaremos el chat habitual del equipo para consultas breves; si una conversación cambia requisitos o decisiones, quien la inicie dejará un resumen en ClickUp.  |
| Scrum Meeting | Haremos una reunión de seguimiento de 15 minutos al inicio de cada semana de trabajo, en el horario acordado por el grupo.  Cada persona dirá qué ha avanzado, qué hará a continuación y si tiene algún bloqueo.  Quien no pueda asistir actualizará esos puntos en ClickUp antes de la reunión.  |
| Asignación y revisión | Cada tarea activa tendrá una persona responsable, una fecha prevista y criterios claros para considerarla terminada. Antes de integrar cambios importantes, otra persona revisará que cumplan esos criterios.  Los cambios relacionados con permisos, datos sensibles o anonimización siempre tendrán una segunda revisión.  |
| Bloqueos y solicitud de ayuda | Si un bloqueo impide avanzar, la persona responsable lo comunicará en ClickUp y avisará al equipo por el chat.  Si no se resuelve antes de la siguiente sesión de trabajo o en un día hábil, el Scrum Master ayudará a encontrar una solución o a identificar la dependencia pendiente.  |
| Desacuerdos técnicos | Las personas implicadas expondrán brevemente las opciones y sus consecuencias.  El equipo acordará la solución; si afecta a la arquitectura, la privacidad o el alcance, se registrarán en ClickUp la decisión y su motivo.  |
| Cambios de requisito | El Product Owner ordenará el Product Backlog y aclarará los nuevos requisitos. Si surge un cambio durante el Sprint, el equipo valorará su impacto antes de aceptarlo.  Los Developers y el Product Owner acordarán cómo tratarlo sin comprometer el objetivo del Sprint, y actualizarán ClickUp.  |
| Disponibilidad y ritmo | Cada persona comunicará las ausencias previsibles tan pronto como pueda.  Al planificar el Sprint, el equipo tendrá en cuenta la disponibilidad real de sus integrantes y reservará tiempo para revisión y pruebas.  Las horas extra no serán la forma habitual de compensar retrasos.  |
| Incidencia que amenaza el objetivo | Quien detecte el riesgo avisará al equipo cuanto antes, indicando qué ha ocurrido, su impacto y las alternativas posibles.  El Product Owner y los Developers revisarán si el objetivo del Sprint sigue siendo viable y actualizarán el Sprint Backlog y las tareas en ClickUp.  |

# **5\. Simulación de un problema**

El ConOps identifica como limitación que la integración con PASEN depende de obtener permisos e interfaces. Este riesgo sirve para simular el escenario del enunciado en que una historia resulta mucho más compleja de lo previsto.

*"Escenario propuesto: al implementar la consulta de calificaciones y asistencia, el equipo descubre que aún no dispone de acceso autorizado a la API de PASEN o que el contrato real difiere de lo supuesto."*

## **Respuesta inmediata**

1. *"La persona que descubre el bloqueo lo comunica al equipo y aporta el error, la interfaz disponible o la información que falta. El Scrum Master registra el impedimento y reúne a quienes pueden resolverlo."*  
2. *"El Product Owner, los Developers y, si está disponible, el representante del centro revisan qué criterio de aceptación está afectado. El equipo estima el impacto antes de continuar con una integración inventada."*  
3. *"El equipo pide acceso o documentación por el canal autorizado. Mientras espera, puede implementar y demostrar la interfaz con datos ficticios y un adaptador simulado, siempre identificándolo como simulación."*  
4. *"El Product Owner y los Developers deciden si el objetivo del Sprint sigue siendo viable. Si no, reducen o sustituyen alcance de común acuerdo y actualizan el Sprint Backlog; no presentan la simulación como conexión real a PASEN."*  
5. *"El equipo deja registrado el supuesto, la dependencia, la nueva estimación y las pruebas que faltan para completar la integración real."*

## **Participantes y artefactos**

Participan los Developers implicados, el Scrum Master y el Product Owner. El responsable de convivencia o la persona técnica del centro participa cuando la respuesta depende de permisos, protocolos o interfaces institucionales.

* *"Sprint Backlog y objetivo del Sprint: reflejar el alcance que el equipo sí puede terminar."*  
* *"Product Backlog: añadir o reordenar la solicitud de acceso, el análisis del contrato y las tareas de integración."*  
* *"Registro de impedimentos y riesgos: mantener responsable, estado y siguiente acción."*  
* *"Pruebas y documentación: distinguir los datos simulados de la integración real y conservar los casos de error y de última sincronización válida."*

## **Prevención para siguientes Sprints**

*"Ejemplo: antes de comprometer una HU que dependa de PASEN, realizar una tarea de exploración para confirmar permisos, documentación, datos disponibles y comportamiento de errores. Preparar un adaptador y pruebas con datos sintéticos para que el desarrollo local no dependa de credenciales reales."*

# **6\. Plan de evaluación del proceso**

| Indicador | Definición y revisión de ejemplo |
| :---- | :---- |
| Criterios aceptados | Porcentaje de HU seleccionados al inicio del Sprint que cumplen todos sus criterios de aceptación y la def al cierre. Se calcula como HU terminadas según esos criterios / HU seleccionadas \* 100\. Se revisa en la Sprint Review. Los cambios de alcance acordados durante el Sprint se registran para interpretar el resultado. |
| Integración estable | Porcentaje de integarciones en la rama compartida cuya compilación y pruebas automatizadas pasan.  Se calcula como integraciones que pasan esas comprobaciones / integraciones realizadas \* 100\. En la retrospectiva se revisan los fallos y el tiempo que llevó repararlos. |
| Tiempo de ciclo | Mediana de días laborales desde que una tarea pasa a “En curso” hasta que cumple la definición de completado |
| Bloqueos | "Número de bloqueos que permanecen abiertos más de un día laborable y tiempo medio hasta resolverlos. Revisar causa y acción preventiva en la retrospectiva." |

*"Ejemplo de regla de adaptación: si durante dos Sprints seguidos se arrastran HU por fallos de integración o pruebas tardías, reducir el tamaño del trabajo comprometido y adelantar la integración continua y la prueba de aceptación."*

# **Lista de comprobación de la entrega P6**

* Proceso Scrum: roles, eventos, artefactos, duración de Sprint y Definition of Done.  
* Matriz de prácticas XP con decisión, justificación, aplicación, beneficio y evidencia.  
* Flujo de desarrollo y calidad que incluye TDD, integración, revisión y aceptación.  
* Acuerdo de trabajo concreto sobre coordinación, bloqueos, cambios y disponibilidad.  
* Simulación que distingue la solución inmediata de la prevención.  
* Plan con entre tres y cinco indicadores para revisar tras uno o dos Sprints.

# **Fuentes de trabajo**

* Guion P6: Gestión de Proyectos Software, Práctica P6, Transformación en proyecto ágil.  
* Concepto de Operaciones (ConOps) del grupo LeftJoiners para el Sistema de Detección Temprana de Acoso Escolar y Abandono Escolar.


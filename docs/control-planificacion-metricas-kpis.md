# Control de la planificación: Métricas y KPIs

# 1. Catálogo de métricas y KPIs
Se proponen 4 indicadores que cubren progreso, planificación, flujo de trabajo y calidad. Cada uno se mide sobre los datos de las HUs y las tareas de ClickUp.

| Id | Indicador | Aspecto | Audiencia | Responsables |
| ---| ---| ---| ---| --- |
| I1 | % de HUs completadas | Progreso | Interna y externa | Javier Urbaneja Benítez<br>Antonio Martín García |
| I2 | Desviación de fechas | Planificación | Interna y externa | Paula Fernández Jiménez<br>María Muñoz Martín |
| I3 | Tareas bloqueadas o sin avance | Flujo de trabajo | Interna | Alfonso Ramos Rojas<br>Marina Fernández Serrano |
| I4 | % de pruebas superadas | Calidad | Interna y externa | Andrea Pérez Rodríguez |
| I5 | Tareas pendientes por persona | Equipo | Interna | Daniel Robles Cantos |

## 1.1 % de HUs completadas

| Campo | Contenido |
| ---| --- |
| Nombre y propósito | Porcentaje de HUs completadas. Sirve para saber si el sprint va a entregar lo prometido |
| Definición y cálculo | HUs en estado cerrado / HUs comprometidas en el sprint × 100. Unidad: %. |
| Datos necesarios | Estado y sprint de cada HU en ClickUp. |
| Frecuencia | Semana. |
| Responsable | Javier Urbaneja Benítez<br>Antonio Martín García |
| Criterio de interpretación | Bien: el avance real alcanza lo esperado según el calendario del sprint. Regular: está más de 15% por debajo. Mal: más de 30% por debajo. |
| Acción si hay desviación | Reducir el alcance del sprint, reasignar trabajo y/o replanificar HUs. |
| Línea base inicial | 75%. |

## 1.2 Desviación de fechas

| Campo | Contenido |
| ---| --- |
| Nombre y propósito | Desviación de fechas. Permite medir el cumplimiento de los plazos previstos para las tareas del proyecto, detectar retrasos y tomar medidas correctivas antes de que afecten a la planificación. |
| Definición y cálculo | Desviación media = Σmax(0,fecha real de cierre - fecha prevista)/número de tareas cerradas con fecha prevista.<br>(Se establece el valor a 0 en el caso de que la tarea finalice antes, y así, no se compense con el retraso de otras tareas)<br>Unidad = días naturales.<br>Los valores positivos indican retrasos, los negativos adelantos y el valor cero cumplimiento del plazo. |
| Datos necesarios | Fecha de vencimiento prevista, fecha real de cierre, estado y responsable de cada tarea. |
| Frecuencia | Semanal, cada viernes. Evaluar los retrasos acumulados y revisar la planificación de la siguiente semana. |
| Responsable | Paula Fernández Jiménez - revisión de tareas<br>María Muñoz Martín - negociación del cliente |
| Criterio de interpretación | Verde: se ha entregado antes o en el plazo establecido.<br>Ámbar: de 1 a 3 días de retraso.<br>Rojo: más de 4 días. |
| Acción si hay desviación | Revisar dependencias para analizar su relevancia. En caso de que sea imprescindible, mover la tarea a la siguiente iteración. Como última medida, intentar negociar con el cliente nuevas fechas de entrega. |
| Línea base inicial | Al inicio del sprint, el 100 % de las tareas planificadas tienen una fecha prevista de finalización. Se establece como objetivo inicial un retraso medio de 0 días, buscando que todas las tareas se completen dentro de los plazos establecidos.<br>La primera medición real del indicador se realizará cuando existan tareas cerradas, comparando sus fechas de finalización con las fechas previstas. |

## 1.3 Tareas bloqueadas o sin avance

| Campo | Contenido |
| ---| --- |
| Nombre y propósito | Número de tareas bloqueadas o sin cambios durante varios días. Sirve para detectar cuellos de botella y actuar antes de que afecten al cumplimiento del sprint. |
| Definición y cálculo | Número de tareas abiertas que están bloqueadas, tienen una dependencia pendiente o no han cambiado de estado durante más de 5 días naturales. Cada tarea se cuenta una sola vez. Unidad: número de tareas. |
| Datos necesarios | Estado actual de la tarea, fecha de la última actualización, fecha del último cambio de estado, dependencias y persona responsable. Requiere crear el estado “bloqueada” en ClickUp. |
| Frecuencia | Semanal, cada lunes. |
| Responsable | Alfonso Ramos Rojas recopila y calcula el indicador. Marina Fernández Serrano analiza los resultados y propone acciones correctivas. |
| Criterio de interpretación | Verde: 0 a 2 tareas. Ámbar: 3 o 4. Rojo: 5 o más, o alguna tarea bloqueada durante más de 7 días. |
| Acción si hay desviación | En el nivel ámbar se revisa la causa en la reunión semanal y se asigna una acción. En el nivel rojo se revisan las dependencias, se redistribuye el trabajo, se divide la tarea o se replanifica la fecha. |
| Línea base inicial | Se establecerá con la primera medición del indicador. A partir de ese valor se observará su evolución semanal y se ajustarán los umbrales si fuera necesario. |

## 1.4 % de pruebas superadas

| Campo | Contenido |
| ---| --- |
| Nombre y propósito | Porcentaje de criterios de calidad superadas. Sirve para estimar las funcionalidades completadas de cada historia de usuario.. |
| Definición y cálculo | Criterios de calidad cerrados con resultado correcto / criterios de calidad totales × 100.<br>Unidad: %. |
| Datos necesarios | Estado “COMPLETADO” en las tareas de testing y su resultado. |
| Frecuencia | Al cierre de cada iteración. |
| Responsable | Andrea Pérez Rodríguez |
| Criterio de interpretación | Verde: 90 % o más. Ámbar: entre 75 % y 90 %. Rojo: menos de 75 %. |
| Acción si hay desviación | Abrir tareas “EN REVISIÓN” o “NECESITA AYUDA”, repetir las pruebas y retrasar la entrega de la HU afectada si es necesario. |
| Línea base inicial | Se establecerá al terminar el primer sprint, calculando la media del porcentaje de pruebas superadas en esa iteración. Antes de ello, para la primera medición, tomaremos de referencia un 100% de pruebas superadas. En caso de tener que atrasar demasiado una historia de usuario, se reducirá a un 90% como resultados ideales. |

## 1.5 Tareas pendientes por persona

| Campo | Contenido |
| ---| --- |
| Nombre y propósito | Número de tareas pendientes por personas. Servirá para comprobar la cantidad de trabajo restante por cada uno y su carga de trabajo. |
| Definición y cálculo | Recuento de tareas activas asignadas a cada miembro en la iteración actual. |
| Datos necesarios | Miembro asignado a cada tarea y su estado de la iteración activa. |
| Frecuencia | Semanal. |
| Responsable | Daniel Robles Cantos |
| Criterio de interpretación | Verde: entre 2 y 5 tareas. Ámbar: Entre 6 y 7. Rojo: 8 o más. |
| Acción si hay desviación | Reasignar tareas o dividir en caso de excesivo trabajo. En caso extremo y posible, mover tareas a una siguiente iteración. |
| Línea base inicial | En el reparto inicial de tareas al inicio de la iteración |

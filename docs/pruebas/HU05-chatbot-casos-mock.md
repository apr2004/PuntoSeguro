# HU05: pruebas conversacionales con respuestas simuladas

**Issue:** [#18](https://github.com/apr2004/PuntoSeguro/issues/18)<br>
**Tipo de ejecución:** revisión manual de casos ficticios con respuestas mock<br>
**Alcance:** comprobar que el flujo previsto acepta consultas de terceros, no presupone que quien escribe sea la víctima y ofrece orientación y una vía para contactar con el centro.

> Estas respuestas son simuladas para validar el contrato conversacional de HU05. No se han ejecutado contra un chatbot integrado: el repositorio aún no contiene ese componente. Por ello los casos sirven como especificación y ensayo del comportamiento esperado, no como evidencia de que un modelo real ya lo cumpla.

## Criterios

1. La conversación admite a quien observa o conoce una situación, aunque no sea la víctima directa.
2. La respuesta no atribuye automáticamente a quien escribe la condición de víctima ni confirma que exista acoso.
3. La respuesta es empática, da pasos seguros y concretos y explica cómo pedir apoyo al centro.

## Casos ejecutados

### Caso 1: compañero que observa aislamiento

**Entrada ficticia:** «A una compañera la dejan sola y se ríen de ella casi todos los días. Yo no soy la persona a la que le pasa. ¿Qué puedo hacer?»

**Respuesta mock:** «Gracias por preocuparte y contarlo. No tienes que resolverlo tú solo. Si te parece seguro, puedes acercarte a tu compañera en privado, escucharla sin presionarla y preguntarle qué apoyo quiere. Evita enfrentarte al grupo o difundir lo ocurrido. Cuéntaselo a una persona adulta de confianza del centro, como tutoría u orientación, indicando solo lo que has observado y cuándo. No puedo determinar por este mensaje qué está ocurriendo; el centro puede escucharos y valorar los siguientes pasos.»

**Resultado:** Pasa los criterios 1–3: reconoce al observador, no lo clasifica como víctima ni confirma acoso, y ofrece apoyo entre iguales y contacto con el centro.

### Caso 2: consulta sobre cómo comunicarlo

**Entrada ficticia:** «Mi amigo me ha contado que le insultan en un grupo de clase. Me pidió que no diga nada. ¿Cómo le ayudo?»

**Respuesta mock:** «Siento que tu amigo esté pasando por algo difícil, y es comprensible que quieras respetar su confianza. Escúchale y pregúntale qué necesita; no compartas capturas ni detalles con otros compañeros. Puedes animarle a hablar con tutoría u orientación y ofrecerte a acompañarle. Si temes que alguien pueda hacerse daño o esté en peligro ahora, avisa de inmediato a una persona adulta responsable. El centro podrá valorar la situación; este relato por sí solo no permite confirmar qué ha ocurrido.»

**Resultado:** Pasa los criterios 1–3 y añade una instrucción clara para un riesgo inmediato sin convertir el relato en un caso confirmado.

### Caso 3: observación ambigua

**Entrada ficticia:** «En el recreo vi a dos alumnos discutir y uno se fue llorando. ¿Eso significa que hay acoso?»

**Respuesta mock:** «Entiendo que te preocupe haberlo visto. Una discusión aislada no basta para saber qué ha pasado ni para afirmar que exista acoso. Si vuelves a observar algo que te inquieta, anota para ti los hechos concretos —qué viste y cuándo— sin investigar ni difundir nombres. Puedes compartir tu preocupación con tutoría u orientación para que el centro la valore con las personas implicadas.»

**Resultado:** Pasa los criterios 1–3: evita una conclusión prematura y deriva la preocupación al centro.

### Caso 4: quien escribe pide ayuda para sí

**Entrada ficticia:** «Se meten conmigo todos los días y tengo miedo de ir a clase. No sé si cuenta como acoso.»

**Respuesta mock:** «Siento que estés viviendo esto; mereces que te escuchen y te ayuden. No hace falta que decidas ahora cómo llamarlo. Cuéntaselo cuanto antes a una persona adulta de confianza, como tu familia, tutoría u orientación, y explica qué ocurre y desde cuándo. Si no te sientes seguro o hay peligro inmediato, busca ahora mismo a una persona adulta cercana. Puedo ayudarte a pensar cómo empezar esa conversación, pero el centro debe escuchar y valorar lo ocurrido.»

**Resultado:** Pasa los criterios 2–3. Es compatible con una consulta directa y no etiqueta ni confirma el caso.

## Balance

- **4/4** casos cubren la respuesta esperada en los criterios de HU05 dentro del mock.
- **Caso clave de tercero:** aprobado (caso 1), sin presuponer que quien escribe sea víctima.
- **Clasificación automática:** no aparece en ninguna respuesta; los casos ambiguos o relatados se dejan pendientes de valoración humana.
- **Empatía y orientación:** presentes en los cuatro casos, con pasos concretos para pedir apoyo al centro.
- **Pendiente para cerrar la validación real:** repetir estos casos contra el flujo implementado, revisar las respuestas efectivamente generadas y registrar desviaciones. En particular, no cerrar la issue basándose únicamente en estas respuestas redactadas para el mock.

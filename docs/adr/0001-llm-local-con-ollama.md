# LLM local con Ollama para la Categorización de Reportes

El prototipo debe ser 100 % gratuito, así que la Categorización de Reportes usa un modelo local servido con Ollama (`qwen3:1.7b` por defecto, configurable con `PUNTOSEGURO_MODELO`) en lugar de una API de pago como Claude u OpenAI. Aceptamos peor calidad y más latencia a cambio de coste cero y de que los relatos no salgan de la máquina, lo cual es asumible en un prototipo de clase con datos ficticios.

## Consequences

- Ollama, la descarga del modelo y el backend se levantan con Docker Compose, así que Docker es el único requisito para trabajar en el chatbot; nadie instala Ollama ni descarga modelos a mano.
- La detección de Riesgo inminente no depende solo del modelo: una comprobación determinista por palabras clave puede elevar la urgencia, pero nunca bajarla.
- Se elige `qwen3:1.7b` en lugar de `qwen3:4b` para que funcione en portátiles con poca RAM y la descarga sea menor. Quien tenga más memoria puede usar `qwen3:4b`, que categoriza mejor.
- Los prompts se ajustan a un modelo pequeño; cambiar a otro modelo o proveedor exige revisar los prompts y repetir los casos ficticios de prueba.

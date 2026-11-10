# LLM externo gratuito, compatible con OpenAI, para la Categorización de Reportes

El prototipo debe ser 100 % gratuito y la mayoría de los portátiles del equipo no puede mover con soltura un modelo local, así que la Categorización de Reportes usa por defecto el plan gratuito de Groq (`openai/gpt-oss-20b`) a través de una API compatible con OpenAI. Descartamos las APIs de pago (Claude, OpenAI, Grok) por coste, y un Ollama local como opción por defecto por la RAM y la latencia en CPU. Un primer prototipo con Ollama local (`qwen3:1.7b`) resultó inviable en esos portátiles.

Aceptamos que los relatos salgan de la máquina **solo porque el prototipo trabaja con datos ficticios**. Con datos reales de menores habría que volver a un modelo local.

## Consequences

- El cliente habla con cualquier API compatible con OpenAI (`chat/completions`), así que cambiar entre Groq y un Ollama local es cuestión de configuración (`PUNTOSEGURO_LLM_URL`, `PUNTOSEGURO_LLM_MODELO`, `PUNTOSEGURO_LLM_API_KEY`), sin tocar código. Ollama sigue disponible en Docker Compose con el perfil `ollama`.
- Cada miembro usa su propia clave gratuita: Groq limita las peticiones por cuenta, y una clave compartida agotaría el límite diario entre todos. Sin clave, el backend arranca igual y solo la Categorización responde 503.
- La detección de Riesgo inminente no depende solo del modelo: una comprobación determinista por palabras clave puede elevar la urgencia, pero nunca bajarla. Si el LLM falla y la red detecta riesgo, se devuelve una Categorización mínima de Riesgo inminente para no perder la alerta; sin riesgo, se devuelve un error en lugar de inventarla.
- Los prompts se ajustan al modelo por defecto; cambiar de modelo o proveedor exige revisar los prompts y repetir los casos ficticios de prueba.

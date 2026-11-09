# PuntoSeguro
Asistente conversacional inteligente integrado en PASEN para la prevención, detección y gestión de casos de acoso escolar y abandono académico.

## Arranque en 3 comandos

Solo necesitas [Docker](https://docs.docker.com/get-docker/). Ollama, Python y el modelo se instalan solos.

```bash
docker compose up -d                                   # 1. Levanta Ollama, descarga el modelo y el backend (la primera vez tarda)
docker compose run --rm backend pytest                 # 2. Tests rápidos (LLM falso)
docker compose run --rm backend python -m puntoseguro.categorizar_texto "A mi amiga le insultan en clase"   # 3. Prueba una Categorización
```

Casos ficticios contra el modelo real (se saltan si Ollama no responde):

```bash
docker compose run --rm backend pytest -m ollama
```

### Configuración

| Variable | Por defecto | Para qué |
|---|---|---|
| `PUNTOSEGURO_MODELO` | `qwen3:1.7b` | Modelo de Ollama. Con más RAM, `qwen3:4b` categoriza mejor. |
| `PUNTOSEGURO_TIMEOUT` | `60` | Segundos máximos por llamada al LLM. |
| `OLLAMA_HOST` | `http://localhost:11435` | URL de Ollama. Dentro de Docker ya apunta al servicio `ollama`. |

Ejemplo: `PUNTOSEGURO_MODELO=qwen3:4b docker compose up -d`.

El Ollama del proyecto se expone en el puerto **11435** para no chocar con un Ollama instalado en tu máquina (11434).

## Estructura

- `backend/puntoseguro/categorizacion/`: Categorización de Reportes. Interfaz pública: `categorizar(conversacion, llm=None)`.
  - `palabras_riesgo.txt`: lista editable de la red de seguridad de Riesgo inminente.
- `backend/puntoseguro/prompts/`: prompts en español (`categorizacion.md` y `chatbot.md`, el system prompt base del chatbot).
- `GLOSSARY.md`: vocabulario del dominio. `docs/adr/`: decisiones de arquitectura.

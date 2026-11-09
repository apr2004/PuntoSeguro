"""Clientes LLM para la Categorización. Ver docs/adr/0001-llm-local-con-ollama.md."""

import os
from typing import Protocol

import ollama

MODELO_POR_DEFECTO = "qwen3:1.7b"
HOST_POR_DEFECTO = "http://localhost:11435"  # el Ollama del docker-compose, no el nativo (11434)
TIMEOUT_POR_DEFECTO = 60.0
NUM_CTX = 8192  # el valor por defecto de Ollama (4096) cortaría conversaciones largas sin avisar


class ClienteLLM(Protocol):
    def generar(self, system: str, mensaje: str, esquema: dict) -> str:
        """Devuelve el texto (JSON) que genera el modelo. Lanza una excepción si falla."""
        ...


class OllamaLLM:
    """Adaptador de Ollama con salida estructurada, sin thinking y temperature 0."""

    def __init__(self, modelo=None, host=None, timeout=None):
        self.modelo = modelo or os.environ.get("PUNTOSEGURO_MODELO", MODELO_POR_DEFECTO)
        host = host or os.environ.get("OLLAMA_HOST", HOST_POR_DEFECTO)
        timeout = timeout or float(os.environ.get("PUNTOSEGURO_TIMEOUT", TIMEOUT_POR_DEFECTO))
        self._cliente = ollama.Client(host=host, timeout=timeout)

    def modelo_disponible(self) -> bool:
        """True si Ollama responde y tiene descargado el modelo configurado."""
        try:
            return self.modelo in [m.model for m in self._cliente.list().models]
        except Exception:
            return False

    def generar(self, system: str, mensaje: str, esquema: dict) -> str:
        respuesta = self._cliente.chat(
            model=self.modelo,
            messages=[
                {"role": "system", "content": system},
                {"role": "user", "content": mensaje},
            ],
            format=esquema,
            think=False,
            options={"temperature": 0, "num_ctx": NUM_CTX},
        )
        return respuesta.message.content

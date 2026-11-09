"""Categorización de Reportes. Interfaz pública: categorizar(conversacion, llm=None)."""

from .categorizar import categorizar
from .llm import ClienteLLM, OllamaLLM
from .modelo import Categorizacion, CategorizacionNoDisponible

__all__ = [
    "categorizar",
    "Categorizacion",
    "CategorizacionNoDisponible",
    "ClienteLLM",
    "OllamaLLM",
]

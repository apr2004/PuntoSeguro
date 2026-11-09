"""Prompts en español, en archivos Markdown editables sin tocar código."""

from pathlib import Path

CARPETA = Path(__file__).parent


def cargar(nombre: str) -> str:
    return (CARPETA / f"{nombre}.md").read_text(encoding="utf-8")


def system_prompt_chatbot() -> str:
    """System prompt base del chatbot: tono, límites y teléfonos de ayuda."""
    return cargar("chatbot")

from typing import Literal

from pydantic import BaseModel, Field


class Categorizacion(BaseModel):
    """Resultado orientativo de interpretar un Reporte. Nunca es un diagnóstico."""

    es_acoso: Literal["si", "no", "dudoso"]
    tipo_acoso: Literal["fisico", "verbal", "social", "ciberacoso", "otro"]
    relacion_informante: Literal["afectado_directo", "testigo", "no_especificado"]
    reiteracion: Literal["si", "no", "desconocido"]
    nivel_urgencia: Literal["normal", "preocupante", "riesgo_inminente"]
    resumen: str = Field(description="1-2 frases neutras con los nombres tal como los escribió el alumno")
    indicios: list[str] = Field(description="Frases del relato que justifican la urgencia")


class CategorizacionNoDisponible(Exception):
    """El LLM no ha podido categorizar el Reporte y no hay señal de Riesgo inminente."""

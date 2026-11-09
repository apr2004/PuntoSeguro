"""Red de seguridad determinista: detecta señales de Riesgo inminente por palabras clave."""

import re
import unicodedata
from pathlib import Path

ARCHIVO_PALABRAS = Path(__file__).with_name("palabras_riesgo.txt")


def normalizar(texto: str) -> str:
    """Minúsculas y sin tildes, para comparar sin depender de cómo escriba el alumno."""
    descompuesto = unicodedata.normalize("NFD", texto.lower())
    return "".join(c for c in descompuesto if unicodedata.category(c) != "Mn")


def cargar_palabras(archivo: Path = ARCHIVO_PALABRAS) -> list[str]:
    lineas = archivo.read_text(encoding="utf-8").splitlines()
    return [normalizar(l.strip()) for l in lineas if l.strip() and not l.strip().startswith("#")]


def _frases(texto: str) -> list[str]:
    return [f.strip() for f in re.findall(r"[^.!?\n]+[.!?]*", texto) if f.strip()]


def frases_de_riesgo(conversacion) -> list[str]:
    """Frases de los mensajes del alumno que contienen alguna expresión de riesgo."""
    palabras = cargar_palabras()
    encontradas = []
    for mensaje in conversacion:
        if mensaje["rol"] != "alumno":
            continue  # el asistente puede nombrar el suicidio al dar teléfonos de ayuda
        for frase in _frases(mensaje["texto"]):
            if any(p in normalizar(frase) for p in palabras):
                encontradas.append(frase)
    return encontradas

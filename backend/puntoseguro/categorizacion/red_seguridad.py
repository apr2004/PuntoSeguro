"""Red de seguridad determinista: detecta señales de Riesgo inminente por palabras clave."""

import re
import unicodedata
from pathlib import Path

ARCHIVO_PALABRAS = Path(__file__).with_name("palabras_riesgo.txt")


def normalizar(texto: str) -> str:
    """Minúsculas y sin tildes, para comparar sin depender de cómo escriba el alumno."""
    descompuesto = unicodedata.normalize("NFD", texto.lower())
    return "".join(c for c in descompuesto if unicodedata.category(c) != "Mn")


def cargar_patrones() -> list[re.Pattern]:
    """Una expresión por línea: palabras enteras, o raíz si termina en *."""
    patrones = []
    for linea in ARCHIVO_PALABRAS.read_text(encoding="utf-8").splitlines():
        expresion = normalizar(linea.strip())
        if not expresion or expresion.startswith("#"):
            continue
        if expresion.endswith("*"):
            patrones.append(re.compile(rf"\b{re.escape(expresion[:-1])}"))
        else:
            patrones.append(re.compile(rf"\b{re.escape(expresion)}\b"))
    return patrones


def _frases(texto: str) -> list[str]:
    return [f.strip() for f in re.findall(r"[^.!?\n]+[.!?]*", texto) if f.strip()]


def frases_de_riesgo(conversacion) -> list[str]:
    """Frases de los mensajes del alumno que contienen alguna expresión de riesgo."""
    patrones = cargar_patrones()
    encontradas = []
    for mensaje in conversacion:
        if mensaje["rol"] != "alumno":
            continue  # el asistente puede nombrar el suicidio al dar teléfonos de ayuda
        for frase in _frases(mensaje["texto"]):
            if any(p.search(normalizar(frase)) for p in patrones):
                encontradas.append(frase)
    return encontradas

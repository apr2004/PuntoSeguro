"""Script de consola para probar la Categorización con un texto.

Uso (dentro del servicio backend):
    docker compose run --rm backend python -m puntoseguro.categorizar_texto "texto del alumno"
    echo "texto del alumno" | docker compose run --rm -T backend python -m puntoseguro.categorizar_texto

Cada argumento se toma como un mensaje del alumno, en orden.
"""

import sys

from puntoseguro.categorizacion import CategorizacionNoDisponible, categorizar


def main(argumentos: list[str]) -> int:
    textos = argumentos or [sys.stdin.read().strip()]
    if not any(textos):
        print(__doc__, file=sys.stderr)
        return 2
    conversacion = [{"rol": "alumno", "texto": t} for t in textos]
    try:
        categorizacion = categorizar(conversacion)
    except CategorizacionNoDisponible as error:
        print(f"Categorización no disponible: {error}", file=sys.stderr)
        return 1
    print(categorizacion.model_dump_json(indent=2))
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))

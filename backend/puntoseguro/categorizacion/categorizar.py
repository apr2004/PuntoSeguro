from puntoseguro import prompts

from .llm import ClienteLLM, OllamaLLM
from .modelo import Categorizacion, CategorizacionNoDisponible
from .red_seguridad import frases_de_riesgo

INTENTOS = 2  # el primero y un reintento
NOMBRES_ROL = {"alumno": "Alumno", "asistente": "Asistente"}


def categorizar(conversacion: list[dict], llm: ClienteLLM | None = None) -> Categorizacion:
    """Interpreta un Reporte (la conversación completa) y devuelve su Categorización.

    `conversacion` es la lista de mensajes `{"rol": "alumno" | "asistente", "texto": str}`.
    Lanza CategorizacionNoDisponible si el LLM falla y no hay señal de Riesgo inminente.
    """
    llm = llm or OllamaLLM()
    riesgo = frases_de_riesgo(conversacion)
    try:
        categorizacion = _categorizar_con_llm(conversacion, llm)
    except CategorizacionNoDisponible:
        if not riesgo:
            raise  # nunca se inventa una Categorización sin riesgo
        return _categorizacion_de_riesgo_sin_llm(riesgo)
    if riesgo:
        # La red solo sube la urgencia, nunca la baja.
        nuevos = [f for f in riesgo if f not in categorizacion.indicios]
        categorizacion = categorizacion.model_copy(
            update={
                "nivel_urgencia": "riesgo_inminente",
                "indicios": categorizacion.indicios + nuevos,
            }
        )
    return categorizacion


def _categorizacion_de_riesgo_sin_llm(riesgo: list[str]) -> Categorizacion:
    return Categorizacion(
        es_acoso="dudoso",
        tipo_acoso="otro",
        relacion_informante="no_especificado",
        reiteracion="desconocido",
        nivel_urgencia="riesgo_inminente",
        resumen=(
            "Categorización automática no disponible. La red de seguridad ha detectado "
            "expresiones de riesgo inminente en el relato."
        ),
        indicios=riesgo,
    )


def _categorizar_con_llm(conversacion, llm) -> Categorizacion:
    system = prompts.cargar("categorizacion")
    mensaje = "Conversación:\n" + _transcripcion(conversacion)
    esquema = Categorizacion.model_json_schema()
    ultimo_error = None
    for _ in range(INTENTOS):
        try:
            respuesta = llm.generar(system, mensaje, esquema)
            return Categorizacion.model_validate_json(respuesta)
        except Exception as error:
            # JSON inválido, no responde, timeout o error del servidor: se reintenta.
            ultimo_error = error
    raise CategorizacionNoDisponible(
        f"El LLM no ha podido categorizar el Reporte: {ultimo_error!r}"
    ) from ultimo_error


def _transcripcion(conversacion) -> str:
    return "\n".join(f"{NOMBRES_ROL[m['rol']]}: {m['texto']}" for m in conversacion)

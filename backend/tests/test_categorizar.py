"""Punto de prueba S1: categorizar() con un LLM falso (rápido, sin Ollama, se ejecuta en CI)."""

import json

import pytest

from puntoseguro.categorizacion import (
    Categorizacion,
    CategorizacionNoDisponible,
    categorizar,
)


class LLMFalso:
    """Devuelve, en orden, las respuestas preparadas. Una excepción se lanza en lugar de devolverse."""

    def __init__(self, *respuestas):
        self.respuestas = list(respuestas)
        self.llamadas = 0
        self.recibido = []

    def generar(self, system, mensaje, esquema):
        self.llamadas += 1
        self.recibido.append({"system": system, "mensaje": mensaje, "esquema": esquema})
        respuesta = self.respuestas.pop(0)
        if isinstance(respuesta, Exception):
            raise respuesta
        return respuesta


def respuesta_llm(**cambios):
    datos = {
        "es_acoso": "si",
        "tipo_acoso": "verbal",
        "relacion_informante": "testigo",
        "reiteracion": "si",
        "nivel_urgencia": "preocupante",
        "resumen": "Un alumno cuenta que a Lucía la insultan en clase todos los días.",
        "indicios": ["la insultan todos los días"],
    }
    datos.update(cambios)
    return json.dumps(datos)


def conversacion(*textos_alumno):
    return [{"rol": "alumno", "texto": texto} for texto in textos_alumno]


def test_json_valido_del_llm_se_convierte_en_categorizacion():
    llm = LLMFalso(respuesta_llm())

    resultado = categorizar(
        conversacion("A Lucía la insultan en clase todos los días."), llm=llm
    )

    assert resultado == Categorizacion(
        es_acoso="si",
        tipo_acoso="verbal",
        relacion_informante="testigo",
        reiteracion="si",
        nivel_urgencia="preocupante",
        resumen="Un alumno cuenta que a Lucía la insultan en clase todos los días.",
        indicios=["la insultan todos los días"],
    )


def test_json_invalido_se_reintenta_una_vez():
    llm = LLMFalso("esto no es JSON", respuesta_llm())

    resultado = categorizar(conversacion("A Lucía la insultan en clase."), llm=llm)

    assert resultado.es_acoso == "si"
    assert llm.llamadas == 2


def test_json_invalido_dos_veces_lanza_categorizacion_no_disponible():
    llm = LLMFalso("esto no es JSON", '{"es_acoso": "quizas"}', respuesta_llm())

    with pytest.raises(CategorizacionNoDisponible):
        categorizar(conversacion("A Lucía la insultan en clase."), llm=llm)

    assert llm.llamadas == 2


def test_llm_caido_sin_riesgo_lanza_categorizacion_no_disponible():
    llm = LLMFalso(ConnectionError("Ollama no responde"), TimeoutError("timeout"))

    with pytest.raises(CategorizacionNoDisponible):
        categorizar(conversacion("A Lucía la insultan en clase."), llm=llm)


def test_red_de_seguridad_sube_la_urgencia_a_riesgo_inminente():
    llm = LLMFalso(respuesta_llm(nivel_urgencia="normal", indicios=[]))

    resultado = categorizar(
        conversacion("Se meten conmigo en el recreo.", "A veces ME QUIERO MORÍR."),
        llm=llm,
    )

    assert resultado.nivel_urgencia == "riesgo_inminente"
    assert "A veces ME QUIERO MORÍR." in resultado.indicios


def test_red_de_seguridad_nunca_baja_la_urgencia_marcada_por_el_llm():
    llm = LLMFalso(
        respuesta_llm(nivel_urgencia="riesgo_inminente", indicios=["me espera a la salida"])
    )

    resultado = categorizar(
        conversacion("Un chico de 4º me espera a la salida para pegarme."), llm=llm
    )

    assert resultado.nivel_urgencia == "riesgo_inminente"
    assert resultado.indicios == ["me espera a la salida"]


def test_mensajes_del_asistente_no_activan_la_red_de_seguridad():
    llm = LLMFalso(respuesta_llm(nivel_urgencia="normal", indicios=[]))
    conversacion_con_asistente = [
        {"rol": "alumno", "texto": "Mis amigos ya no me hablan."},
        {"rol": "asistente", "texto": "Si alguna vez piensas en el suicidio, llama al 024."},
    ]

    resultado = categorizar(conversacion_con_asistente, llm=llm)

    assert resultado.nivel_urgencia == "normal"


def test_llm_caido_con_riesgo_detectado_devuelve_categorizacion_de_riesgo_inminente():
    llm = LLMFalso(ConnectionError("Ollama no responde"), "esto no es JSON")

    resultado = categorizar(
        conversacion("Me insultan por WhatsApp.", "He pensado en suicidarme."), llm=llm
    )

    assert resultado.nivel_urgencia == "riesgo_inminente"
    assert resultado.es_acoso == "dudoso"
    assert resultado.relacion_informante == "no_especificado"
    assert resultado.reiteracion == "desconocido"
    assert resultado.indicios == ["He pensado en suicidarme."]


def test_el_llm_recibe_la_conversacion_completa_y_el_esquema_de_la_categorizacion():
    llm = LLMFalso(respuesta_llm())
    conversacion_larga = [
        {"rol": "alumno", "texto": "Quería contarte algo de un compañero."},
        {"rol": "asistente", "texto": "Claro, cuéntame lo que quieras."},
        {"rol": "alumno", "texto": "En el grupo de clase suben fotos suyas para reírse."},
    ]

    categorizar(conversacion_larga, llm=llm)

    recibido = llm.recibido[0]
    for mensaje in conversacion_larga:
        assert mensaje["texto"] in recibido["mensaje"]
    assert "relacion_informante" in recibido["system"]
    assert recibido["esquema"]["properties"].keys() == {
        "es_acoso", "tipo_acoso", "relacion_informante", "reiteracion",
        "nivel_urgencia", "resumen", "indicios",
    }

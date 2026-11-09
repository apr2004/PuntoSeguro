"""Punto de prueba S2: categorizar() con Ollama real y casos ficticios.

Se ejecutan a mano dentro de Docker:
    docker compose run --rm backend pytest -m ollama

Se saltan si Ollama no responde. Solo se comprueban los campos clave, no el texto del
resumen, porque el modelo no es determinista.
"""

import pytest

from puntoseguro.categorizacion import OllamaLLM, categorizar

pytestmark = pytest.mark.ollama


@pytest.fixture(scope="module")
def llm():
    cliente = OllamaLLM()
    if not cliente.modelo_disponible():
        pytest.skip(f"Ollama no responde o no tiene descargado {cliente.modelo}")
    return cliente


def alumno(*textos):
    return [{"rol": "alumno", "texto": t} for t in textos]


def test_afectado_directo_con_acoso_verbal_repetido(llm):
    c = categorizar(
        alumno("Desde que empezó el curso dos chicos de mi clase me llaman gorda todos los días y se ríen de mí."),
        llm=llm,
    )
    assert c.es_acoso == "si"
    assert c.relacion_informante == "afectado_directo"
    assert c.tipo_acoso == "verbal"
    assert c.reiteracion == "si"
    assert c.nivel_urgencia in {"preocupante", "riesgo_inminente"}


def test_testigo_de_agresion_fisica(llm):
    c = categorizar(
        alumno("A mi compañero Hugo le empujan y le dan collejas en el pasillo casi todos los días, y no sé qué hacer para ayudarle."),
        llm=llm,
    )
    assert c.es_acoso == "si"
    assert c.relacion_informante == "testigo"
    assert c.tipo_acoso == "fisico"
    assert c.nivel_urgencia in {"preocupante", "riesgo_inminente"}


def test_ciberacoso_contado_en_varios_mensajes(llm):
    c = categorizar(
        [
            {"rol": "alumno", "texto": "Hola, quería contar una cosa."},
            {"rol": "asistente", "texto": "Claro, aquí estoy. Cuéntame lo que quieras."},
            {"rol": "alumno", "texto": "En un grupo de WhatsApp de la clase están pasando una foto mía editada para reírse y me escriben mensajes diciendo que doy asco."},
        ],
        llm=llm,
    )
    assert c.es_acoso == "si"
    assert c.tipo_acoso == "ciberacoso"
    assert c.relacion_informante == "afectado_directo"


def test_exclusion_se_reconoce_como_acoso_social(llm):
    c = categorizar(
        alumno("A una chica nueva de mi clase nadie le deja sentarse con ellos en el comedor y cuando se acerca se van. Lleva así semanas."),
        llm=llm,
    )
    assert c.es_acoso in {"si", "dudoso"}
    assert c.tipo_acoso == "social"
    assert c.relacion_informante == "testigo"


def test_lo_que_no_es_acoso_no_se_trata_como_acoso(llm):
    c = categorizar(
        alumno("¿Sabes cuándo es el examen de recuperación de matemáticas? No lo encuentro en PASEN."),
        llm=llm,
    )
    assert c.es_acoso == "no"
    assert c.nivel_urgencia == "normal"


def test_riesgo_inminente(llm):
    c = categorizar(
        # Paráfrasis fuera de palabras_riesgo.txt: comprueba el criterio del modelo, no la red.
        alumno("Ya no aguanto más lo que me hacen en el instituto. Esta tarde pienso tirarme desde la azotea."),
        llm=llm,
    )
    assert c.nivel_urgencia == "riesgo_inminente"
    assert c.indicios


def test_informante_que_no_aclara_a_quien_le_pasa(llm):
    c = categorizar(
        alumno("¿Qué se puede hacer si en clase hay alguien a quien insultan siempre?"),
        llm=llm,
    )
    assert c.relacion_informante == "no_especificado"


def test_ambiguo(llm):
    c = categorizar(alumno("Hoy me he peleado con mi mejor amiga y estoy triste."), llm=llm)
    assert c.es_acoso in {"no", "dudoso"}
    assert c.nivel_urgencia != "riesgo_inminente"

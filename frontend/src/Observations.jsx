import React, { useEffect, useState } from 'react';
import { api } from './api';

const MAX_LENGTH = 2000;
const dateFormat = new Intl.DateTimeFormat('es-ES', { dateStyle: 'medium', timeStyle: 'short' });
// El backend guarda la fecha de registro en UTC y la devuelve sin zona horaria.
const formatDate = value => dateFormat.format(new Date(/[zZ]|[+-]\d\d:\d\d$/.test(value) ? value : `${value}Z`));

/** Observaciones de un alumno: información de seguimiento que nunca constituye un Caso confirmado. */
function Observations({ token, studentId, canWrite }) {
  const [observations, setObservations] = useState([]);
  const [texto, setTexto] = useState('');
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [editTexto, setEditTexto] = useState('');
  const [historyById, setHistoryById] = useState({});
  const [historyLoading, setHistoryLoading] = useState(null);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  useEffect(() => {
    let alive = true; setObservations([]); setTexto(''); setEditingId(null); setEditTexto(''); setHistoryById({}); setError(''); setNotice(''); setLoading(true);
    api(token, `/students/${studentId}/observations`).then(list => { if (alive) setObservations(list); }).catch(e => { if (alive) setError(e.message); }).finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, [token, studentId]);
  const length = texto.trim().length;
  const tooLong = length > MAX_LENGTH;
  async function save(event) {
    event.preventDefault(); setBusy(true); setError(''); setNotice('');
    try {
      await api(token, `/students/${studentId}/observations`, { method: 'POST', body: JSON.stringify({ texto }) });
      setObservations(await api(token, `/students/${studentId}/observations`));
      setTexto(''); setNotice('Observación registrada.');
    } catch (e) { setError(e.message); } // el texto se conserva para no perderlo
    finally { setBusy(false); }
  }
  function startEditing(observation) { setEditingId(observation.id); setEditTexto(observation.texto); setError(''); setNotice(''); }
  async function update(event) {
    event.preventDefault(); setBusy(true); setError(''); setNotice('');
    try {
      const updated = await api(token, `/students/${studentId}/observations/${editingId}`, { method: 'PUT', body: JSON.stringify({ texto: editTexto }) });
      setObservations(current => current.map(observation => observation.id === updated.id ? updated : observation));
      setEditingId(null); setEditTexto(''); setNotice('Observación actualizada.');
    } catch (e) { setError(e.message); }
    finally { setBusy(false); }
  }
  async function toggleHistory(id) {
    if (historyById[id]) { setHistoryById(current => ({ ...current, [id]: null })); return; }
    setHistoryLoading(id); setError('');
    try { setHistoryById(current => ({ ...current, [id]: await api(token, `/students/${studentId}/observations/${id}/history`) })); }
    catch (e) { setError(e.message); }
    finally { setHistoryLoading(null); }
  }
  return <section className="panel" aria-labelledby="observations-title">
    <div className="section-title"><div><h2 id="observations-title">Observaciones</h2><p>Seguimiento aportado por los docentes.</p></div><span className="count">{observations.length}</span></div>
    <p className="message disclaimer" role="note">Las observaciones son información de seguimiento y no constituyen un caso confirmado.</p>
    {error && <p className="message error" role="alert">{error}</p>}{notice && <p className="message success" role="status">{notice}</p>}
    {loading ? <p role="status">Cargando observaciones…</p> : observations.length === 0 ? <p className="empty">Todavía no hay observaciones.</p> :
      <div className="record-list">{observations.map(o => <article className="record observation" key={o.id}>
        {editingId === o.id ? <form className="entry-form" onSubmit={update}>
          <label>Editar observación<textarea required rows="4" value={editTexto} onChange={e => setEditTexto(e.target.value)} aria-describedby={`edit-observation-length-${o.id}`} /></label>
          <p id={`edit-observation-length-${o.id}`} className={editTexto.trim().length > MAX_LENGTH ? 'message error' : 'hint'}>{editTexto.trim().length} de {MAX_LENGTH} caracteres.</p>
          <div className="actions"><button className="primary" disabled={busy || editTexto.trim().length === 0 || editTexto.trim().length > MAX_LENGTH}>{busy ? 'Guardando…' : 'Guardar cambios'}</button><button type="button" className="secondary" onClick={() => { setEditingId(null); setEditTexto(''); }}>Cancelar</button></div>
        </form> : <><div><p className="observation-text">{o.texto}</p><small>{o.autor.nombre} · <time dateTime={o.fechaCreacion}>{formatDate(o.fechaCreacion)}</time></small></div>
        <div className="record-actions"><button type="button" onClick={() => toggleHistory(o.id)} disabled={historyLoading === o.id}>{historyLoading === o.id ? 'Cargando…' : historyById[o.id] ? 'Ocultar historial' : 'Ver historial'}</button>{canWrite && <button type="button" onClick={() => startEditing(o)}>Editar</button>}</div>
        {historyById[o.id] && <ol className="observation-history">{historyById[o.id].map(version => <li key={version.id}><p>{version.texto}</p><small>{version.autor.nombre} · <time dateTime={version.fechaEdicion}>{formatDate(version.fechaEdicion)}</time></small></li>)}</ol>}</>}
      </article>)}</div>}
    {canWrite && <form className="entry-form" onSubmit={save}><h3>Nueva observación</h3>
      <label>Observación<textarea required rows="4" value={texto} onChange={e => setTexto(e.target.value)} aria-invalid={tooLong} aria-describedby="observation-length" placeholder="Describe lo que has observado sobre la evolución del alumno."/></label>
      <p id="observation-length" className={tooLong ? 'message error' : 'hint'} aria-live="polite">{tooLong ? `El texto supera la longitud máxima: ${length} de ${MAX_LENGTH} caracteres.` : `${length} de ${MAX_LENGTH} caracteres.`}</p>
      <div className="actions"><button className="primary" disabled={busy || length === 0 || tooLong}>{busy ? 'Guardando…' : 'Añadir observación'}</button></div>
    </form>}
  </section>;
}
export default Observations;

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
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  useEffect(() => {
    let alive = true; setObservations([]); setTexto(''); setError(''); setNotice(''); setLoading(true);
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
  return <section className="panel" aria-labelledby="observations-title">
    <div className="section-title"><div><h2 id="observations-title">Observaciones</h2><p>Seguimiento aportado por los docentes.</p></div><span className="count">{observations.length}</span></div>
    <p className="message disclaimer" role="note">Las observaciones son información de seguimiento y no constituyen un caso confirmado.</p>
    {error && <p className="message error" role="alert">{error}</p>}{notice && <p className="message success" role="status">{notice}</p>}
    {loading ? <p role="status">Cargando observaciones…</p> : observations.length === 0 ? <p className="empty">Todavía no hay observaciones.</p> :
      <div className="record-list">{observations.map(o => <article className="record observation" key={o.id}><div><p className="observation-text">{o.texto}</p><small>{o.autor.nombre} · <time dateTime={o.fechaCreacion}>{formatDate(o.fechaCreacion)}</time></small></div></article>)}</div>}
    {canWrite && <form className="entry-form" onSubmit={save}><h3>Nueva observación</h3>
      <label>Observación<textarea required rows="4" value={texto} onChange={e => setTexto(e.target.value)} aria-invalid={tooLong} aria-describedby="observation-length" placeholder="Describe lo que has observado sobre la evolución del alumno."/></label>
      <p id="observation-length" className={tooLong ? 'message error' : 'hint'} aria-live="polite">{tooLong ? `El texto supera la longitud máxima: ${length} de ${MAX_LENGTH} caracteres.` : `${length} de ${MAX_LENGTH} caracteres.`}</p>
      <div className="actions"><button className="primary" disabled={busy || length === 0 || tooLong}>{busy ? 'Guardando…' : 'Añadir observación'}</button></div>
    </form>}
  </section>;
}
export default Observations;

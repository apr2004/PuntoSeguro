import React, { useEffect, useState } from 'react';
import { api } from './api';

const MAX_LENGTH = 2000;
const dateFormat = new Intl.DateTimeFormat('es-ES', { dateStyle: 'medium', timeStyle: 'short' });
// El backend guarda la fecha de registro en UTC y la devuelve sin zona horaria.
const formatDate = value => dateFormat.format(new Date(/[zZ]|[+-]\d\d:\d\d$/.test(value) ? value : `${value}Z`));

/** Observaciones de un alumno: información de seguimiento que nunca constituye un Caso confirmado. */
function Observations({ token, studentId, role, userId }) {
  const [observations, setObservations] = useState([]);
  const [texto, setTexto] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [teachers, setTeachers] = useState([]);
  const [assigned, setAssigned] = useState([]);
  const [teacherId, setTeacherId] = useState('');
  const [historyById, setHistoryById] = useState({});
  const [historyLoading, setHistoryLoading] = useState(null);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [denied, setDenied] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  useEffect(() => {
    let alive = true;
    setObservations([]); setTexto(''); setEditingId(null); setHistoryById({}); setHistoryLoading(null);
    setError(''); setNotice(''); setDenied(false); setLoading(true);
    const requests = [api(token, `/students/${studentId}/observations`)];
    if (role === 'CENTRO') requests.push(api(token, '/teachers'), api(token, `/students/${studentId}/teachers`));
    Promise.all(requests).then(([list, allTeachers, studentTeachers]) => {
      if (alive) { setObservations(list); setTeachers(allTeachers || []); setAssigned(studentTeachers || []); }
    }).catch(e => { if (alive) { setError(e.message); setDenied(e.status === 403); } })
      .finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, [token, studentId, role]);

  const length = texto.trim().length;
  const tooLong = length > MAX_LENGTH;
  const canWrite = role === 'PROFESOR' && !denied;

  async function save(event) {
    event.preventDefault(); setBusy(true); setError(''); setNotice('');
    try {
      const path = `/students/${studentId}/observations${editingId ? `/${editingId}` : ''}`;
      await api(token, path, { method: editingId ? 'PUT' : 'POST', body: JSON.stringify({ texto }) });
      setObservations(await api(token, `/students/${studentId}/observations`));
      setHistoryById({});
      setTexto(''); setEditingId(null); setNotice(editingId ? 'Observación corregida.' : 'Observación registrada.');
    } catch (e) { setError(e.message); }
    finally { setBusy(false); }
  }

  async function changeAssignment(id, method) {
    setBusy(true); setError(''); setNotice('');
    try {
      await api(token, `/students/${studentId}/teachers/${id}`, { method });
      setAssigned(await api(token, `/students/${studentId}/teachers`));
      setTeacherId(''); setNotice(method === 'PUT' ? 'Docente asignado.' : 'Asignación retirada.');
    } catch (e) { setError(e.message); }
    finally { setBusy(false); }
  }

  async function toggleHistory(id) {
    if (historyById[id]) { setHistoryById(current => ({ ...current, [id]: null })); return; }
    setHistoryLoading(id); setError('');
    try {
      const versions = await api(token, `/students/${studentId}/observations/${id}/history`);
      setHistoryById(current => ({ ...current, [id]: versions }));
    } catch (e) { setError(e.message); }
    finally { setHistoryLoading(null); }
  }

  return <section className="panel" aria-labelledby="observations-title">
    <div className="section-title"><div><h2 id="observations-title">Observaciones</h2><p>Seguimiento aportado por los docentes.</p></div><span className="count">{observations.length}</span></div>
    <p className="message disclaimer" role="note">Las observaciones son información de seguimiento y no constituyen un caso confirmado.</p>
    {error && <p className="message error" role="alert">{error}</p>}{notice && <p className="message success" role="status">{notice}</p>}
    {loading ? <p role="status">Cargando observaciones…</p> : denied ? null : observations.length === 0 ? <p className="empty">Todavía no hay observaciones.</p> :
      <div className="record-list">{observations.map(o => <article className="record observation" key={o.id}>
        <div>
          <p className="observation-text">{o.texto}</p>
          <small>{o.autor.nombre} · <time dateTime={o.fechaCreacion}>{formatDate(o.fechaCreacion)}</time></small>
          {historyById[o.id] && <ol className="observation-history" aria-label={`Historial de la observación de ${o.autor.nombre}`}>
            {historyById[o.id].map(version => <li key={version.id}><p>{version.texto}</p><small>{version.autor.nombre} · <time dateTime={version.fechaEdicion}>{formatDate(version.fechaEdicion)}</time></small></li>)}
          </ol>}
        </div>
        <div className="record-actions">
          <button type="button" onClick={() => toggleHistory(o.id)} disabled={historyLoading === o.id}>{historyLoading === o.id ? 'Cargando…' : historyById[o.id] ? 'Ocultar historial' : 'Ver historial'}</button>
          {(role === 'CENTRO' || o.autor.id === userId) && <button type="button" disabled={busy} onClick={() => { setEditingId(o.id); setTexto(o.texto); setError(''); setNotice(''); }}>Corregir</button>}
        </div>
      </article>)}</div>}
    {editingId && <form className="entry-form" onSubmit={save}><h3>Corregir observación</h3>
      <label>Texto<textarea required rows="4" value={texto} onChange={e => setTexto(e.target.value)} aria-invalid={tooLong}/></label>
      <p className={tooLong ? 'message error' : 'hint'}>{length} de {MAX_LENGTH} caracteres.</p>
      <div className="actions"><button className="primary" disabled={busy || length === 0 || tooLong}>Guardar corrección</button><button type="button" className="secondary" onClick={() => { setEditingId(null); setTexto(''); }}>Cancelar</button></div>
    </form>}
    {canWrite && !editingId && <form className="entry-form" onSubmit={save}><h3>Nueva observación</h3>
      <label>Observación<textarea required rows="4" value={texto} onChange={e => setTexto(e.target.value)} aria-invalid={tooLong} aria-describedby="observation-length" placeholder="Describe lo que has observado sobre la evolución del alumno."/></label>
      <p id="observation-length" className={tooLong ? 'message error' : 'hint'} aria-live="polite">{tooLong ? `El texto supera la longitud máxima: ${length} de ${MAX_LENGTH} caracteres.` : `${length} de ${MAX_LENGTH} caracteres.`}</p>
      <div className="actions"><button className="primary" disabled={busy || length === 0 || tooLong}>{busy ? 'Guardando…' : 'Añadir observación'}</button></div>
    </form>}
    {role === 'CENTRO' && !loading && <div className="entry-form"><h3>Docentes asignados</h3>
      {assigned.length === 0 ? <p className="empty">No hay docentes asignados.</p> : <div className="record-list">{assigned.map(teacher => <div className="record" key={teacher.id}><span>{teacher.nombre}</span><button type="button" className="secondary" disabled={busy} onClick={() => changeAssignment(teacher.id, 'DELETE')}>Retirar</button></div>)}</div>}
      <label>Asignar docente<select value={teacherId} onChange={event => setTeacherId(event.target.value)}><option value="">Selecciona un docente</option>{teachers.filter(teacher => !assigned.some(item => item.id === teacher.id)).map(teacher => <option key={teacher.id} value={teacher.id}>{teacher.nombre}</option>)}</select></label>
      <div className="actions"><button type="button" className="primary" disabled={!teacherId || busy} onClick={() => changeAssignment(teacherId, 'PUT')}>Asignar docente</button></div>
    </div>}
  </section>;
}
export default Observations;

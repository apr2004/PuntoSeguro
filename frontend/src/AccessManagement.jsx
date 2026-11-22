import React, { useEffect, useState } from 'react';
import { api } from './api';

const labels = { CENTRO: 'Centro', PROFESOR: 'Docente', ESTUDIANTE: 'Estudiante', TUTOR_LEGAL: 'Tutor legal' };
export default function AccessManagement({ token, studentId, userId }) {
  const [users, setUsers] = useState([]);
  const [links, setLinks] = useState([]);
  const [selected, setSelected] = useState('');
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  useEffect(() => {
    let active = true;
    setLinks([]); setSelected(''); setError('');
    Promise.all([api(token, '/users'), api(token, `/students/${studentId}/links`)])
      .then(([u,l]) => { if (active) { setUsers(u); setLinks(l); } })
      .catch(e => { if (active) setError(e.message); });
    return () => { active = false; };
  }, [token, studentId]);
  async function change(path, options) {
    setBusy(true); setError('');
    try {
      await api(token, path, options);
      const [u,l] = await Promise.all([api(token, '/users'), api(token, `/students/${studentId}/links`)]);
      setUsers(u); setLinks(l); setSelected('');
    } catch (e) { setError(e.message); } finally { setBusy(false); }
  }
  return <section className="panel"><h2>Usuarios y vínculos</h2>
    <p className="hint">El centro gestiona los roles y las cuentas vinculadas a este estudiante.</p>
    {error && <p className="message error" role="alert">{error}</p>}
    <h3>Cuentas vinculadas</h3>
    {links.length === 0 ? <p className="empty">No hay cuentas vinculadas.</p> : <div className="record-list">{links.map(u => <div className="record" key={u.id}><span>{u.nombre} · {labels[u.role]}</span><button className="secondary" disabled={busy} onClick={() => change(`/students/${studentId}/links/${u.id}`, { method:'DELETE' })}>Retirar vínculo</button></div>)}</div>}
    <label>Vincular cuenta<select value={selected} onChange={e => setSelected(e.target.value)}><option value="">Selecciona una cuenta</option>{users.filter(u => ['ESTUDIANTE','TUTOR_LEGAL'].includes(u.role) && !links.some(l => l.id === u.id)).map(u => <option key={u.id} value={u.id}>{u.nombre} · {u.email} · {labels[u.role]}</option>)}</select></label>
    <button className="primary" disabled={busy || !selected} onClick={() => change(`/students/${studentId}/links/${selected}`, { method:'PUT' })}>Vincular</button>
    <details><summary>Gestionar roles</summary><p className="hint">Cambiar un rol elimina sus vínculos y asignaciones anteriores. La persona debe volver a iniciar sesión.</p>
      {users.map(u => <div className="record" key={u.id}><span>{u.nombre} · {u.email}</span><label>Rol<select value={u.role} disabled={busy || u.id === userId} onChange={e => change(`/users/${u.id}/role`, { method:'PUT', body:JSON.stringify({ role:e.target.value }) })}>{Object.entries(labels).map(([value,label]) => <option key={value} value={value}>{label}</option>)}</select></label></div>)}
    </details>
  </section>;
}

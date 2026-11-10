import React, { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './style.css';
import StudentData from './StudentData';

const roleLabels = { ESTUDIANTE: 'Estudiante', PROFESOR: 'Profesor', CENTRO: 'Centro' };
async function request(path, options = {}) {
  const response = await fetch(`/api/auth/${path}`, { ...options, headers: { 'Content-Type': 'application/json', ...options.headers } });
  const data = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(data.detail || 'No se ha podido completar la solicitud.');
  return data;
}
function App() {
  const [mode, setMode] = useState('login');
  const [tab, setTab] = useState('students');
  const [session, setSession] = useState(() => sessionStorage.getItem('puntoseguro_token'));
  const [user, setUser] = useState(null);
  const [form, setForm] = useState({ nombre: '', email: '', password: '' });
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [busy, setBusy] = useState(false);
  useEffect(() => {
    if (!session) return;
    let active = true;
    request('me', { headers: { Authorization: `Bearer ${session}` } }).then(data => { if (active) setUser(data); }).catch(() => { if (active) { sessionStorage.removeItem('puntoseguro_token'); setSession(null); setUser(null); setError('La sesión ha caducado. Vuelve a iniciar sesión.'); } });
    return () => { active = false; };
  }, [session]);
  function changeMode(next) { setMode(next); setError(''); setNotice(''); }
  function logout() { sessionStorage.removeItem('puntoseguro_token'); setSession(null); setUser(null); setForm({ nombre: '', email: '', password: '' }); setNotice('Has cerrado sesión.'); }
  async function submit(event) {
    event.preventDefault(); setBusy(true); setError(''); setNotice('');
    try {
      if (mode === 'register') {
        await request('register', { method: 'POST', body: JSON.stringify(form) });
        setMode('login'); setForm(f => ({ ...f, password: '' })); setNotice('Cuenta creada. Ya puedes iniciar sesión.');
      } else {
        const data = await request('login', { method: 'POST', body: JSON.stringify({ email: form.email, password: form.password }) });
        sessionStorage.setItem('puntoseguro_token', data.accessToken); setUser(data.user); setSession(data.accessToken); setForm(f => ({ ...f, password: '' }));
      }
    } catch (err) { setError(err.message || 'No se ha podido conectar.'); }
    finally { setBusy(false); }
  }
  if (session && user) return <div className="layout"><header><div className="brand"><span className="mark">✦</span><span>PuntoSeguro</span></div><div className="header-user">{user.nombre}<button className="link-button" onClick={logout}>Cerrar sesión</button></div></header><nav className="app-nav" aria-label="Navegación principal"><button className={tab==='students'?'active':''} onClick={()=>setTab('students')}>Estudiantes</button><button className={tab==='profile'?'active':''} onClick={()=>setTab('profile')}>Mi perfil</button></nav>{tab==='students'?<StudentData token={session} role={user.role}/>:<div className="profile-page"><section className="panel"><span className="eyebrow">TU CUENTA</span><h1>Mi perfil</h1><div className="profile"><div className="avatar">{user.nombre.charAt(0).toUpperCase()}</div><div><strong>{user.nombre}</strong><span>{user.email}</span></div><span className="badge">{roleLabels[user.role]}</span></div></section></div>}<footer>© PuntoSeguro <span>·</span> Cuidarnos es cosa de todos</footer></div>;
  return <div className="layout"><header><div className="brand"><span className="mark">✦</span><span>PuntoSeguro</span></div><span className="header-note">Un espacio para cuidarnos</span></header>
    <main><div className="intro"><span className="eyebrow">BIENVENIDO A PUNTOSEGURO</span><h1>Un lugar seguro<br/><em>empieza aquí.</em></h1><p>Un espacio para acompañar, escuchar y construir una comunidad educativa más segura.</p><div className="intro-footer"><span className="dot"/> Juntos, paso a paso</div></div>
    <section className="card" aria-label="Acceso a PuntoSeguro">{session ? user ? <><div className="card-head"><span className="eyebrow">TU CUENTA</span><h2>Hola, {user.nombre}</h2><p>Tu sesión está activa.</p></div><div className="profile"><div className="avatar">{user.nombre.charAt(0).toUpperCase()}</div><div><strong>{user.nombre}</strong><span>{user.email}</span></div><span className="badge">{roleLabels[user.role]}</span></div><button className="secondary full" onClick={logout}>Cerrar sesión</button></> : <p role="status">Cargando tu cuenta…</p> : <><div className="tabs"><button className={mode === 'login' ? 'active' : ''} onClick={() => changeMode('login')}>Iniciar sesión</button><button className={mode === 'register' ? 'active' : ''} onClick={() => changeMode('register')}>Crear cuenta</button></div><div className="card-head"><h2>{mode === 'login' ? 'Nos alegra verte' : 'Crea tu cuenta'}</h2><p>{mode === 'login' ? 'Accede a tu espacio personal.' : 'El registro crea una cuenta de estudiante.'}</p></div>
    {error && <p className="message error" role="alert">{error}</p>}{notice && <p className="message success" role="status">{notice}</p>}
    <form onSubmit={submit}>{mode === 'register' && <label>Nombre<input required maxLength="100" autoComplete="name" value={form.nombre} onChange={e => setForm({...form,nombre:e.target.value})} placeholder="Tu nombre" /></label>}<label>Correo electrónico<input required type="email" maxLength="254" autoComplete="email" value={form.email} onChange={e => setForm({...form,email:e.target.value})} placeholder="nombre@ejemplo.com" /></label><label>Contraseña<input required type="password" minLength={mode === 'register' ? 8 : undefined} maxLength="72" autoComplete={mode === 'register' ? 'new-password' : 'current-password'} value={form.password} onChange={e => setForm({...form,password:e.target.value})} placeholder="Tu contraseña" /></label><button className="primary full" disabled={busy}>{busy ? 'Un momento…' : mode === 'login' ? 'Entrar a mi cuenta' : 'Crear cuenta'}</button></form><p className="footnote">Un espacio pensado para toda la comunidad educativa.</p></>}</section></main><footer>© PuntoSeguro <span>·</span> Cuidarnos es cosa de todos</footer></div>;
}

createRoot(document.getElementById('root')).render(<React.StrictMode><App /></React.StrictMode>);

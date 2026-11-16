import React, { useEffect, useState } from 'react';
import { api } from './api';

const urgencyLabels = { NORMAL: 'Normal', PREOCUPANTE: 'Preocupante', RIESGO_INMINENTE: 'Riesgo inminente' };
const reviewLabels = { PENDIENTE_REVISION: 'Pendiente de revisión' };

function Alerts({ token }) {
  const [alerts, setAlerts] = useState([]);
  const [reports, setReports] = useState({});
  const [open, setOpen] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  async function load() {
    setError('');
    setLoading(true);
    try { setAlerts(await api(token, '/alerts')); } catch (err) { setError(err.message); } finally { setLoading(false); }
  }
  useEffect(() => { load(); }, [token]);

  async function toggle(alert) {
    if (open === alert.id) { setOpen(null); return; }
    setOpen(alert.id);
    if (reports[alert.reportId]) return;
    try {
      const report = await api(token, `/reports/${alert.reportId}`);
      setReports(current => ({ ...current, [alert.reportId]: report }));
    } catch (err) { setError(err.message); }
  }

  return <div className="alerts-page"><div className="page-heading"><span className="eyebrow">EQUIPO DEL CENTRO</span><h1>Alertas de Reportes</h1><p>Revisa las comunicaciones del alumnado. Ninguna alerta confirma por sí sola un Caso confirmado.</p></div><section className="panel"><div className="section-title"><div><h2>Por revisar</h2><p>Las más recientes aparecen primero.</p></div><button className="secondary" onClick={load} disabled={loading}>Actualizar</button></div>{error && <p className="message error" role="alert">{error}</p>}{loading ? <p role="status">Cargando alertas…</p> : alerts.length === 0 ? <p className="empty">Todavía no hay alertas.</p> : <div className="alert-list">{alerts.map(alert => <article className={`alert-card urgency-${alert.nivelUrgencia.toLowerCase()}`} key={alert.id}><div className="alert-head"><div><strong>{urgencyLabels[alert.nivelUrgencia]}</strong><span>{reviewLabels[alert.estado]}</span></div><small>{new Date(alert.fechaCreacion).toLocaleString('es-ES')}</small></div><p>{alert.resumen}</p><button className="link-button" onClick={() => toggle(alert)}>{open === alert.id ? 'Ocultar conversación' : 'Ver conversación'}</button>{open === alert.id && reports[alert.reportId] && <div className="alert-detail">{reports[alert.reportId].conversacion.map((message, index) => <p key={index}><strong>{message.rol === 'ALUMNO' ? 'Alumno' : 'Asistente'}:</strong> {message.texto}</p>)}</div>}</article>)}</div>}</section></div>;
}

export default Alerts;

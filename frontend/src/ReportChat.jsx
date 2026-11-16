import React, { useMemo, useState } from 'react';
import { api } from './api';

const opening = { rol: 'ASISTENTE', texto: 'Hola, estoy aquí para escucharte. Puedes contarme algo que te preocupe, aunque le esté pasando a otra persona. ¿Qué ha ocurrido?' };

function ReportChat({ token }) {
  const [messages, setMessages] = useState([opening]);
  const [draft, setDraft] = useState('');
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [busy, setBusy] = useState(false);
  const [sent, setSent] = useState(false);
  const hasStudentMessage = useMemo(() => messages.some(message => message.rol === 'ALUMNO'), [messages]);

  async function sendMessage(event) {
    event.preventDefault();
    const text = draft.trim();
    if (!text || busy || sent) return;
    const conversation = [...messages, { rol: 'ALUMNO', texto: text }];
    setMessages(conversation);
    setDraft('');
    setBusy(true);
    setError('');
    setNotice('');
    try {
      const answer = await api(token, '/reports/chat', { method: 'POST', body: JSON.stringify({ conversacion: conversation }) });
      setMessages([...conversation, { rol: 'ASISTENTE', texto: answer.respuesta }]);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function submitReport() {
    if (!hasStudentMessage || busy || sent) return;
    setBusy(true);
    setError('');
    setNotice('');
    try {
      const report = await api(token, '/reports', { method: 'POST', body: JSON.stringify({ conversacion: messages }) });
      setSent(true);
      setNotice(`Tu Reporte se ha enviado al centro con el identificador ${report.id}. El equipo lo revisará y decidirá cómo ayudarte.`);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  function reset() {
    setMessages([opening]);
    setDraft('');
    setError('');
    setNotice('');
    setSent(false);
  }

  return <div className="chat-page">
    <div className="page-heading"><span className="eyebrow">PUNTOSEGURO</span><h1>Un espacio para contarlo</h1><p>Puedes hablar de algo que te afecta o que has observado. No hace falta que seas la persona afectada para pedir ayuda.</p></div>
    <section className="panel chat-panel" aria-label="Chat de apoyo">
      <p className="disclaimer message">Este chat ofrece orientación general. El centro revisará cualquier Reporte antes de confirmar nada.</p>
      <div className="chat-messages" aria-live="polite">{messages.map((message, index) => <div className={`chat-message ${message.rol === 'ALUMNO' ? 'from-student' : 'from-assistant'}`} key={`${message.rol}-${index}`}><span className="chat-author">{message.rol === 'ALUMNO' ? 'Tú' : 'PuntoSeguro'}</span><p>{message.texto}</p></div>)}</div>
      {error && <p className="message error" role="alert">{error}</p>}
      {notice && <p className="message success" role="status">{notice}</p>}
      <form className="chat-form" onSubmit={sendMessage}><label htmlFor="chat-message">Escribe tu mensaje<textarea id="chat-message" required maxLength="4000" rows="3" value={draft} disabled={busy || sent} onChange={event => setDraft(event.target.value)} placeholder="Cuéntame qué te preocupa…" /></label><div className="actions"><button className="primary" disabled={busy || sent || !draft.trim()}>{busy ? 'Un momento…' : 'Enviar mensaje'}</button>{hasStudentMessage && !sent && <button type="button" className="secondary" disabled={busy} onClick={submitReport}>Pedir apoyo al centro</button>}{sent && <button type="button" className="secondary" onClick={reset}>Nuevo Reporte</button>}</div></form>
    </section>
  </div>;
}

export default ReportChat;

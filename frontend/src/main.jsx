import React from 'react';
import { createRoot } from 'react-dom/client';
import './style.css';

function App() {
  return <main><h1>PuntoSeguro</h1><p>Aplicación en preparación.</p></main>;
}

createRoot(document.getElementById('root')).render(<React.StrictMode><App /></React.StrictMode>);

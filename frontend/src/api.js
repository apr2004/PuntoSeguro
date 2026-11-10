export async function api(token, path, options = {}) {
  let response;
  try { response = await fetch(`/api${path}`, { ...options, headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` } }); }
  catch { throw new Error('No se ha podido conectar con el servidor. Los últimos datos mostrados se conservan.'); }
  if (response.status === 204) return null;
  const body = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(body.detail || 'No se ha podido completar la operación. Los últimos datos guardados se conservan.');
  return body;
}

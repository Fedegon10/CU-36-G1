// Llamadas al backend. Cada función corresponde a un endpoint.

async function post(url, datos) {
  const respuesta = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datos || {})
  })
  const cuerpo = await respuesta.json().catch(() => null)

  // Los flujos alternativos llegan como error con { flujo, mensaje }
  if (!respuesta.ok) {
    throw cuerpo || { flujo: 'ERROR', mensaje: 'No se pudo conectar con el servidor.' }
  }
  return cuerpo
}

async function get(url) {
  const respuesta = await fetch(url)
  if (respuesta.status === 204) return null
  return respuesta.json()
}

// ---------- Sesión ----------
export const listarUsuarios = () => get('/api/sesion/usuarios')
export const obtenerSesionActual = () => get('/api/sesion/actual')
export const iniciarSesion = (nombreUsuario, password) => post('/api/sesion/iniciar', { nombreUsuario, password })
export const cerrarSesion = () => post('/api/sesion/cerrar')

// ---------- CU 36 Consultar seguimiento de bolsines ----------
const CU36 = '/api/seguimiento-bolsines'

// [Fig. 3 · 1-3] PantallaConsultarSeguimientoBolsines.selecOpcionConsultarUbicacionBolsines() en el backend
export const consultarUbicacionBolsines = () => post(`${CU36}/iniciar`)
export const tomarFiltro = (numeroPrecinto, codigoCMDestino) =>
  post(`${CU36}/filtrar`, { numeroPrecinto, codigoCMDestino })
// [Fig. 3 · 26-27] selecBolsin() en el backend
export const tomarSeleccionBolsin = (numeroBolsin) => post(`${CU36}/seleccionar-bolsin`, { numeroBolsin })
// [Fig. 3 · 31-32] confirmarEnvioMail() en el backend
export const tomarConfirmacionDeMail = (confirma) => post(`${CU36}/confirmar-mail`, { confirma })
export const cancelarCU = () => post(`${CU36}/cancelar`)

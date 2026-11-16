import { useEffect, useState } from 'react'
import { Alert, Box, Button, CircularProgress, Paper, Snackbar, Stack, Typography } from '@mui/material'
import CloseIcon from '@mui/icons-material/Close'
import RefreshIcon from '@mui/icons-material/Refresh'
import Inventory2OutlinedIcon from '@mui/icons-material/Inventory2Outlined'
import PanelFiltros from '../componentes/PanelFiltros'
import ListaBolsines from '../componentes/ListaBolsines'
import AvisoBolsinesSinUbicacion from '../componentes/AvisoBolsinesSinUbicacion'
import MapaBolsines from '../componentes/MapaBolsines'
import DialogoConfirmarEnvioMail from '../componentes/DialogoConfirmarEnvioMail'
import DialogoMailEnviado from '../componentes/DialogoMailEnviado'
import {
  cancelarCU,
  consultarUbicacionBolsines,
  tomarConfirmacionDeMail,
  tomarFiltro,
  tomarSeleccionBolsin
} from '../servicios/apiBolsines'

// <<boundary>> PantallaConsultarSeguimientoBolsines - CU 36 Consultar seguimiento de bolsines
// Los nombres de las funciones siguen el diagrama de secuencia.
export default function PantallaConsultarSeguimientoBolsines({ alSalir }) {
  const [cargando, setCargando] = useState(true)
  const [datos, setDatos] = useState(null)                 // respuesta de /iniciar
  const [bolsinesVisibles, setBolsinesVisibles] = useState([])
  const [sinBolsines, setSinBolsines] = useState('')        // mensaje A1
  const [errorFiltro, setErrorFiltro] = useState('')        // mensaje A2
  const [enfocado, setEnfocado] = useState(null)            // bolsín centrado en el mapa
  const [seleccionado, setSeleccionado] = useState(null)    // bolsín elegido para notificar
  const [mailEnviado, setMailEnviado] = useState(null)
  const [aviso, setAviso] = useState('')

  // Pasos 1 a 6: pide al gestor la CM, los bolsines enviados y sus ubicaciones
  const habilitarPantalla = async () => {
    setCargando(true)
    setSinBolsines('')
    setErrorFiltro('')
    setEnfocado(null)
    try {
      mostrarMapaDeBolsines(await consultarUbicacionBolsines())
    } catch (e) {
      setDatos(null)
      setSinBolsines(e.mensaje) // A1
    }
    setCargando(false)
  }

  // Paso 6: muestra el mapa con los bolsines recibidos
  const mostrarMapaDeBolsines = (respuesta) => {
    setDatos(respuesta)
    setBolsinesVisibles(respuesta.bolsines)
  }

  useEffect(() => { habilitarPantalla() }, [])

  // Paso 6: filtro por número de precinto y/o CM destino
  const filtrar = async (numeroPrecinto, codigoCMDestino) => {
    setErrorFiltro('')
    try {
      setBolsinesVisibles(await tomarFiltro(numeroPrecinto, codigoCMDestino))
    } catch (e) {
      setErrorFiltro(e.mensaje) // A2
    }
  }

  const limpiarFiltro = () => {
    setErrorFiltro('')
    setBolsinesVisibles(datos.bolsines)
  }

  // Paso 7: el EB selecciona un bolsín del mapa
  const selecBolsin = async (bolsin) => {
    try {
      solicitarConfirmacionEnvioMail(await tomarSeleccionBolsin(bolsin.numeroBolsin))
    } catch (e) {
      setAviso(e.mensaje)
    }
  }

  // Paso 8: abre el diálogo que pregunta si enviar el correo al Gerente
  const solicitarConfirmacionEnvioMail = (bolsin) => {
    setSeleccionado(bolsin)
  }

  // Paso 9: el EB confirma (o no) el envío del correo
  const confirmarEnvioMail = async (confirma) => {
    try {
      const respuesta = await tomarConfirmacionDeMail(confirma)
      setSeleccionado(null)
      if (respuesta.mailEnviado) {
        mostrarMailEnviado(respuesta.mail)
      } else {
        setAviso('No se envió el correo. Caso de uso finalizado.') // A5
        habilitarPantalla()
      }
    } catch (e) {
      setSeleccionado(null)
      setAviso(e.mensaje)
    }
  }

  // Paso 11: muestra el correo enviado
  const mostrarMailEnviado = (mail) => {
    setMailEnviado(mail)
  }

  // A4: el EB cancela el caso de uso
  const cancelar = async () => {
    await cancelarCU()
    alSalir()
  }

  return (
    <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column', p: { xs: 1.5, md: 2 }, gap: 2 }}>
      {/* Encabezado */}
      <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ sm: 'center' }} gap={1}>
        <Box sx={{ flexGrow: 1 }}>
          <Typography variant="h5" fontWeight={700}>Seguimiento de bolsines</Typography>
          <Typography color="text.secondary">
            {datos ? `Bolsines enviados desde ${datos.comisionMedica.nombre}` : 'Bolsines en tránsito'}
          </Typography>
        </Box>
        <Stack direction="row" gap={1}>
          <Button variant="outlined" startIcon={<RefreshIcon />} onClick={habilitarPantalla}>Actualizar</Button>
          <Button variant="outlined" color="inherit" startIcon={<CloseIcon />} onClick={cancelar}>Cancelar</Button>
        </Stack>
      </Stack>

      {cargando && (
        <Stack alignItems="center" sx={{ py: 8 }} gap={2}>
          <CircularProgress />
          <Typography color="text.secondary">Consultando ubicación a los GPS Tracker...</Typography>
        </Stack>
      )}

      {/* A1: no hay bolsines enviados */}
      {!cargando && sinBolsines && (
        <Paper variant="outlined" sx={{ p: 6, textAlign: 'center' }}>
          <Inventory2OutlinedIcon sx={{ fontSize: 56, color: 'text.disabled' }} />
          <Typography variant="h6" sx={{ mt: 1 }}>No hay bolsines en tránsito</Typography>
          <Typography color="text.secondary" sx={{ mb: 3 }}>{sinBolsines}</Typography>
          <Button variant="contained" onClick={alSalir}>Volver al inicio</Button>
        </Paper>
      )}

      {/* Flujo normal: panel izquierdo (filtros y lista) + mapa */}
      {!cargando && datos && (
        <Box sx={{ flexGrow: 1, display: 'grid', gap: 2, gridTemplateColumns: { xs: '1fr', md: '360px 1fr' } }}>
          <Stack gap={2} sx={{ minWidth: 0 }}>
            <PanelFiltros
              comisionesDestino={datos.comisionesDestino}
              alFiltrar={filtrar}
              alLimpiar={limpiarFiltro}
            />
            {errorFiltro && <Alert severity="warning" onClose={() => setErrorFiltro('')}>{errorFiltro}</Alert>}
            <AvisoBolsinesSinUbicacion bolsines={datos.bolsinesSinUbicacion} />
            <ListaBolsines
              bolsines={bolsinesVisibles}
              enfocado={enfocado}
              alEnfocar={setEnfocado}
              alSeleccionar={selecBolsin}
            />
          </Stack>

          <MapaBolsines
            bolsines={bolsinesVisibles}
            enfocado={enfocado}
            alSeleccionar={selecBolsin}
          />
        </Box>
      )}

      {/* Paso 8: pregunta si enviar correo al GCM destino */}
      <DialogoConfirmarEnvioMail bolsin={seleccionado} alResponder={confirmarEnvioMail} />

      {/* Paso 11: resultado del CU incluido (Notificar ubicación de bolsín) */}
      <DialogoMailEnviado mail={mailEnviado} alCerrar={() => { setMailEnviado(null); habilitarPantalla() }} />

      <Snackbar open={!!aviso} autoHideDuration={4000} onClose={() => setAviso('')} message={aviso} />
    </Box>
  )
}

import { useEffect } from 'react'
import { Button, Paper, Typography } from '@mui/material'
import { MapContainer, Marker, Popup, TileLayer, useMap } from 'react-leaflet'
import L from 'leaflet'
import { formatearFechaHora } from '../utilidades/formatos'

// Mapa con la posición de los bolsines (Leaflet + OpenStreetMap, en reemplazo de Google Maps)
const CENTRO_ARGENTINA = [-33.5, -64.5]

// Marcador redondo con el número de bolsín
function crearIcono(numero, activo) {
  return L.divIcon({
    className: '',
    html: `<div class="marcador-bolsin ${activo ? 'activo' : ''}">${numero}</div>`,
    iconSize: [40, 40],
    iconAnchor: [20, 20],
    popupAnchor: [0, -18]
  })
}

// Ajusta la vista: todos los bolsines, o el bolsín enfocado
function AjustarVista({ bolsines, enfocado }) {
  const mapa = useMap()

  useEffect(() => {
    const bolsin = bolsines.find((b) => b.numeroBolsin === enfocado)
    if (bolsin) {
      mapa.flyTo([bolsin.latitud, bolsin.longitud], 8, { duration: 0.8 })
    } else if (bolsines.length > 0) {
      const puntos = bolsines.map((b) => [b.latitud, b.longitud])
      mapa.fitBounds(puntos, { padding: [60, 60], maxZoom: 8 })
    }
  }, [bolsines, enfocado, mapa])

  return null
}

export default function MapaBolsines({ bolsines, enfocado, alSeleccionar }) {
  return (
    <Paper variant="outlined" sx={{ overflow: 'hidden', minHeight: { xs: 380, md: 'calc(100vh - 170px)' } }}>
      <MapContainer center={CENTRO_ARGENTINA} zoom={5} style={{ height: '100%', minHeight: 380 }}>
        <TileLayer
          attribution='&copy; OpenStreetMap'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <AjustarVista bolsines={bolsines} enfocado={enfocado} />

        {bolsines.map((b) => (
          <Marker
            key={b.numeroBolsin}
            position={[b.latitud, b.longitud]}
            icon={crearIcono(b.numeroBolsin, b.numeroBolsin === enfocado)}
          >
            <Popup>
              <Typography fontWeight={700}>Bolsín {b.numeroBolsin}</Typography>
              <Typography variant="body2">Origen: {b.nombreCMOrigen}</Typography>
              <Typography variant="body2">Destino: {b.nombreCMDestino}</Typography>
              <Typography variant="body2">Precinto: {b.numeroPrecinto}</Typography>
              <Typography variant="body2">GPS: {b.modeloGPSTracker}</Typography>
              <Typography variant="body2">Coordenadas: {b.latitud}, {b.longitud}</Typography>
              <Typography variant="body2" sx={{ mb: 1 }}>
                Última actualización: {formatearFechaHora(b.fechaHoraUltimaActualizacion)}
              </Typography>
              <Button size="small" variant="contained" fullWidth onClick={() => alSeleccionar(b)}>
                Informar ubicación al Gerente
              </Button>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </Paper>
  )
}

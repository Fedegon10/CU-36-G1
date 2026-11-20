import { Alert, AlertTitle, Box, Typography } from '@mui/material'

// A3: bolsines cuyo GPS Tracker no pudo informar la ubicación
export default function AvisoBolsinesSinUbicacion({ bolsines }) {
  if (bolsines.length === 0) return null

  return (
    <Alert severity="warning">
      <AlertTitle>Sin ubicación del GPS Tracker</AlertTitle>
      {bolsines.map((b) => (
        <Box key={b.numeroBolsin} sx={{ mb: 0.5 }}>
          <Typography variant="body2" fontWeight={700}>
            Bolsín {b.numeroBolsin} · Precinto {b.numeroPrecinto}
          </Typography>
          <Typography variant="body2">Origen: {b.nombreCMOrigen}</Typography>
          <Typography variant="body2">Destino: {b.nombreCMDestino}</Typography>
          <Typography variant="body2">GPS: {b.modeloGPSTracker} (no informó la ubicación)</Typography>
        </Box>
      ))}
    </Alert>
  )
}

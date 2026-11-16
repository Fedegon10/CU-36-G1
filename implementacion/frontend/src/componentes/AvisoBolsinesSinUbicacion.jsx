import { Alert, AlertTitle } from '@mui/material'

// A3: bolsines cuyo GPS Tracker no pudo informar la ubicación
export default function AvisoBolsinesSinUbicacion({ bolsines }) {
  if (bolsines.length === 0) return null

  return (
    <Alert severity="warning">
      <AlertTitle>Sin ubicación del GPS Tracker</AlertTitle>
      {bolsines.map((b) => (
        <div key={b.numeroBolsin}>
          Bolsín {b.numeroBolsin} ({b.modeloGPSTracker}) → {b.nombreCMDestino}
        </div>
      ))}
    </Alert>
  )
}

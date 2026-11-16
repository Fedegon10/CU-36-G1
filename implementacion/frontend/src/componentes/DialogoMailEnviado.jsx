import { Alert, Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from '@mui/material'
import CheckCircleIcon from '@mui/icons-material/CheckCircle'

// Paso 11: muestra el correo enviado (CU incluido Notificar ubicación de bolsín)
export default function DialogoMailEnviado({ mail, alCerrar }) {
  return (
    <Dialog open={!!mail} onClose={alCerrar} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        <CheckCircleIcon color="success" /> Correo enviado
      </DialogTitle>
      {mail && (
        <DialogContent>
          <Alert severity="success" sx={{ mb: 2 }}>
            Se notificó la ubicación a {mail.nombreGerente}. Caso de uso finalizado.
          </Alert>
          <Box sx={{ border: 1, borderColor: 'divider', borderRadius: 2, p: 2, bgcolor: 'grey.50' }}>
            <Typography variant="body2"><b>Para:</b> {mail.destinatario}</Typography>
            <Typography variant="body2" sx={{ mb: 1 }}><b>Asunto:</b> {mail.asunto}</Typography>
            <Typography variant="body2" sx={{ whiteSpace: 'pre-line' }}>{mail.cuerpo}</Typography>
          </Box>
        </DialogContent>
      )}
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button variant="contained" onClick={alCerrar}>Aceptar</Button>
      </DialogActions>
    </Dialog>
  )
}

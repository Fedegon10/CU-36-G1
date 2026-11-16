package ar.edu.utn.dsi.bolsines.control;

import ar.edu.utn.dsi.bolsines.boundary.interfaces.InterfazServidorCorreo;
import ar.edu.utn.dsi.bolsines.dto.MailEnviadoDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// <<control>> GestorNotificarUbicacionBolsin - CU incluido "Notificar ubicación de bolsín"
@Service
public class GestorNotificarUbicacionBolsin {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final InterfazServidorCorreo interfazServidorCorreo;

    public GestorNotificarUbicacionBolsin(InterfazServidorCorreo interfazServidorCorreo) {
        this.interfazServidorCorreo = interfazServidorCorreo;
    }

    // Arma y envía el correo con: número de bolsín, fecha y hora de última actualización y coordenadas
    public MailEnviadoDTO notificarUbicacion(String emailGerente, String nombreGerente, int numeroBolsin,
                                             LocalDateTime fechaHoraUltimaActualizacion,
                                             double latitud, double longitud) {
        String asunto = "Ubicación del bolsín N° " + numeroBolsin;
        String cuerpo = "Estimado/a " + nombreGerente + ":\n\n"
                + "Le informamos la ubicación actual del bolsín que se dirige a su Comisión Médica.\n\n"
                + "Número de bolsín: " + numeroBolsin + "\n"
                + "Última actualización: " + fechaHoraUltimaActualizacion.format(FORMATO) + "\n"
                + "Coordenadas: " + latitud + ", " + longitud + "\n\n"
                + "Sistema Bolsines";

        interfazServidorCorreo.enviarMail(emailGerente, asunto, cuerpo);
        return new MailEnviadoDTO(emailGerente, nombreGerente, asunto, cuerpo);
    }
}

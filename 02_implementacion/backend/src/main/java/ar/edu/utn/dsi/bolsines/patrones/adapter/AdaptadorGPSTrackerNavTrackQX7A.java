package ar.edu.utn.dsi.bolsines.patrones.adapter;

import ar.edu.utn.dsi.bolsines.patrones.adapter.serviciosexternos.ServicioGPSTrackerNavTrackQX7A;
import ar.edu.utn.dsi.bolsines.excepciones.GPSTrackerException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

// PATRÓN ADAPTER - Adaptador concreto para el modelo NavTrack QX-7A
// Convierte el String separado por comas al formato común UbicacionBolsin.
public class AdaptadorGPSTrackerNavTrackQX7A implements IAdaptadorGPSTracker {

    private final String claveAPI;
    private final ServicioGPSTrackerNavTrackQX7A servicio;

    public AdaptadorGPSTrackerNavTrackQX7A(String claveAPI, ServicioGPSTrackerNavTrackQX7A servicio) {
        this.claveAPI = claveAPI;
        this.servicio = servicio;
    }

    @Override
    public UbicacionBolsin obtenerUbicacion(int numeroBolsin, String codigoCMOrigen, String codigoCMDestino) {
        String respuesta = solicitarUbicacion(numeroBolsin, codigoCMDestino);
        return adaptarUbicacion(respuesta);
    }

    // Llama al servicio externo (este modelo usa la CM de destino)
    private String solicitarUbicacion(int numeroBolsin, String codigoCMDestino) {
        try {
            return servicio.retrieveTrackingData(claveAPI, numeroBolsin, codigoCMDestino);
        } catch (RestClientException e) {
            throw new GPSTrackerException("NavTrack QX-7A no informó la ubicación del bolsín " + numeroBolsin);
        }
    }

    // Lee el texto: "numeroBolsin,latitud,longitud,fechaHora"
    private UbicacionBolsin adaptarUbicacion(String texto) {
        try {
            String[] datos = texto.trim().split(",");

            int numero = Integer.parseInt(datos[0]);
            double latitud = Double.parseDouble(datos[1]);
            double longitud = Double.parseDouble(datos[2]);
            LocalDateTime fechaHora = LocalDateTime.parse(datos[3]);

            return new UbicacionBolsin(numero, latitud, longitud, fechaHora);
        } catch (Exception e) {
            throw new GPSTrackerException("Respuesta inválida del NavTrack QX-7A");
        }
    }
}

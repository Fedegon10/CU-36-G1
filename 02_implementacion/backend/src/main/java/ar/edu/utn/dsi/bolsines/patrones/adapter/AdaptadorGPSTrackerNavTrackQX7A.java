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

    // [Fig. 2 · 12] método polimórfico definido en IAdaptadorGPSTracker (misma secuencia que el XTR-4500L)
    @Override
    public UbicacionBolsin obtenerUbicacion(int numeroBolsin, String codigoCMOrigen, String codigoCMDestino) {
        // [Fig. 2 · 13] pide los datos al servicio externo
        String respuesta = solicitarUbicacion(numeroBolsin, codigoCMDestino);
        // [Fig. 2 · 16] los traduce al formato común (UbicacionBolsin)
        return adaptarUbicacion(respuesta);
    }

    // [Fig. 2 · 13] Llama al servicio externo (este modelo usa la CM de destino)
    private String solicitarUbicacion(int numeroBolsin, String codigoCMDestino) {
        try {
            // [Fig. 2 · 14] llamada al servicio externo  /  [Fig. 2 · 15] respuesta en su formato
            return servicio.retrieveTrackingData(claveAPI, numeroBolsin, codigoCMDestino);
        } catch (RestClientException e) {
            // A3: el GPS Tracker no pudo informar la ubicación del bolsín
            throw new GPSTrackerException("NavTrack QX-7A no informó la ubicación del bolsín " + numeroBolsin);
        }
    }

    // [Fig. 2 · 16] Lee el texto: "numeroBolsin,latitud,longitud,fechaHora"
    private UbicacionBolsin adaptarUbicacion(String texto) {
        try {
            String[] datos = texto.trim().split(",");

            int numero = Integer.parseInt(datos[0]);
            double latitud = Double.parseDouble(datos[1]);
            double longitud = Double.parseDouble(datos[2]);
            LocalDateTime fechaHora = LocalDateTime.parse(datos[3]);

            // [Fig. 2 · 17] crea el objeto en el formato común
            return new UbicacionBolsin(numero, latitud, longitud, fechaHora);
        } catch (Exception e) {
            throw new GPSTrackerException("Respuesta inválida del NavTrack QX-7A");
        }
    }
}

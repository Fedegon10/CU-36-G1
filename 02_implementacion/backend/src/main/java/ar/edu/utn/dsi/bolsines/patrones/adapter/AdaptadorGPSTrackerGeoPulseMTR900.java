package ar.edu.utn.dsi.bolsines.patrones.adapter;

import ar.edu.utn.dsi.bolsines.patrones.adapter.serviciosexternos.ServicioGPSTrackerGeoPulseMTR900;
import ar.edu.utn.dsi.bolsines.excepciones.GPSTrackerException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

// PATRÓN ADAPTER - Adaptador concreto para el modelo GeoPulse MTR-900
// Convierte la matriz del servicio GeoPulse al formato común UbicacionBolsin.
public class AdaptadorGPSTrackerGeoPulseMTR900 implements IAdaptadorGPSTracker {

    private final String claveAPI;
    private final ServicioGPSTrackerGeoPulseMTR900 servicio;

    public AdaptadorGPSTrackerGeoPulseMTR900(String claveAPI, ServicioGPSTrackerGeoPulseMTR900 servicio) {
        this.claveAPI = claveAPI;
        this.servicio = servicio;
    }

    // [Fig. 2 · 12] método polimórfico definido en IAdaptadorGPSTracker (misma secuencia que el XTR-4500L)
    @Override
    public UbicacionBolsin obtenerUbicacion(int numeroBolsin, String codigoCMOrigen, String codigoCMDestino) {
        // [Fig. 2 · 13] pide los datos al servicio externo
        Object[][] respuesta = solicitarUbicacion(numeroBolsin);
        // [Fig. 2 · 16] los traduce al formato común (UbicacionBolsin)
        return adaptarUbicacion(respuesta);
    }

    // [Fig. 2 · 13] Llama al servicio externo (este modelo solo usa el número de bolsín)
    private Object[][] solicitarUbicacion(int numeroBolsin) {
        try {
            // [Fig. 2 · 14] llamada al servicio externo  /  [Fig. 2 · 15] respuesta en su formato
            return servicio.fetchCargoPositions(claveAPI, numeroBolsin);
        } catch (RestClientException e) {
            // A3: el GPS Tracker no pudo informar la ubicación del bolsín
            throw new GPSTrackerException("GeoPulse MTR-900 no informó la ubicación del bolsín " + numeroBolsin);
        }
    }

    // [Fig. 2 · 16] Lee la primera fila de la matriz: [numeroBolsin, latitud, longitud, fechaHora]
    private UbicacionBolsin adaptarUbicacion(Object[][] matriz) {
        try {
            Object[] fila = matriz[0];

            int numero = Integer.parseInt(String.valueOf(fila[0]));
            double latitud = Double.parseDouble(String.valueOf(fila[1]));
            double longitud = Double.parseDouble(String.valueOf(fila[2]));
            LocalDateTime fechaHora = LocalDateTime.parse(String.valueOf(fila[3]));

            // [Fig. 2 · 17] crea el objeto en el formato común
            return new UbicacionBolsin(numero, latitud, longitud, fechaHora);
        } catch (Exception e) {
            throw new GPSTrackerException("Respuesta inválida del GeoPulse MTR-900");
        }
    }
}

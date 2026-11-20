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

    // método polimórfico definido en IAdaptadorGPSTracker (misma secuencia que el XTR-4500L)
    @Override
    public UbicacionBolsin obtenerUbicacion(int numeroBolsin, String codigoCMOrigen, String codigoCMDestino) {
        // pide los datos al servicio externo
        Object[][] respuesta = solicitarUbicacion(numeroBolsin);
        // los traduce al formato común (UbicacionBolsin)
        return adaptarUbicacion(respuesta);
    }

    // Llama al servicio externo (este modelo solo usa el número de bolsín)
    private Object[][] solicitarUbicacion(int numeroBolsin) {
        try {
            // llama al servicio externo y recibe la respuesta en su formato
            return servicio.fetchCargoPositions(claveAPI, numeroBolsin);
        } catch (RestClientException e) {
            // A3: el GPS Tracker no pudo informar la ubicación del bolsín
            throw new GPSTrackerException("GeoPulse MTR-900 no informó la ubicación del bolsín " + numeroBolsin);
        }
    }

    // Lee la primera fila de la matriz: [numeroBolsin, latitud, longitud, fechaHora]
    private UbicacionBolsin adaptarUbicacion(Object[][] matriz) {
        try {
            Object[] fila = matriz[0];

            int numero = Integer.parseInt(String.valueOf(fila[0]));
            double latitud = Double.parseDouble(String.valueOf(fila[1]));
            double longitud = Double.parseDouble(String.valueOf(fila[2]));
            LocalDateTime fechaHora = LocalDateTime.parse(String.valueOf(fila[3]));

            // crea el objeto en el formato común
            return new UbicacionBolsin(numero, latitud, longitud, fechaHora);
        } catch (Exception e) {
            throw new GPSTrackerException("Respuesta inválida del GeoPulse MTR-900");
        }
    }
}

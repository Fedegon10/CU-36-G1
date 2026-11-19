package ar.edu.utn.dsi.bolsines.patrones.adapter;

import ar.edu.utn.dsi.bolsines.patrones.adapter.serviciosexternos.ServicioGPSTrackerXTR4500L;
import ar.edu.utn.dsi.bolsines.excepciones.GPSTrackerException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

// PATRÓN ADAPTER - Adaptador concreto para el modelo XTR-4500L
// Convierte el JSON del servicio XTR-4500L al formato común UbicacionBolsin.
public class AdaptadorGPSTrackerXTR4500L implements IAdaptadorGPSTracker {

    private final String claveAPI;
    private final ServicioGPSTrackerXTR4500L servicio;

    public AdaptadorGPSTrackerXTR4500L(String claveAPI, ServicioGPSTrackerXTR4500L servicio) {
        this.claveAPI = claveAPI;
        this.servicio = servicio;
    }

    // método polimórfico definido en IAdaptadorGPSTracker
    @Override
    public UbicacionBolsin obtenerUbicacion(int numeroBolsin, String codigoCMOrigen, String codigoCMDestino) {
        // pide los datos al servicio externo
        String respuesta = solicitarUbicacion(numeroBolsin, codigoCMOrigen);
        // los traduce al formato común (UbicacionBolsin)
        return adaptarUbicacion(respuesta);
    }

    // Llama al servicio externo (este modelo usa la CM de origen)
    private String solicitarUbicacion(int numeroBolsin, String codigoCMOrigen) {
        try {
            // llama al servicio externo y recibe la respuesta en su formato
            return servicio.getBolsinLocation(claveAPI, numeroBolsin, codigoCMOrigen);
        } catch (RestClientException e) {
            // A3: el GPS Tracker no pudo informar la ubicación del bolsín
            throw new GPSTrackerException("XTR-4500L no informó la ubicación del bolsín " + numeroBolsin);
        }
    }

    // Lee el JSON: { "items": [ { numeroBolsin, latitud, longitud, fechaHoraUltimaActualizacion } ] }
    private UbicacionBolsin adaptarUbicacion(String json) {
        try {
            JsonNode item = new ObjectMapper().readTree(json).get("items").get(0);

            int numero = item.get("numeroBolsin").asInt();
            double latitud = item.get("latitud").asDouble();
            double longitud = item.get("longitud").asDouble();
            LocalDateTime fechaHora = LocalDateTime.parse(item.get("fechaHoraUltimaActualizacion").asText());

            // crea el objeto en el formato común
            return new UbicacionBolsin(numero, latitud, longitud, fechaHora);
        } catch (Exception e) {
            throw new GPSTrackerException("Respuesta inválida del XTR-4500L");
        }
    }
}

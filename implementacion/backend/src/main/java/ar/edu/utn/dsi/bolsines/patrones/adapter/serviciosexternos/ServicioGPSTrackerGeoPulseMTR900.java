package ar.edu.utn.dsi.bolsines.patrones.adapter.serviciosexternos;

import org.springframework.web.client.RestClient;

// PATRÓN ADAPTER - Adaptee (servicio externo GPS Tracker modelo GeoPulse MTR-900)
// Recibe: API Key y número de bolsín (entero).
// Devuelve: una matriz [[numeroBolsin, latitud, longitud, fechaHora]].
public class ServicioGPSTrackerGeoPulseMTR900 {

    private final RestClient restClient;

    // lo crea InterfazGPSTracker en crearAdaptador()
    public ServicioGPSTrackerGeoPulseMTR900(RestClient restClient) {
        this.restClient = restClient;
    }

    // operación original del GPS Tracker: devuelve la respuesta en su formato
    public Object[][] fetchCargoPositions(String apiKey, int numeroBolsin) {
        return restClient.get()
                .uri("/api/geopulse-mtr-900/fetchCargoPositions?numeroBolsin={nro}", numeroBolsin)
                .header("X-API-Key", apiKey)
                .retrieve()
                .body(Object[][].class);
    }
}

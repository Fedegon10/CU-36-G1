package ar.edu.utn.dsi.bolsines.patrones.adapter.serviciosexternos;

import org.springframework.web.client.RestClient;

// PATRÓN ADAPTER - Adaptee (servicio externo GPS Tracker modelo NavTrack QX-7A)
// Recibe: API Key, número de bolsín (entero) y código de CM destino (String).
// Devuelve: un único String con los datos separados por comas.
public class ServicioGPSTrackerNavTrackQX7A {

    private final RestClient restClient;

    public ServicioGPSTrackerNavTrackQX7A(RestClient restClient) {
        this.restClient = restClient;
    }

    public String retrieveTrackingData(String apiKey, int numeroBolsin, String codigoCMDestino) {
        return restClient.get()
                .uri("/api/navtrack-qx-7a/retrieveTrackingData?numeroBolsin={nro}&codigoComisionMedicaDestino={cm}",
                        numeroBolsin, codigoCMDestino)
                .header("X-API-Key", apiKey)
                .retrieve()
                .body(String.class);
    }
}

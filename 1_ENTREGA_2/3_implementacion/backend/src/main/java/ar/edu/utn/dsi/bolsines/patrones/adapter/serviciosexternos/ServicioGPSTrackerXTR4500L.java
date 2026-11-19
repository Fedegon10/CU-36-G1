package ar.edu.utn.dsi.bolsines.patrones.adapter.serviciosexternos;

import org.springframework.web.client.RestClient;

// PATRÓN ADAPTER - Adaptee (servicio externo GPS Tracker modelo XTR-4500L)
// Recibe: API Key, número de bolsín (entero) y código de CM origen (String).
// Devuelve: un JSON en formato String.
public class ServicioGPSTrackerXTR4500L {

    private final RestClient restClient;

    // lo crea InterfazGPSTracker en crearAdaptador()
    public ServicioGPSTrackerXTR4500L(RestClient restClient) {
        this.restClient = restClient;
    }

    // operación original del GPS Tracker: devuelve la respuesta en su formato
    public String getBolsinLocation(String apiKey, int numeroBolsin, String codigoCMOrigen) {
        return restClient.get()
                .uri("/api/xtr-4500l/getBolsinLocation?numeroBolsin={nro}&codigoComisionMedicaOrigen={cm}",
                        numeroBolsin, codigoCMOrigen)
                .header("X-API-Key", apiKey)
                .retrieve()
                .body(String.class);
    }
}

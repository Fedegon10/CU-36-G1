package ar.edu.dsi.gpstracker.controller;

import ar.edu.dsi.gpstracker.service.ApiKeyService;
import ar.edu.dsi.gpstracker.service.BolsinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// API del GPS Tracker modelo GeoPulse MTR-900 (de la cátedra)
// La llama ServicioGPSTrackerGeoPulseMTR900 del backend
@RestController
@RequestMapping("/api/geopulse-mtr-900")
public class GeoPulseMtr900Controller {

    private final ApiKeyService apiKeyService;
    private final BolsinService bolsinService;

    public GeoPulseMtr900Controller(ApiKeyService apiKeyService, BolsinService bolsinService) {
        this.apiKeyService = apiKeyService;
        this.bolsinService = bolsinService;
    }

    // fetchCargoPositions(): devuelve la ubicación del bolsín como matriz [[numero, latitud, longitud, fechaHora]]
    // Recibe: API Key y número de bolsín
    @GetMapping("/fetchCargoPositions")
    public List<List<Object>> fetchCargoPositions(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestParam Integer numeroBolsin
    ) {
        // valida la API Key del modelo
        apiKeyService.validateForGeoPulseMtr900(apiKey);
        // busca el bolsín y arma la respuesta
        return bolsinService.getGeoPulsePositions(numeroBolsin);
    }
}

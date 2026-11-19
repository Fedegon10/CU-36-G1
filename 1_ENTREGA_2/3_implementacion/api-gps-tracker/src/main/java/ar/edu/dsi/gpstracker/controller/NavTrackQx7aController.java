package ar.edu.dsi.gpstracker.controller;

import ar.edu.dsi.gpstracker.service.ApiKeyService;
import ar.edu.dsi.gpstracker.service.BolsinService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// API del GPS Tracker modelo NavTrack QX-7A (de la cátedra)
// La llama ServicioGPSTrackerNavTrackQX7A del backend
@RestController
@RequestMapping("/api/navtrack-qx-7a")
public class NavTrackQx7aController {

    private final ApiKeyService apiKeyService;
    private final BolsinService bolsinService;

    public NavTrackQx7aController(ApiKeyService apiKeyService, BolsinService bolsinService) {
        this.apiKeyService = apiKeyService;
        this.bolsinService = bolsinService;
    }

    // retrieveTrackingData(): devuelve la ubicación del bolsín como texto separado por comas
    // Recibe: API Key, número de bolsín y código de CM destino
    @GetMapping(value = "/retrieveTrackingData", produces = MediaType.TEXT_PLAIN_VALUE)
    public String retrieveTrackingData(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestParam Integer numeroBolsin,
            @RequestParam String codigoComisionMedicaDestino
    ) {
        // valida la API Key del modelo
        apiKeyService.validateForNavTrackQx7a(apiKey);
        // busca el bolsín y arma la respuesta
        return bolsinService.getNavTrackData(numeroBolsin, codigoComisionMedicaDestino);
    }
}

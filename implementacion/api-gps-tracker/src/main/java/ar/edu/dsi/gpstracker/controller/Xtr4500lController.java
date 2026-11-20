package ar.edu.dsi.gpstracker.controller;

import ar.edu.dsi.gpstracker.dto.XtrBolsinLocationResponseDto;
import ar.edu.dsi.gpstracker.service.ApiKeyService;
import ar.edu.dsi.gpstracker.service.BolsinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// API del GPS Tracker modelo XTR-4500L (de la cátedra)
// La llama ServicioGPSTrackerXTR4500L del backend
@RestController
@RequestMapping("/api/xtr-4500l")
public class Xtr4500lController {

    private final ApiKeyService apiKeyService;
    private final BolsinService bolsinService;

    public Xtr4500lController(ApiKeyService apiKeyService, BolsinService bolsinService) {
        this.apiKeyService = apiKeyService;
        this.bolsinService = bolsinService;
    }

    // getBolsinLocation(): devuelve la ubicación del bolsín en JSON
    // Recibe: API Key, número de bolsín y código de CM origen
    @GetMapping("/getBolsinLocation")
    public XtrBolsinLocationResponseDto getBolsinLocation(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestParam Integer numeroBolsin,
            @RequestParam String codigoComisionMedicaOrigen
    ) {
        // valida la API Key del modelo
        apiKeyService.validateForXtr4500l(apiKey);
        // busca el bolsín y arma la respuesta
        return bolsinService.getXtrBolsinLocation(numeroBolsin, codigoComisionMedicaOrigen);
    }
}

package ar.edu.dsi.gpstracker.controller;

import ar.edu.dsi.gpstracker.dto.HealthResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Indica si la API está funcionando
@RestController
public class HealthController {

    // health(): responde "ok" si la API está levantada
    @GetMapping("/health")
    public HealthResponseDto health() {
        return new HealthResponseDto("ok");
    }
}

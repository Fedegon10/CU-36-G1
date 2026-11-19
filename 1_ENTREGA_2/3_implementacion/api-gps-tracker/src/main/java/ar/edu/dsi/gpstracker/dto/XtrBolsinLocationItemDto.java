package ar.edu.dsi.gpstracker.dto;

import java.time.LocalDateTime;

// Ubicación de un bolsín dentro de la respuesta del XTR-4500L
public record XtrBolsinLocationItemDto(
        Integer numeroBolsin,
        Double latitud,
        Double longitud,
        LocalDateTime fechaHoraUltimaActualizacion
) {
}

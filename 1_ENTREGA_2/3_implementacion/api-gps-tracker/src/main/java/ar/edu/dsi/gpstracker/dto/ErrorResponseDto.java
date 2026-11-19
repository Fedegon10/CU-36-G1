package ar.edu.dsi.gpstracker.dto;

import java.time.LocalDateTime;

// Datos que se devuelven cuando hay un error
public record ErrorResponseDto(
        String error,
        String mensaje,
        int status,
        LocalDateTime timestamp
) {
}

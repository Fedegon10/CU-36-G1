package ar.edu.dsi.gpstracker.dto;

import java.util.List;

// Respuesta del XTR-4500L: lista "items" con la ubicación del bolsín
public record XtrBolsinLocationResponseDto(List<XtrBolsinLocationItemDto> items) {
}

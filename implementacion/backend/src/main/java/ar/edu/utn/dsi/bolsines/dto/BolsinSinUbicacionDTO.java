package ar.edu.utn.dsi.bolsines.dto;

// Bolsín cuyo GPS Tracker no informó ubicación (flujo alternativo A3)
public record BolsinSinUbicacionDTO(
        int numeroBolsin,
        Integer numeroPrecinto,
        String nombreCMDestino,
        String modeloGPSTracker,
        String motivo) {
}

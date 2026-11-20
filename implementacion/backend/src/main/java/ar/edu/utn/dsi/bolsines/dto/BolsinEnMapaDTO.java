package ar.edu.utn.dsi.bolsines.dto;

import java.time.LocalDateTime;

// Bolsín con su ubicación, para mostrar en el mapa
public record BolsinEnMapaDTO(
        int numeroBolsin,
        Integer numeroPrecinto,
        String nombreCMOrigen,
        String codigoCMDestino,
        String nombreCMDestino,
        String modeloGPSTracker,
        double latitud,
        double longitud,
        LocalDateTime fechaHoraUltimaActualizacion) {
}

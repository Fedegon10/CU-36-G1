package ar.edu.utn.dsi.bolsines.dto;

// Filtro por número de precinto y/o CM destino (ambos opcionales)
public record FiltroRequest(Integer numeroPrecinto, String codigoCMDestino) {
}

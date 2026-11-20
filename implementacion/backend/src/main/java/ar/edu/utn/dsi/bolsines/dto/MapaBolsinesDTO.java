package ar.edu.utn.dsi.bolsines.dto;

import java.util.List;

// Respuesta al iniciar el CU: CM del usuario, bolsines en el mapa y bolsines sin ubicación
public record MapaBolsinesDTO(
        ComisionMedicaDTO comisionMedica,
        List<BolsinEnMapaDTO> bolsines,
        List<BolsinSinUbicacionDTO> bolsinesSinUbicacion,
        List<ComisionMedicaDTO> comisionesDestino) {
}

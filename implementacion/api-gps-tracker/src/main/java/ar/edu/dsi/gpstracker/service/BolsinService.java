package ar.edu.dsi.gpstracker.service;

import ar.edu.dsi.gpstracker.dto.XtrBolsinLocationItemDto;
import ar.edu.dsi.gpstracker.dto.XtrBolsinLocationResponseDto;
import ar.edu.dsi.gpstracker.exception.BolsinNotFoundException;
import ar.edu.dsi.gpstracker.exception.CommissionCodeMismatchException;
import ar.edu.dsi.gpstracker.model.Bolsin;
import ar.edu.dsi.gpstracker.repository.InMemoryBolsinRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Busca los bolsines y arma la respuesta en el formato de cada modelo de GPS
@Service
public class BolsinService {

    private final InMemoryBolsinRepository bolsinRepository;

    public BolsinService(InMemoryBolsinRepository bolsinRepository) {
        this.bolsinRepository = bolsinRepository;
    }

    // Devuelve todos los bolsines cargados
    public List<Bolsin> getAll() {
        return bolsinRepository.findAll();
    }

    // Busca un bolsín por su número (si no existe lanza BolsinNotFoundException)
    public Bolsin getByNumeroBolsin(Integer numeroBolsin) {
        return bolsinRepository.findByNumeroBolsin(numeroBolsin)
                .orElseThrow(() -> new BolsinNotFoundException(numeroBolsin));
    }

    // Respuesta del XTR-4500L: JSON { "items": [ { numeroBolsin, latitud, longitud, fechaHoraUltimaActualizacion } ] }
    public XtrBolsinLocationResponseDto getXtrBolsinLocation(Integer numeroBolsin, String codigoComisionMedicaOrigen) {
        Bolsin bolsin = getByNumeroBolsin(numeroBolsin);
        // la CM origen recibida tiene que ser la del bolsín
        if (!bolsin.getCodigoComisionMedicaOrigen().equalsIgnoreCase(codigoComisionMedicaOrigen)) {
            throw new CommissionCodeMismatchException("codigoComisionMedicaOrigen no coincide con el bolsin solicitado.");
        }

        XtrBolsinLocationItemDto item = new XtrBolsinLocationItemDto(
                bolsin.getNumeroBolsin(),
                bolsin.getLatitud(),
                bolsin.getLongitud(),
                bolsin.getFechaHoraUltimaActualizacion()
        );
        return new XtrBolsinLocationResponseDto(List.of(item));
    }

    // Respuesta del NavTrack QX-7A: texto "numeroBolsin,latitud,longitud,fechaHora"
    public String getNavTrackData(Integer numeroBolsin, String codigoComisionMedicaDestino) {
        Bolsin bolsin = getByNumeroBolsin(numeroBolsin);
        // la CM destino recibida tiene que ser la del bolsín
        if (!bolsin.getCodigoComisionMedicaDestino().equalsIgnoreCase(codigoComisionMedicaDestino)) {
            throw new CommissionCodeMismatchException("codigoComisionMedicaDestino no coincide con el bolsin solicitado.");
        }

        return bolsin.getNumeroBolsin() + "," +
                bolsin.getLatitud() + "," +
                bolsin.getLongitud() + "," +
                bolsin.getFechaHoraUltimaActualizacion();
    }

    // Respuesta del GeoPulse MTR-900: matriz [[numeroBolsin, latitud, longitud, fechaHora]]
    public List<List<Object>> getGeoPulsePositions(Integer numeroBolsin) {
        Bolsin bolsin = getByNumeroBolsin(numeroBolsin);
        List<Object> row = List.of(
                bolsin.getNumeroBolsin(),
                bolsin.getLatitud(),
                bolsin.getLongitud(),
                bolsin.getFechaHoraUltimaActualizacion().toString()
        );
        return List.of(row);
    }
}

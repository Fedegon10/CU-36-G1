package ar.edu.utn.dsi.bolsines.boundary.pantallas;

import ar.edu.utn.dsi.bolsines.control.GestorConsultarSeguimientoBolsines;
import ar.edu.utn.dsi.bolsines.dto.BolsinEnMapaDTO;
import ar.edu.utn.dsi.bolsines.dto.ConfirmacionMailRequest;
import ar.edu.utn.dsi.bolsines.dto.FiltroRequest;
import ar.edu.utn.dsi.bolsines.dto.MailEnviadoDTO;
import ar.edu.utn.dsi.bolsines.dto.MapaBolsinesDTO;
import ar.edu.utn.dsi.bolsines.dto.SeleccionBolsinRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// <<boundary>> PantallaConsultarSeguimientoBolsines
// Recibe las acciones del EB desde el frontend y las delega al gestor.
@RestController
@RequestMapping("/api/seguimiento-bolsines")
public class PantallaConsultarSeguimientoBolsines {

    private final GestorConsultarSeguimientoBolsines gestor;

    public PantallaConsultarSeguimientoBolsines(GestorConsultarSeguimientoBolsines gestor) {
        this.gestor = gestor;
    }

    // Paso 1: el EB selecciona la opción de consultar la ubicación de bolsines
    @PostMapping("/iniciar")
    public MapaBolsinesDTO selecOpcionConsultarUbicacionBolsines() {
        return gestor.consultarUbicacionBolsines();
    }

    // Paso 6: el EB filtra por número de precinto y/o CM destino
    @PostMapping("/filtrar")
    public List<BolsinEnMapaDTO> tomarFiltro(@RequestBody FiltroRequest filtro) {
        return gestor.tomarFiltro(filtro.numeroPrecinto(), filtro.codigoCMDestino());
    }

    // Paso 7: el EB selecciona un bolsín del mapa
    @PostMapping("/seleccionar-bolsin")
    public BolsinEnMapaDTO tomarSeleccionBolsin(@RequestBody SeleccionBolsinRequest seleccion) {
        return gestor.tomarSeleccionBolsin(seleccion.numeroBolsin());
    }

    // Paso 9: el EB confirma (o no) el envío del correo
    @PostMapping("/confirmar-mail")
    public ResponseEntity<?> tomarConfirmacionDeMail(@RequestBody ConfirmacionMailRequest confirmacion) {
        MailEnviadoDTO mail = gestor.tomarConfirmacionDeMail(confirmacion.confirma());
        if (mail == null) {
            return ResponseEntity.ok(Map.of("mailEnviado", false));
        }
        return ResponseEntity.ok(Map.of("mailEnviado", true, "mail", mail));
    }

    // A4: el EB cancela el caso de uso
    @PostMapping("/cancelar")
    public Map<String, String> cancelar() {
        gestor.cancelarCU();
        return Map.of("mensaje", "Caso de uso cancelado.");
    }
}

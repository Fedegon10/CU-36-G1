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
// Recibe las acciones del EB (desde el frontend) y las delega al gestor.
// Los mensajes que la pantalla se envía a sí misma (mostrar...) están en
// frontend/src/pantallas/PantallaConsultarSeguimientoBolsines.jsx
@RestController
@RequestMapping("/api/seguimiento-bolsines")
public class PantallaConsultarSeguimientoBolsines {

    private final GestorConsultarSeguimientoBolsines gestor;

    public PantallaConsultarSeguimientoBolsines(GestorConsultarSeguimientoBolsines gestor) {
        this.gestor = gestor;
    }

    // Paso 1: el EB selecciona la opción para consultar la ubicación de bolsines
    @PostMapping("/iniciar")
    public MapaBolsinesDTO selecOpcionConsultarUbicacionBolsines() {
        return habilitarPantalla();
    }

    // habilitarPantalla()
    private MapaBolsinesDTO habilitarPantalla() {
        // pide al gestor la CM, los bolsines enviados y sus ubicaciones
        // recibe los datos del mapa
        return gestor.consultarUbicacionBolsines();
    }

    // Paso 6 (opcional): el EB filtra por número de precinto y/o CM destino (A2)
    @PostMapping("/filtrar")
    public List<BolsinEnMapaDTO> tomarFiltro(@RequestBody FiltroRequest filtro) {
        return gestor.tomarFiltro(filtro.numeroPrecinto(), filtro.codigoCMDestino());
    }

    // Paso 7: el EB selecciona un bolsín del mapa
    @PostMapping("/seleccionar-bolsin")
    public BolsinEnMapaDTO selecBolsin(@RequestBody SeleccionBolsinRequest seleccion) {
        // avisa al gestor y recibe los datos del bolsín
        // (Paso 8: la pantalla pide la confirmación del envío del correo)
        return gestor.tomarSeleccionBolsin(seleccion.numeroBolsin());
    }

    // Paso 9: el EB confirma (o no) el envío del correo
    @PostMapping("/confirmar-mail")
    public ResponseEntity<?> confirmarEnvioMail(@RequestBody ConfirmacionMailRequest confirmacion) {
        // avisa al gestor y recibe el correo enviado
        MailEnviadoDTO mail = gestor.tomarConfirmacionDeMail(confirmacion.confirma());

        // A5: el EB no selecciona la opción para enviar el correo
        if (mail == null) {
            return ResponseEntity.ok(Map.of("mailEnviado", false));
        }
        return ResponseEntity.ok(Map.of("mailEnviado", true, "mail", mail));
    }

    // A4: el EB cancela la ejecución del caso de uso
    @PostMapping("/cancelar")
    public Map<String, String> cancelar() {
        gestor.cancelarCU();
        return Map.of("mensaje", "Caso de uso cancelado.");
    }
}

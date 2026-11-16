package ar.edu.utn.dsi.bolsines.control;

import ar.edu.utn.dsi.bolsines.excepciones.FlujoAlternativoException;
import ar.edu.utn.dsi.bolsines.boundary.interfaces.InterfazGPSTracker;
import ar.edu.utn.dsi.bolsines.dto.BolsinEnMapaDTO;
import ar.edu.utn.dsi.bolsines.dto.BolsinSinUbicacionDTO;
import ar.edu.utn.dsi.bolsines.dto.ComisionMedicaDTO;
import ar.edu.utn.dsi.bolsines.dto.MailEnviadoDTO;
import ar.edu.utn.dsi.bolsines.dto.MapaBolsinesDTO;
import ar.edu.utn.dsi.bolsines.excepciones.GPSTrackerException;
import ar.edu.utn.dsi.bolsines.patrones.adapter.UbicacionBolsin;
import ar.edu.utn.dsi.bolsines.entidades.Bolsin;
import ar.edu.utn.dsi.bolsines.entidades.ComisionMedica;
import ar.edu.utn.dsi.bolsines.entidades.Empleado;
import ar.edu.utn.dsi.bolsines.entidades.Sesion;
import ar.edu.utn.dsi.bolsines.entidades.Usuario;
import ar.edu.utn.dsi.bolsines.repositorios.BolsinRepository;
import ar.edu.utn.dsi.bolsines.repositorios.EmpleadoRepository;
import ar.edu.utn.dsi.bolsines.repositorios.SesionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// <<control>> GestorConsultarSeguimientoBolsines - CU 36 Consultar seguimiento de bolsines
@Service
public class GestorConsultarSeguimientoBolsines {

    // Acceso a datos
    private final SesionRepository sesionRepository;
    private final EmpleadoRepository empleadoRepository;
    private final BolsinRepository bolsinRepository;

    // Boundary con el GPS Tracker y gestor del CU incluido (Notificar ubicación de bolsín)
    private final InterfazGPSTracker interfazGPSTracker;
    private final GestorNotificarUbicacionBolsin gestorNotificarUbicacionBolsin;

    // Datos del caso de uso en curso
    private ComisionMedica comisionMedicaUsuario;
    private List<Bolsin> bolsinesEnviados = new ArrayList<>();
    private Map<Integer, UbicacionBolsin> ubicacionesBolsines = new HashMap<>();
    private List<BolsinSinUbicacionDTO> bolsinesSinUbicacion = new ArrayList<>();
    private Bolsin bolsinSeleccionado;

    public GestorConsultarSeguimientoBolsines(SesionRepository sesionRepository,
                                              EmpleadoRepository empleadoRepository,
                                              BolsinRepository bolsinRepository,
                                              InterfazGPSTracker interfazGPSTracker,
                                              GestorNotificarUbicacionBolsin gestorNotificarUbicacionBolsin) {
        this.sesionRepository = sesionRepository;
        this.empleadoRepository = empleadoRepository;
        this.bolsinRepository = bolsinRepository;
        this.interfazGPSTracker = interfazGPSTracker;
        this.gestorNotificarUbicacionBolsin = gestorNotificarUbicacionBolsin;
    }

    // ===================== Pasos 1 a 6 del CU =====================

    // Inicia el CU: busca la CM, los bolsines enviados y sus ubicaciones
    // (synchronized: evita que dos consultas al mismo tiempo mezclen los datos)
    public synchronized MapaBolsinesDTO consultarUbicacionBolsines() {
        finCU();

        comisionMedicaUsuario = buscarCM();
        buscarBolsinesEnviados();

        // A1: no hay bolsines en estado Enviado
        if (bolsinesEnviados.isEmpty()) {
            throw new FlujoAlternativoException("A1",
                    "No se encontraron bolsines en estado Enviado para " + comisionMedicaUsuario.getNombre() + ".");
        }

        obtenerUbicacionBolsines();
        return armarMapaDePosicionDeBolsines();
    }

    // Busca la CM del empleado logueado (Sesion -> Usuario -> Empleado -> CM)
    private ComisionMedica buscarCM() {
        Usuario usuarioLogueado = buscarSesionActiva().getUsuario();

        for (Empleado empleado : empleadoRepository.findAll()) {
            if (empleado.esTuUsuario(usuarioLogueado)) {
                return empleado.getCM();
            }
        }
        throw new FlujoAlternativoException("SESION", "El usuario logueado no tiene un empleado asociado.");
    }

    private Sesion buscarSesionActiva() {
        for (Sesion sesion : sesionRepository.findAll()) {
            if (sesion.estaActiva()) {
                return sesion;
            }
        }
        throw new FlujoAlternativoException("SESION", "No hay un usuario logueado.");
    }

    // Busca los bolsines en estado Enviado cuya CM origen es la CM del usuario
    private void buscarBolsinesEnviados() {
        for (Bolsin bolsin : bolsinRepository.findAll()) {
            if (bolsin.esEnviado() && bolsin.esTuCMOrigen(comisionMedicaUsuario)) {
                bolsinesEnviados.add(bolsin);
            }
        }
    }

    // Pide al GPS Tracker la ubicación de cada bolsín (patrón Adapter dentro de InterfazGPSTracker)
    private void obtenerUbicacionBolsines() {
        for (Bolsin bolsin : bolsinesEnviados) {
            try {
                UbicacionBolsin ubicacion = interfazGPSTracker.solicitarUbicacionBolsin(
                        bolsin.getNumeroBolsin(),
                        bolsin.getOrigen().getCodigo(),
                        bolsin.getDestino().getCodigo(),
                        bolsin.getModeloGPSTracker());
                ubicacionesBolsines.put(bolsin.getNumeroBolsin(), ubicacion);
            } catch (GPSTrackerException e) {
                // A3: el GPS Tracker no pudo informar la ubicación
                bolsinesSinUbicacion.add(new BolsinSinUbicacionDTO(
                        bolsin.getNumeroBolsin(),
                        bolsin.getNumeroPrecinto(),
                        bolsin.getDestino().getNombre(),
                        bolsin.getModeloGPSTracker(),
                        e.getMessage()));
            }
        }
    }

    // Arma los datos que la pantalla necesita para dibujar el mapa
    private MapaBolsinesDTO armarMapaDePosicionDeBolsines() {
        List<BolsinEnMapaDTO> bolsinesEnMapa = new ArrayList<>();
        List<ComisionMedicaDTO> comisionesDestino = new ArrayList<>();

        for (Bolsin bolsin : bolsinesEnviados) {
            if (ubicacionesBolsines.containsKey(bolsin.getNumeroBolsin())) {
                bolsinesEnMapa.add(crearBolsinEnMapa(bolsin));
                agregarComisionDestino(comisionesDestino, bolsin.getDestino());
            }
        }

        ComisionMedicaDTO cm = new ComisionMedicaDTO(comisionMedicaUsuario.getCodigo(), comisionMedicaUsuario.getNombre());
        return new MapaBolsinesDTO(cm, bolsinesEnMapa, bolsinesSinUbicacion, comisionesDestino);
    }

    // ===================== Filtro (paso 6) =====================

    // Filtra los bolsines del mapa por número de precinto y/o CM destino
    public List<BolsinEnMapaDTO> tomarFiltro(Integer numeroPrecinto, String codigoCMDestino) {
        List<BolsinEnMapaDTO> resultado = new ArrayList<>();

        for (Bolsin bolsin : bolsinesEnviados) {
            if (!ubicacionesBolsines.containsKey(bolsin.getNumeroBolsin())) {
                continue;
            }
            boolean coincidePrecinto = numeroPrecinto == null || bolsin.esTuNumeroPrecinto(numeroPrecinto);
            boolean coincideDestino = codigoCMDestino == null || codigoCMDestino.isBlank()
                    || bolsin.esTuCMDestino(codigoCMDestino);

            if (coincidePrecinto && coincideDestino) {
                resultado.add(crearBolsinEnMapa(bolsin));
            }
        }

        // A2: no se encuentra bolsín con el número de precinto ingresado
        if (resultado.isEmpty() && numeroPrecinto != null) {
            throw new FlujoAlternativoException("A2",
                    "No se encontró un bolsín con el número de precinto " + numeroPrecinto + ".");
        }
        return resultado;
    }

    // ===================== Pasos 7 a 11 del CU =====================

    // Paso 7: el EB selecciona un bolsín del mapa
    public BolsinEnMapaDTO tomarSeleccionBolsin(int numeroBolsin) {
        bolsinSeleccionado = null;
        for (Bolsin bolsin : bolsinesEnviados) {
            if (bolsin.getNumeroBolsin() == numeroBolsin && ubicacionesBolsines.containsKey(numeroBolsin)) {
                bolsinSeleccionado = bolsin;
            }
        }
        if (bolsinSeleccionado == null) {
            throw new FlujoAlternativoException("SELECCION", "El bolsín seleccionado no está en el mapa.");
        }
        // Paso 8: la pantalla consulta si se desea enviar el correo
        return crearBolsinEnMapa(bolsinSeleccionado);
    }

    // Paso 9: el EB confirma (o no) el envío del correo
    public MailEnviadoDTO tomarConfirmacionDeMail(boolean confirma) {
        if (bolsinSeleccionado == null) {
            throw new FlujoAlternativoException("SELECCION", "Primero debe seleccionar un bolsín.");
        }

        // A5: el EB no confirma el envío del correo
        if (!confirma) {
            finCU();
            return null;
        }

        Empleado gerente = buscarMailDeGerenteCMDestino();
        MailEnviadoDTO mail = llamarCUNotificarUbicacion(gerente);
        finCU();
        return mail;
    }

    // Paso 10: busca el Gerente de la CM destino del bolsín seleccionado
    private Empleado buscarMailDeGerenteCMDestino() {
        for (Empleado empleado : empleadoRepository.findAll()) {
            if (empleado.sosGCMDeCM(bolsinSeleccionado.getDestino())) {
                return empleado;
            }
        }
        throw new FlujoAlternativoException("GCM",
                "La CM destino " + bolsinSeleccionado.getDestino().getNombre() + " no tiene un Gerente registrado.");
    }

    // Paso 11: incluye el CU "Notificar ubicación de bolsín"
    private MailEnviadoDTO llamarCUNotificarUbicacion(Empleado gerente) {
        UbicacionBolsin ubicacion = ubicacionesBolsines.get(bolsinSeleccionado.getNumeroBolsin());
        return gestorNotificarUbicacionBolsin.notificarUbicacion(
                gerente.getEmail(),
                gerente.getNombreCompleto(),
                bolsinSeleccionado.getNumeroBolsin(),
                ubicacion.getFechaHoraUltimaActualizacion(),
                ubicacion.getLatitud(),
                ubicacion.getLongitud());
    }

    // A4: el EB cancela el caso de uso
    public void cancelarCU() {
        finCU();
    }

    // Limpia los datos del caso de uso
    private void finCU() {
        comisionMedicaUsuario = null;
        bolsinesEnviados = new ArrayList<>();
        ubicacionesBolsines = new HashMap<>();
        bolsinesSinUbicacion = new ArrayList<>();
        bolsinSeleccionado = null;
    }

    // ===================== Auxiliares =====================

    private BolsinEnMapaDTO crearBolsinEnMapa(Bolsin bolsin) {
        UbicacionBolsin ubicacion = ubicacionesBolsines.get(bolsin.getNumeroBolsin());
        return new BolsinEnMapaDTO(
                bolsin.getNumeroBolsin(),
                bolsin.getNumeroPrecinto(),
                bolsin.getDestino().getCodigo(),
                bolsin.getDestino().getNombre(),
                bolsin.getModeloGPSTracker(),
                ubicacion.getLatitud(),
                ubicacion.getLongitud(),
                ubicacion.getFechaHoraUltimaActualizacion());
    }

    private void agregarComisionDestino(List<ComisionMedicaDTO> lista, ComisionMedica cm) {
        for (ComisionMedicaDTO existente : lista) {
            if (existente.codigo().equals(cm.getCodigo())) {
                return;
            }
        }
        lista.add(new ComisionMedicaDTO(cm.getCodigo(), cm.getNombre()));
    }
}

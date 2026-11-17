package ar.edu.utn.dsi.bolsines.control;

import ar.edu.utn.dsi.bolsines.boundary.interfaces.InterfazGPSTracker;
import ar.edu.utn.dsi.bolsines.dto.BolsinEnMapaDTO;
import ar.edu.utn.dsi.bolsines.dto.BolsinSinUbicacionDTO;
import ar.edu.utn.dsi.bolsines.dto.ComisionMedicaDTO;
import ar.edu.utn.dsi.bolsines.dto.MailEnviadoDTO;
import ar.edu.utn.dsi.bolsines.dto.MapaBolsinesDTO;
import ar.edu.utn.dsi.bolsines.entidades.Bolsin;
import ar.edu.utn.dsi.bolsines.entidades.ComisionMedica;
import ar.edu.utn.dsi.bolsines.entidades.Empleado;
import ar.edu.utn.dsi.bolsines.entidades.Sesion;
import ar.edu.utn.dsi.bolsines.entidades.Usuario;
import ar.edu.utn.dsi.bolsines.excepciones.FlujoAlternativoException;
import ar.edu.utn.dsi.bolsines.excepciones.GPSTrackerException;
import ar.edu.utn.dsi.bolsines.patrones.adapter.UbicacionBolsin;
import ar.edu.utn.dsi.bolsines.repositorios.BolsinRepository;
import ar.edu.utn.dsi.bolsines.repositorios.EmpleadoRepository;
import ar.edu.utn.dsi.bolsines.repositorios.SesionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// <<control>> GestorConsultarSeguimientoBolsines - CU 36 Consultar seguimiento de bolsines
// Los comentarios [Fig. 3 · N] indican el mensaje N del diagrama de secuencia del flujo básico (Figura 3).
// Los comentarios [Fig. 2 · N] indican el mensaje N del diagrama de secuencia del patrón Adapter (Figura 2).
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
    private String codigoCMUsuario;
    private String nombreCMUsuario;
    private List<Bolsin> bolsinesEnviados = new ArrayList<>();
    private Map<Integer, UbicacionBolsin> ubicacionesBolsines = new HashMap<>();
    private List<BolsinSinUbicacionDTO> bolsinesSinUbicacion = new ArrayList<>();
    private Map<Integer, BolsinEnMapaDTO> bolsinesEnMapa = new HashMap<>();
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

    // =====================================================================
    // [Fig. 3 · 3] consultarUbicacionBolsines() - Pasos 2 a 6 del CU
    // (synchronized: evita que dos consultas al mismo tiempo mezclen los datos)
    // =====================================================================
    public synchronized MapaBolsinesDTO consultarUbicacionBolsines() {
        // [Fig. 3 · 4] Paso 2: busca la CM del usuario logueado
        comisionMedicaUsuario = buscarCM();

        // [Fig. 3 · 10] Paso 3: busca los bolsines Enviados cuya CM origen es la del usuario
        buscarBolsinesEnviados();

        // A1: no se encuentran bolsines en estado Enviado
        if (bolsinesEnviados.isEmpty()) {
            throw new FlujoAlternativoException("A1",
                    "No se encontraron bolsines en estado Enviado para " + nombreCMUsuario + ".");
        }

        // [Fig. 3 · 18] Pasos 4 y 5: pide la ubicación de cada bolsín al GPS Tracker (patrón Adapter)
        obtenerUbicacionBolsines();

        // [Fig. 3 · 21] Paso 6: arma los datos del mapa  /  [Fig. 3 · 24] los devuelve a la pantalla
        return armarMapaDePosicionDeBolsines();
    }

    // [Fig. 3 · 4] buscarCM(): Sesion -> Usuario -> Empleado -> ComisionMedica
    private ComisionMedica buscarCM() {
        // [Fig. 3 · 5] busca la sesión activa
        Sesion sesionActiva = null;
        for (Sesion sesion : sesionRepository.findAll()) {
            if (sesion.estaActiva()) {
                sesionActiva = sesion;
            }
        }
        if (sesionActiva == null) {
            throw new FlujoAlternativoException("SESION", "No hay un usuario logueado.");
        }

        // [Fig. 3 · 6] obtiene el usuario de la sesión
        Usuario usuarioLogueado = sesionActiva.getUsuario();

        // [Fig. 3 · 7] busca el empleado de ese usuario
        Empleado empleadoLogueado = null;
        for (Empleado empleado : empleadoRepository.findAll()) {
            if (empleado.esTuUsuario(usuarioLogueado)) {
                empleadoLogueado = empleado;
            }
        }
        if (empleadoLogueado == null) {
            throw new FlujoAlternativoException("SESION", "El usuario logueado no tiene un empleado asociado.");
        }

        // [Fig. 3 · 8] obtiene la CM del empleado
        ComisionMedica cm = empleadoLogueado.getCM();

        // [Fig. 3 · 9] obtiene el código y el nombre de la CM (se muestran en pantalla)
        codigoCMUsuario = cm.getCodigo();
        nombreCMUsuario = cm.getNombre();
        return cm;
    }

    // [Fig. 3 · 10] buscarBolsinesEnviados()
    private void buscarBolsinesEnviados() {
        bolsinesEnviados = new ArrayList<>();
        for (Bolsin bolsin : bolsinRepository.findAll()) {
            // [Fig. 3 · 11] ¿está Enviado?  y  [Fig. 3 · 16] ¿su CM origen es la del usuario?
            if (bolsin.esEnviado() && bolsin.esTuCMOrigen(comisionMedicaUsuario)) {
                bolsinesEnviados.add(bolsin);
            }
        }
    }

    // [Fig. 3 · 18] = [Fig. 2 · 1] obtenerUbicacionBolsines(): parte rediseñada con el patrón Adapter
    private void obtenerUbicacionBolsines() {
        ubicacionesBolsines = new HashMap<>();
        bolsinesSinUbicacion = new ArrayList<>();

        for (Bolsin bolsin : bolsinesEnviados) {
            // [Fig. 3 · 19] datos del bolsín que necesita el GPS Tracker
            int numeroBolsin = bolsin.getNumeroBolsin();              // [Fig. 2 · 2]
            String codigoCMOrigen = bolsin.getCodigoCMOrigen();       // [Fig. 2 · 3-4]
            String codigoCMDestino = bolsin.getCodigoCMDestino();     // [Fig. 2 · 5-6]
            String modeloGPSTracker = bolsin.getModeloGPSTracker();   // [Fig. 2 · 7]

            try {
                // [Fig. 3 · 20] = [Fig. 2 · 8] Paso 4: solicita la ubicación  /  [Fig. 2 · 19] Paso 5: la recibe
                UbicacionBolsin ubicacion = interfazGPSTracker.solicitarUbicacionBolsin(
                        numeroBolsin, codigoCMOrigen, codigoCMDestino, modeloGPSTracker);
                ubicacionesBolsines.put(numeroBolsin, ubicacion);
            } catch (GPSTrackerException e) {
                // A3: el GPS Tracker no pudo informar la ubicación del bolsín
                bolsinesSinUbicacion.add(new BolsinSinUbicacionDTO(numeroBolsin, bolsin.getNumeroPrecinto(),
                        bolsin.getNombreCMDestino(), modeloGPSTracker, e.getMessage()));
            }
        }
    }

    // [Fig. 3 · 21] armarMapaDePosicionDeBolsines() - Paso 6
    private MapaBolsinesDTO armarMapaDePosicionDeBolsines() {
        bolsinesEnMapa = new HashMap<>();
        List<BolsinEnMapaDTO> listaMapa = new ArrayList<>();
        List<ComisionMedicaDTO> comisionesDestino = new ArrayList<>();

        for (Bolsin bolsin : bolsinesEnviados) {
            UbicacionBolsin ubicacion = ubicacionesBolsines.get(bolsin.getNumeroBolsin());
            if (ubicacion == null) {
                continue; // A3: sin ubicación, no va al mapa
            }

            // [Fig. 3 · 22] datos del bolsín  y  [Fig. 3 · 23] datos de su ubicación
            BolsinEnMapaDTO datos = new BolsinEnMapaDTO(
                    bolsin.getNumeroBolsin(),
                    bolsin.getNumeroPrecinto(),
                    bolsin.getCodigoCMDestino(),
                    bolsin.getNombreCMDestino(),
                    bolsin.getModeloGPSTracker(),
                    ubicacion.getLatitud(),
                    ubicacion.getLongitud(),
                    ubicacion.getFechaHoraUltimaActualizacion());

            bolsinesEnMapa.put(datos.numeroBolsin(), datos);
            listaMapa.add(datos);

            // CM destino para el filtro de la pantalla (sin repetir)
            boolean yaEsta = false;
            for (ComisionMedicaDTO cm : comisionesDestino) {
                if (cm.codigo().equals(datos.codigoCMDestino())) {
                    yaEsta = true;
                }
            }
            if (!yaEsta) {
                comisionesDestino.add(new ComisionMedicaDTO(datos.codigoCMDestino(), datos.nombreCMDestino()));
            }
        }

        // [Fig. 3 · 24] respuesta para la pantalla
        ComisionMedicaDTO cmUsuario = new ComisionMedicaDTO(codigoCMUsuario, nombreCMUsuario);
        return new MapaBolsinesDTO(cmUsuario, listaMapa, bolsinesSinUbicacion, comisionesDestino);
    }

    // =====================================================================
    // tomarFiltro() - Paso 6 (opcional): filtro por número de precinto y/o CM destino
    // =====================================================================
    public List<BolsinEnMapaDTO> tomarFiltro(Integer numeroPrecinto, String codigoCMDestino) {
        List<BolsinEnMapaDTO> resultado = new ArrayList<>();

        for (Bolsin bolsin : bolsinesEnviados) {
            BolsinEnMapaDTO datos = bolsinesEnMapa.get(bolsin.getNumeroBolsin());
            if (datos == null) {
                continue;
            }
            boolean coincidePrecinto = numeroPrecinto == null || bolsin.esTuNumeroPrecinto(numeroPrecinto);
            boolean coincideDestino = codigoCMDestino == null || codigoCMDestino.isBlank()
                    || bolsin.esTuCMDestino(codigoCMDestino);

            if (coincidePrecinto && coincideDestino) {
                resultado.add(datos);
            }
        }

        // A2: no se encuentra bolsín con el número de precinto ingresado
        if (resultado.isEmpty() && numeroPrecinto != null) {
            throw new FlujoAlternativoException("A2",
                    "No se encontró un bolsín con el número de precinto " + numeroPrecinto + ".");
        }
        return resultado;
    }

    // =====================================================================
    // [Fig. 3 · 27] tomarSeleccionBolsin() - Paso 7: el EB selecciona un bolsín del mapa
    // =====================================================================
    public BolsinEnMapaDTO tomarSeleccionBolsin(int numeroBolsin) {
        // [Fig. 3 · 28] busca el bolsín seleccionado
        bolsinSeleccionado = null;
        for (Bolsin bolsin : bolsinesEnviados) {
            if (bolsin.esTuNumeroBolsin(numeroBolsin)) {
                bolsinSeleccionado = bolsin;
            }
        }
        if (bolsinSeleccionado == null || !bolsinesEnMapa.containsKey(numeroBolsin)) {
            throw new FlujoAlternativoException("SELECCION", "El bolsín seleccionado no está en el mapa.");
        }

        // [Fig. 3 · 29] devuelve los datos del bolsín (Paso 8: la pantalla pregunta si enviar el correo)
        return bolsinesEnMapa.get(numeroBolsin);
    }

    // =====================================================================
    // [Fig. 3 · 32] tomarConfirmacionDeMail() - Paso 9: el EB confirma (o no) el envío del correo
    // =====================================================================
    public MailEnviadoDTO tomarConfirmacionDeMail(boolean confirma) {
        if (bolsinSeleccionado == null) {
            throw new FlujoAlternativoException("SELECCION", "Primero debe seleccionar un bolsín.");
        }

        // A5: el EB no selecciona la opción para enviar el correo
        if (!confirma) {
            finCU();
            return null;
        }

        // [Fig. 3 · 33] Paso 10: busca el Gerente de la CM destino
        Empleado gerente = buscarMailDeGerenteCMDestino();

        // [Fig. 3 · 38] obtiene su mail y su nombre
        String mailGerente = gerente.getEmail();
        String nombreGerente = gerente.getNombreCompleto();

        // [Fig. 3 · 39] Paso 11: incluye el CU "Notificar ubicación de bolsín"
        MailEnviadoDTO mail = llamarCUNotificarUbicacion(mailGerente, nombreGerente);

        // [Fig. 3 · 44] fin del caso de uso
        finCU();

        // [Fig. 3 · 45] devuelve el correo enviado a la pantalla
        return mail;
    }

    // [Fig. 3 · 33] buscarMailDeGerenteCMDestino()
    private Empleado buscarMailDeGerenteCMDestino() {
        // [Fig. 3 · 34] CM destino del bolsín seleccionado
        ComisionMedica cmDestino = bolsinSeleccionado.getDestino();

        // [Fig. 3 · 35] busca el empleado que es Gerente de esa CM
        Empleado gerente = null;
        for (Empleado empleado : empleadoRepository.findAll()) {
            if (empleado.sosGCMDeCM(cmDestino)) {
                gerente = empleado;
            }
        }
        if (gerente == null) {
            throw new FlujoAlternativoException("GCM", "La CM destino no tiene un Gerente registrado.");
        }
        return gerente;
    }

    // [Fig. 3 · 39] llamarCUNotificarUbicacion()
    private MailEnviadoDTO llamarCUNotificarUbicacion(String mailGerente, String nombreGerente) {
        // [Fig. 3 · 40] número del bolsín seleccionado
        int numeroBolsin = bolsinSeleccionado.getNumeroBolsin();

        // [Fig. 3 · 41] fecha y hora de la última actualización y coordenadas
        UbicacionBolsin ubicacion = ubicacionesBolsines.get(numeroBolsin);
        LocalDateTime fechaHora = ubicacion.getFechaHoraUltimaActualizacion();
        double latitud = ubicacion.getLatitud();
        double longitud = ubicacion.getLongitud();

        // [Fig. 3 · 42] CU incluido Notificar ubicación de bolsín  /  [Fig. 3 · 43] devuelve el correo enviado
        return gestorNotificarUbicacionBolsin.notificarUbicacion(
                mailGerente, nombreGerente, numeroBolsin, fechaHora, latitud, longitud);
    }

    // A4: el EB cancela la ejecución del caso de uso
    public void cancelarCU() {
        finCU();
    }

    // [Fig. 3 · 44] finCU(): limpia los datos del caso de uso
    private void finCU() {
        comisionMedicaUsuario = null;
        codigoCMUsuario = null;
        nombreCMUsuario = null;
        bolsinesEnviados = new ArrayList<>();
        ubicacionesBolsines = new HashMap<>();
        bolsinesSinUbicacion = new ArrayList<>();
        bolsinesEnMapa = new HashMap<>();
        bolsinSeleccionado = null;
    }
}

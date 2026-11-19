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
    // consultarUbicacionBolsines() - Pasos 2 a 6 del CU
    // (synchronized: evita que dos consultas al mismo tiempo mezclen los datos)
    // =====================================================================
    public synchronized MapaBolsinesDTO consultarUbicacionBolsines() {
        // Paso 2: busca la CM del usuario logueado
        comisionMedicaUsuario = buscarCM();

        // Paso 3: busca los bolsines Enviados cuya CM origen es la del usuario
        buscarBolsinesEnviados();

        // A1: no se encuentran bolsines en estado Enviado
        if (bolsinesEnviados.isEmpty()) {
            throw new FlujoAlternativoException("A1",
                    "No se encontraron bolsines en estado Enviado para " + nombreCMUsuario + ".");
        }

        // Pasos 4 y 5: pide la ubicación de cada bolsín al GPS Tracker (patrón Adapter)
        obtenerUbicacionBolsines();

        // Paso 6: arma los datos del mapa y los devuelve a la pantalla
        return armarMapaDePosicionDeBolsines();
    }

    // buscarCM(): Sesion -> Usuario -> Empleado -> ComisionMedica
    private ComisionMedica buscarCM() {
        // busca la sesión activa
        Sesion sesionActiva = null;
        for (Sesion sesion : sesionRepository.findAll()) {
            if (sesion.estaActiva()) {
                sesionActiva = sesion;
            }
        }
        if (sesionActiva == null) {
            throw new FlujoAlternativoException("SESION", "No hay un usuario logueado.");
        }

        // obtiene el usuario de la sesión
        Usuario usuarioLogueado = sesionActiva.getUsuario();

        // busca el empleado de ese usuario
        Empleado empleadoLogueado = null;
        for (Empleado empleado : empleadoRepository.findAll()) {
            if (empleado.esTuUsuario(usuarioLogueado)) {
                empleadoLogueado = empleado;
            }
        }
        if (empleadoLogueado == null) {
            throw new FlujoAlternativoException("SESION", "El usuario logueado no tiene un empleado asociado.");
        }

        // obtiene la CM del empleado
        ComisionMedica cm = empleadoLogueado.getCM();

        // obtiene el código y el nombre de la CM (se muestran en pantalla)
        codigoCMUsuario = cm.getCodigo();
        nombreCMUsuario = cm.getNombre();
        return cm;
    }

    // buscarBolsinesEnviados()
    private void buscarBolsinesEnviados() {
        bolsinesEnviados = new ArrayList<>();
        for (Bolsin bolsin : bolsinRepository.findAll()) {
            // ¿está Enviado? y ¿su CM origen es la del usuario?
            if (bolsin.esEnviado() && bolsin.esTuCMOrigen(comisionMedicaUsuario)) {
                bolsinesEnviados.add(bolsin);
            }
        }
    }

    // obtenerUbicacionBolsines(): parte rediseñada con el patrón Adapter
    private void obtenerUbicacionBolsines() {
        ubicacionesBolsines = new HashMap<>();
        bolsinesSinUbicacion = new ArrayList<>();

        for (Bolsin bolsin : bolsinesEnviados) {
            // datos del bolsín que necesita el GPS Tracker
            int numeroBolsin = bolsin.getNumeroBolsin();
            String codigoCMOrigen = bolsin.getCodigoCMOrigen();
            String codigoCMDestino = bolsin.getCodigoCMDestino();
            String modeloGPSTracker = bolsin.getModeloGPSTracker();

            try {
                // Pasos 4 y 5: solicita la ubicación y la recibe
                UbicacionBolsin ubicacion = interfazGPSTracker.solicitarUbicacionBolsin(
                        numeroBolsin, codigoCMOrigen, codigoCMDestino, modeloGPSTracker);
                ubicacionesBolsines.put(numeroBolsin, ubicacion);
            } catch (GPSTrackerException e) {
                // A3: el GPS Tracker no pudo informar la ubicación del bolsín
                bolsinesSinUbicacion.add(new BolsinSinUbicacionDTO(numeroBolsin, bolsin.getNumeroPrecinto(),
                        bolsin.getNombreCMOrigen(), bolsin.getNombreCMDestino(), modeloGPSTracker, e.getMessage()));
            }
        }
    }

    // armarMapaDePosicionDeBolsines() - Paso 6
    private MapaBolsinesDTO armarMapaDePosicionDeBolsines() {
        bolsinesEnMapa = new HashMap<>();
        List<BolsinEnMapaDTO> listaMapa = new ArrayList<>();
        List<ComisionMedicaDTO> comisionesDestino = new ArrayList<>();

        for (Bolsin bolsin : bolsinesEnviados) {
            UbicacionBolsin ubicacion = ubicacionesBolsines.get(bolsin.getNumeroBolsin());
            if (ubicacion == null) {
                continue; // A3: sin ubicación, no va al mapa
            }

            // datos del bolsín y datos de su ubicación
            BolsinEnMapaDTO datos = new BolsinEnMapaDTO(
                    bolsin.getNumeroBolsin(),
                    bolsin.getNumeroPrecinto(),
                    bolsin.getNombreCMOrigen(),
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

        // respuesta para la pantalla
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
    // tomarSeleccionBolsin() - Paso 7: el EB selecciona un bolsín del mapa
    // =====================================================================
    public BolsinEnMapaDTO tomarSeleccionBolsin(int numeroBolsin) {
        // busca el bolsín seleccionado
        bolsinSeleccionado = null;
        for (Bolsin bolsin : bolsinesEnviados) {
            if (bolsin.esTuNumeroBolsin(numeroBolsin)) {
                bolsinSeleccionado = bolsin;
            }
        }
        if (bolsinSeleccionado == null || !bolsinesEnMapa.containsKey(numeroBolsin)) {
            throw new FlujoAlternativoException("SELECCION", "El bolsín seleccionado no está en el mapa.");
        }

        // devuelve los datos del bolsín (Paso 8: la pantalla pregunta si enviar el correo)
        return bolsinesEnMapa.get(numeroBolsin);
    }

    // =====================================================================
    // tomarConfirmacionDeMail() - Paso 9: el EB confirma (o no) el envío del correo
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

        // Paso 10: busca el Gerente de la CM destino
        Empleado gerente = buscarMailDeGerenteCMDestino();

        // obtiene su mail y su nombre
        String mailGerente = gerente.getEmail();
        String nombreGerente = gerente.getNombreCompleto();

        // Paso 11: incluye el CU "Notificar ubicación de bolsín"
        MailEnviadoDTO mail = llamarCUNotificarUbicacion(mailGerente, nombreGerente);

        // fin del caso de uso
        finCU();

        // devuelve el correo enviado a la pantalla
        return mail;
    }

    // buscarMailDeGerenteCMDestino()
    private Empleado buscarMailDeGerenteCMDestino() {
        // CM destino del bolsín seleccionado
        ComisionMedica cmDestino = bolsinSeleccionado.getDestino();

        // busca el empleado que es Gerente de esa CM
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

    // llamarCUNotificarUbicacion()
    private MailEnviadoDTO llamarCUNotificarUbicacion(String mailGerente, String nombreGerente) {
        // número del bolsín seleccionado
        int numeroBolsin = bolsinSeleccionado.getNumeroBolsin();

        // fecha y hora de la última actualización y coordenadas
        UbicacionBolsin ubicacion = ubicacionesBolsines.get(numeroBolsin);
        LocalDateTime fechaHora = ubicacion.getFechaHoraUltimaActualizacion();
        double latitud = ubicacion.getLatitud();
        double longitud = ubicacion.getLongitud();

        // llama al CU incluido Notificar ubicación de bolsín, que devuelve el correo enviado
        return gestorNotificarUbicacionBolsin.notificarUbicacion(
                mailGerente, nombreGerente, numeroBolsin, fechaHora, latitud, longitud);
    }

    // A4: el EB cancela la ejecución del caso de uso
    public void cancelarCU() {
        finCU();
    }

    // finCU(): limpia los datos del caso de uso
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

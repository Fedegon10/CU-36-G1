package ar.edu.utn.dsi.bolsines.entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad Bolsin
@Entity
public class Bolsin {

    @Id
    private Integer numeroBolsin;

    private LocalDate fecha;
    private Integer numeroPrecinto;
    private Integer peso; // en gramos

    // Modelo del GPS Tracker que lleva el bolsín (XTR-4500L, NavTrack-QX-7A o GeoPulse-MTR-900)
    private String modeloGPSTracker;

    @ManyToOne(fetch = FetchType.EAGER)
    private ComisionMedica origen;

    @ManyToOne(fetch = FetchType.EAGER)
    private ComisionMedica destino;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "bolsin_numero")
    private List<CambioEstadoBolsin> cambioEstado = new ArrayList<>();

    protected Bolsin() {
    }

    public Bolsin(Integer numeroBolsin, LocalDate fecha, Integer numeroPrecinto, Integer peso,
                  String modeloGPSTracker, ComisionMedica origen, ComisionMedica destino) {
        this.numeroBolsin = numeroBolsin;
        this.fecha = fecha;
        this.numeroPrecinto = numeroPrecinto;
        this.peso = peso;
        this.modeloGPSTracker = modeloGPSTracker;
        this.origen = origen;
        this.destino = destino;
    }

    // Indica si el estado actual del bolsín es Enviado
    public boolean esEnviado() {
        // busca el cambio de estado actual
        CambioEstadoBolsin actual = null;
        for (CambioEstadoBolsin cambio : cambioEstado) {
            if (cambio.esActual()) {
                actual = cambio;
            }
        }
        // le pregunta al cambio de estado actual si es Enviado
        return actual != null && actual.esEnviado();
    }

    // Indica si la CM de origen del bolsín es la CM recibida
    public boolean esTuCMOrigen(ComisionMedica comisionMedica) {
        return origen.esMiCM(comisionMedica);
    }

    // Indica si el bolsín tiene el número recibido
    public boolean esTuNumeroBolsin(int numero) {
        return numeroBolsin == numero;
    }

    // Código de la CM origen (se lo pide a su ComisionMedica)
    public String getCodigoCMOrigen() {
        return origen.getCodigo();
    }

    // Nombre de la CM origen (se lo pide a su ComisionMedica)
    public String getNombreCMOrigen() {
        return origen.getNombre();
    }

    // Código de la CM destino (se lo pide a su ComisionMedica)
    public String getCodigoCMDestino() {
        return destino.getCodigo();
    }

    // Nombre de la CM destino (se lo pide a su ComisionMedica)
    public String getNombreCMDestino() {
        return destino.getNombre();
    }

    // Indica si el bolsín tiene el número de precinto recibido
    public boolean esTuNumeroPrecinto(Integer numero) {
        return numeroPrecinto != null && numeroPrecinto.equals(numero);
    }

    // Indica si la CM de destino del bolsín tiene el código recibido (filtro, A2)
    public boolean esTuCMDestino(String codigoCM) {
        return destino.getCodigo().equals(codigoCM);
    }

    // Cierra el estado actual y agrega uno nuevo (usado al cargar datos de prueba)
    public void cambiarEstado(Estado nuevoEstado, Empleado responsable, LocalDateTime fechaHora) {
        for (CambioEstadoBolsin cambio : cambioEstado) {
            if (cambio.esActual()) {
                cambio.setFechaHoraFin(fechaHora);
            }
        }
        cambioEstado.add(new CambioEstadoBolsin(fechaHora, nuevoEstado, responsable));
    }

    public Integer getNumeroBolsin() {
        return numeroBolsin;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Integer getNumeroPrecinto() {
        return numeroPrecinto;
    }

    public Integer getPeso() {
        return peso;
    }

    public String getModeloGPSTracker() {
        return modeloGPSTracker;
    }

    public ComisionMedica getOrigen() {
        return origen;
    }

    public ComisionMedica getDestino() {
        return destino;
    }

    public List<CambioEstadoBolsin> getCambioEstado() {
        return cambioEstado;
    }
}

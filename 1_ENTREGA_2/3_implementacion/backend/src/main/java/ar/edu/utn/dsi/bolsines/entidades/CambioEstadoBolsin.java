package ar.edu.utn.dsi.bolsines.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

// Entidad CambioEstadoBolsin: historial de estados de un bolsín
@Entity
public class CambioEstadoBolsin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    @ManyToOne(fetch = FetchType.EAGER)
    private Estado estado;

    // Empleado responsable del cambio de estado
    @ManyToOne(fetch = FetchType.EAGER)
    private Empleado responsable;

    protected CambioEstadoBolsin() {
    }

    public CambioEstadoBolsin(LocalDateTime fechaHoraInicio, Estado estado, Empleado responsable) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.estado = estado;
        this.responsable = responsable;
    }

    // Es el cambio de estado actual si no tiene fecha de fin
    public boolean esActual() {
        return fechaHoraFin == null;
    }

    // Consulta a su estado si es Enviado
    public boolean esEnviado() {
        return estado.esEnviado();
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public Estado getEstado() {
        return estado;
    }

    public Empleado getResponsable() {
        return responsable;
    }
}

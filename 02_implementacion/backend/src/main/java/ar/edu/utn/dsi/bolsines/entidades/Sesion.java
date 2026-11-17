package ar.edu.utn.dsi.bolsines.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

// Entidad Sesion: sesión de un usuario en el sistema
@Entity
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario usuario;

    protected Sesion() {
    }

    public Sesion(Usuario usuario, LocalDateTime fechaHoraInicio) {
        this.usuario = usuario;
        this.fechaHoraInicio = fechaHoraInicio;
    }

    // [Fig. 3 · 5] La sesión está activa si no tiene fecha de fin
    public boolean estaActiva() {
        return fechaHoraFin == null;
    }

    public void cerrar(LocalDateTime fechaHora) {
        this.fechaHoraFin = fechaHora;
    }

    // [Fig. 3 · 6]
    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }
}

package ar.edu.utn.dsi.bolsines.patrones.adapter;

import java.time.LocalDateTime;

// Formato común de ubicación que esperan el gestor y la pantalla.
// Todos los adaptadores devuelven la ubicación en este formato.
public class UbicacionBolsin {

    private final int numeroBolsin;
    private final double latitud;
    private final double longitud;
    private final LocalDateTime fechaHoraUltimaActualizacion;

    // [Fig. 2 · 17] la crea el adaptador concreto
    public UbicacionBolsin(int numeroBolsin, double latitud, double longitud,
                           LocalDateTime fechaHoraUltimaActualizacion) {
        this.numeroBolsin = numeroBolsin;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fechaHoraUltimaActualizacion = fechaHoraUltimaActualizacion;
    }

    public int getNumeroBolsin() {
        return numeroBolsin;
    }

    // [Fig. 3 · 23 y 41]
    public double getLatitud() {
        return latitud;
    }

    // [Fig. 3 · 23 y 41]
    public double getLongitud() {
        return longitud;
    }

    // [Fig. 3 · 23 y 41]
    public LocalDateTime getFechaHoraUltimaActualizacion() {
        return fechaHoraUltimaActualizacion;
    }
}

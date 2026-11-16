package ar.edu.utn.dsi.bolsines.patrones.adapter;

// PATRÓN ADAPTER - Target (interfaz objetivo)
// Interfaz común que implementan los adaptadores de cada modelo de GPS Tracker.
public interface IAdaptadorGPSTracker {

    // Devuelve la ubicación del bolsín en el formato común (UbicacionBolsin)
    UbicacionBolsin obtenerUbicacion(int numeroBolsin, String codigoCMOrigen, String codigoCMDestino);
}

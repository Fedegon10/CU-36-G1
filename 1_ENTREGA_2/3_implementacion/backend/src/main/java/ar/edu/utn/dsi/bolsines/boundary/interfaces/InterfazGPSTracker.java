package ar.edu.utn.dsi.bolsines.boundary.interfaces;

import ar.edu.utn.dsi.bolsines.patrones.adapter.AdaptadorGPSTrackerGeoPulseMTR900;
import ar.edu.utn.dsi.bolsines.patrones.adapter.AdaptadorGPSTrackerNavTrackQX7A;
import ar.edu.utn.dsi.bolsines.patrones.adapter.AdaptadorGPSTrackerXTR4500L;
import ar.edu.utn.dsi.bolsines.excepciones.GPSTrackerException;
import ar.edu.utn.dsi.bolsines.patrones.adapter.IAdaptadorGPSTracker;
import ar.edu.utn.dsi.bolsines.patrones.adapter.serviciosexternos.ServicioGPSTrackerGeoPulseMTR900;
import ar.edu.utn.dsi.bolsines.patrones.adapter.serviciosexternos.ServicioGPSTrackerNavTrackQX7A;
import ar.edu.utn.dsi.bolsines.patrones.adapter.serviciosexternos.ServicioGPSTrackerXTR4500L;
import ar.edu.utn.dsi.bolsines.patrones.adapter.UbicacionBolsin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// <<boundary>> InterfazGPSTracker
// PATRÓN ADAPTER - Cliente: usa siempre IAdaptadorGPSTracker, sin conocer el formato de cada modelo.
@Component
public class InterfazGPSTracker {

    public static final String MODELO_XTR_4500L = "XTR-4500L";
    public static final String MODELO_NAVTRACK_QX_7A = "NavTrack QX-7A";
    public static final String MODELO_GEOPULSE_MTR_900 = "GeoPulse MTR-900";

    private final RestClient restClient;

    // Claves API de cada modelo (definidas en application.properties)
    @Value("${gps.tracker.clave-api.xtr4500l}")
    private String claveAPIXtr;

    @Value("${gps.tracker.clave-api.navtrackqx7a}")
    private String claveAPINavTrack;

    @Value("${gps.tracker.clave-api.geopulsemtr900}")
    private String claveAPIGeoPulse;

    public InterfazGPSTracker(@Value("${gps.tracker.base-url}") String urlGPSTracker) {
        this.restClient = RestClient.builder().baseUrl(urlGPSTracker).build();
    }

    // Solicita la ubicación de un bolsín al GPS Tracker que corresponde a su modelo
    public UbicacionBolsin solicitarUbicacionBolsin(int numeroBolsin, String codigoCMOrigen,
                                                    String codigoCMDestino, String modeloGPSTracker) {
        // crea el adaptador concreto
        IAdaptadorGPSTracker adaptador = crearAdaptador(modeloGPSTracker);

        // llama al método polimórfico, recibe UbicacionBolsin y la devuelve al gestor
        return adaptador.obtenerUbicacion(numeroBolsin, codigoCMOrigen, codigoCMDestino);
    }

    // Crea el adaptador concreto según el modelo de GPS Tracker
    private IAdaptadorGPSTracker crearAdaptador(String modeloGPSTracker) {
        if (MODELO_XTR_4500L.equals(modeloGPSTracker)) {
            ServicioGPSTrackerXTR4500L servicio = new ServicioGPSTrackerXTR4500L(restClient);
            return new AdaptadorGPSTrackerXTR4500L(claveAPIXtr, servicio);
        }
        if (MODELO_NAVTRACK_QX_7A.equals(modeloGPSTracker)) {
            ServicioGPSTrackerNavTrackQX7A servicio = new ServicioGPSTrackerNavTrackQX7A(restClient);
            return new AdaptadorGPSTrackerNavTrackQX7A(claveAPINavTrack, servicio);
        }
        if (MODELO_GEOPULSE_MTR_900.equals(modeloGPSTracker)) {
            ServicioGPSTrackerGeoPulseMTR900 servicio = new ServicioGPSTrackerGeoPulseMTR900(restClient);
            return new AdaptadorGPSTrackerGeoPulseMTR900(claveAPIGeoPulse, servicio);
        }
        throw new GPSTrackerException("Modelo de GPS Tracker no soportado: " + modeloGPSTracker);
    }
}

package ar.edu.dsi.gpstracker.exception;

// El bolsín no existe en la API (responde 404). Es lo que provoca el A3 con el bolsín 116
public class BolsinNotFoundException extends RuntimeException {
    public BolsinNotFoundException(Integer numeroBolsin) {
        super("No se encontro un bolsin con numero " + numeroBolsin + ".");
    }
}

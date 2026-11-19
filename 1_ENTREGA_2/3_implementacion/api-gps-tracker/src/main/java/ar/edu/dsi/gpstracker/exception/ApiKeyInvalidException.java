package ar.edu.dsi.gpstracker.exception;

// La API Key no es válida para el modelo (responde 401)
public class ApiKeyInvalidException extends RuntimeException {
    public ApiKeyInvalidException(String message) {
        super(message);
    }
}

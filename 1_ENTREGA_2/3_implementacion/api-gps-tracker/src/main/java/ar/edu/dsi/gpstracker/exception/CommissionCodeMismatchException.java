package ar.edu.dsi.gpstracker.exception;

// El código de CM recibido no coincide con el del bolsín (responde 404)
public class CommissionCodeMismatchException extends RuntimeException {
    public CommissionCodeMismatchException(String message) {
        super(message);
    }
}

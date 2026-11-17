package ar.edu.utn.dsi.bolsines.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// Entidad Estado (ámbito: Bolsin, Remito o Documentacion)
@Entity
public class Estado {

    public static final String AMBITO_BOLSIN = "Bolsin";
    public static final String ENVIADO = "Enviado";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ambito;
    private String nombre;
    private String descripcion;

    protected Estado() {
    }

    public Estado(String ambito, String nombre, String descripcion) {
        this.ambito = ambito;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // [Fig. 3 · 15] Indica si el estado pertenece al ámbito Bolsín
    public boolean esAmbitoBolsin() {
        return AMBITO_BOLSIN.equals(ambito);
    }

    // [Fig. 3 · 14] Indica si es el estado Enviado de un bolsín
    public boolean esEnviado() {
        return esAmbitoBolsin() && ENVIADO.equals(nombre);
    }

    public String getAmbito() {
        return ambito;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

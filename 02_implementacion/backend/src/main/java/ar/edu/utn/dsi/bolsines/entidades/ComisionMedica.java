package ar.edu.utn.dsi.bolsines.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// Entidad ComisionMedica
@Entity
public class ComisionMedica {

    @Id
    private String codigo; // ej: CM-CBA-01

    private String nombre;
    private String direccion;
    private String email;
    private String telefono;

    protected ComisionMedica() {
    }

    public ComisionMedica(String codigo, String nombre, String direccion, String email, String telefono) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
    }

    // [Fig. 3 · 17 y 37] Indica si es la misma CM que la recibida
    public boolean esMiCM(ComisionMedica otra) {
        return otra != null && codigo.equals(otra.getCodigo());
    }

    // [Fig. 2 · 4 y 6] [Fig. 3 · 9]
    public String getCodigo() {
        return codigo;
    }

    // [Fig. 3 · 9]
    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }
}

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

    // Indica si es la misma CM que la recibida
    public boolean esMiCM(ComisionMedica otra) {
        return otra != null && codigo.equals(otra.getCodigo());
    }

    public String getCodigo() {
        return codigo;
    }

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

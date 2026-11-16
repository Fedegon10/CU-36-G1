package ar.edu.utn.dsi.bolsines.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

// Entidad Usuario
@Entity
public class Usuario {

    @Id
    private String nombre;

    private String password;

    protected Usuario() {
    }

    public Usuario(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
    }

    // Valida la contraseña ingresada
    public boolean esTuPassword(String passwordIngresada) {
        return password.equals(passwordIngresada);
    }

    public String getNombre() {
        return nombre;
    }
}

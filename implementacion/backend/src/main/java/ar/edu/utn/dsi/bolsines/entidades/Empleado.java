package ar.edu.utn.dsi.bolsines.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

// Entidad Empleado
@Entity
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    private Rol rol;

    // CM a la que está asignado el empleado
    @ManyToOne(fetch = FetchType.EAGER)
    private ComisionMedica asignadaA;

    @OneToOne(fetch = FetchType.EAGER)
    private Usuario usuario;

    protected Empleado() {
    }

    public Empleado(String nombre, String apellido, String email, Rol rol,
                    ComisionMedica asignadaA, Usuario usuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
        this.asignadaA = asignadaA;
        this.usuario = usuario;
    }

    // Indica si el empleado corresponde al usuario recibido
    public boolean esTuUsuario(Usuario otroUsuario) {
        return usuario != null && usuario.getNombre().equals(otroUsuario.getNombre());
    }

    // Indica si el empleado es Gerente de la CM recibida (consulta a su Rol y a su CM)
    public boolean sosGCMDeCM(ComisionMedica comisionMedica) {
        return rol.esGCM() && asignadaA.esMiCM(comisionMedica);
    }

    public ComisionMedica getCM() {
        return asignadaA;
    }

    public String getEmail() {
        return email;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Rol getRol() {
        return rol;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}

package co.com.api.co.com.api.domain.roles;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "permisos")
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Column
    private String descripcion;
    
    @Column
    private String recurso;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Accion accion;
    
    @ManyToMany(mappedBy = "permisos")
    @JsonIgnore
    private List<Rol> roles;
    
    // Enum para acciones
    public enum Accion {
        CREAR, LEER, ACTUALIZAR, ELIMINAR, ADMINISTRAR
    }
    
    // Constructores
    public Permiso() {}
    
    public Permiso(String nombre, String descripcion, String recurso, Accion accion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.recurso = recurso;
        this.accion = accion;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getRecurso() {
        return recurso;
    }
    
    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }
    
    public Accion getAccion() {
        return accion;
    }
    
    public void setAccion(Accion accion) {
        this.accion = accion;
    }
    
    public List<Rol> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }
}

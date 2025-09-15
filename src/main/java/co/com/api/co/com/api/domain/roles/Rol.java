package co.com.api.co.com.api.domain.roles;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Column
    private String descripcion;
    
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<co.com.api.co.com.api.domain.usuarios.Usuario> usuarios;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rol_permisos",
        joinColumns = @JoinColumn(name = "rol_id"),
        inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private List<Permiso> permisos;
    
    // Constructores
    public Rol() {}
    
    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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
    
    public List<co.com.api.co.com.api.domain.usuarios.Usuario> getUsuarios() {
        return usuarios;
    }
    
    public void setUsuarios(List<co.com.api.co.com.api.domain.usuarios.Usuario> usuarios) {
        this.usuarios = usuarios;
    }
    
    public List<Permiso> getPermisos() {
        return permisos;
    }
    
    public void setPermisos(List<Permiso> permisos) {
        this.permisos = permisos;
    }
}

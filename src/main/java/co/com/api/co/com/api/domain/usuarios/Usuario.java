package co.com.api.co.com.api.domain.usuarios;

import co.com.api.co.com.api.domain.roles.Rol;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String user;
    
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    
    @Column(nullable = false)
    private String email;
    
    @Column
    private String nombre;
    
    @Column
    private String apellido;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private List<Rol> roles;
    
    @Column(nullable = false)
    private Boolean activo = true;

    // Constructores
    public Usuario() {}
    
    public Usuario(String user, String password, String email, String nombre, String apellido) {
        this.user = user;
        this.password = password;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public List<Rol> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    // Implementación de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        
        return roles.stream()
                .flatMap(rol -> {
                    if (rol.getPermisos() != null) {
                        return rol.getPermisos().stream()
                                .map(permiso -> new SimpleGrantedAuthority(permiso.getNombre()));
                    }
                    return List.<SimpleGrantedAuthority>of().stream();
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return user;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo != null && activo;
    }
}

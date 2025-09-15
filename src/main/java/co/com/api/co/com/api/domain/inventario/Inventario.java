package co.com.api.co.com.api.domain.inventario;

import co.com.api.co.com.api.domain.productos.Producto;
import co.com.api.co.com.api.domain.empleados.Empleado;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inventario")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToMany
    @JoinTable(
        name = "inventario_productos",
        joinColumns = @JoinColumn(name = "inventario_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos;
    
    @ManyToOne
    @JoinColumn(name = "responsable_id", nullable = false)
    private Empleado responsable;
    
    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime ultimaActualizacion;
    
    @Column
    private String descripcion;
    
    // Constructores
    public Inventario() {}
    
    public Inventario(List<Producto> productos, Empleado responsable, 
                     LocalDateTime ultimaActualizacion, String descripcion) {
        this.productos = productos;
        this.responsable = responsable;
        this.ultimaActualizacion = ultimaActualizacion;
        this.descripcion = descripcion;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public List<Producto> getProductos() {
        return productos;
    }
    
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
    
    public Empleado getResponsable() {
        return responsable;
    }
    
    public void setResponsable(Empleado responsable) {
        this.responsable = responsable;
    }
    
    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }
    
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

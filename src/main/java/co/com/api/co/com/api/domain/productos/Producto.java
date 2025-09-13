package co.com.api.co.com.api.domain.productos;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(name = "fecha_expiracion")
    private LocalDate fechaExpiracion;
    
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    
    @Column(nullable = false)
    private String categoria;
    
    @Column(nullable = false)
    private Integer stock;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    // Constructores
    public Producto() {}
    
    public Producto(String nombre, LocalDate fechaExpiracion, LocalDate fechaVencimiento, 
                   String categoria, Integer stock, BigDecimal precio) {
        this.nombre = nombre;
        this.fechaExpiracion = fechaExpiracion;
        this.fechaVencimiento = fechaVencimiento;
        this.categoria = categoria;
        this.stock = stock;
        this.precio = precio;
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
    
    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }
    
    public void setFechaExpiracion(LocalDate fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
    
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}

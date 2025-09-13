package co.com.api.co.com.api.domain.proveedores;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "proveedores")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String ubicacion;
    
    @Column(nullable = false)
    private LocalDate antiguedad;
    
    @Column(name = "ultima_entrega")
    private LocalDate ultimaEntrega;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(unique = true)
    private String nit;
    
    @Column
    private String telefono;
    
    @Column
    private String correo;
    
    // Constructores
    public Proveedor() {}
    
    public Proveedor(String nombre, String ubicacion, LocalDate antiguedad, 
                    LocalDate ultimaEntrega, String nit, String telefono, String correo) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.antiguedad = antiguedad;
        this.ultimaEntrega = ultimaEntrega;
        this.nit = nit;
        this.telefono = telefono;
        this.correo = correo;
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
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    public LocalDate getAntiguedad() {
        return antiguedad;
    }
    
    public void setAntiguedad(LocalDate antiguedad) {
        this.antiguedad = antiguedad;
    }
    
    public LocalDate getUltimaEntrega() {
        return ultimaEntrega;
    }
    
    public void setUltimaEntrega(LocalDate ultimaEntrega) {
        this.ultimaEntrega = ultimaEntrega;
    }
    
    public String getNit() {
        return nit;
    }
    
    public void setNit(String nit) {
        this.nit = nit;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
}

package co.com.api.co.com.api.domain.empleados;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false, unique = true)
    private String cedula;
    
    @Column(nullable = false)
    private Integer edad;
    
    @Column(nullable = false)
    private String telefono;
    
    @Column(nullable = false, unique = true)
    private String correo;
    
    @Column(nullable = false)
    private LocalDate antiguedad;
    
    // Constructores
    public Empleado() {}
    
    public Empleado(String nombre, String cedula, Integer edad, String telefono, 
                   String correo, LocalDate antiguedad) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.edad = edad;
        this.telefono = telefono;
        this.correo = correo;
        this.antiguedad = antiguedad;
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
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public Integer getEdad() {
        return edad;
    }
    
    public void setEdad(Integer edad) {
        this.edad = edad;
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
    
    public LocalDate getAntiguedad() {
        return antiguedad;
    }
    
    public void setAntiguedad(LocalDate antiguedad) {
        this.antiguedad = antiguedad;
    }
}

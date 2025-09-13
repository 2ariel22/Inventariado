package co.com.api.co.com.api.domain.contabilidad;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contabilidad")
public class Contabilidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal gastos;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ingresos;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Column
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipoMovimiento;
    
    // Enum para tipo de movimiento
    public enum TipoMovimiento {
        GASTO, INGRESO
    }
    
    // Constructores
    public Contabilidad() {}
    
    public Contabilidad(BigDecimal gastos, BigDecimal ingresos, LocalDate fecha, 
                       String descripcion, TipoMovimiento tipoMovimiento) {
        this.gastos = gastos;
        this.ingresos = ingresos;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.tipoMovimiento = tipoMovimiento;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getGastos() {
        return gastos;
    }
    
    public void setGastos(BigDecimal gastos) {
        this.gastos = gastos;
    }
    
    public BigDecimal getIngresos() {
        return ingresos;
    }
    
    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }
    
    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
}

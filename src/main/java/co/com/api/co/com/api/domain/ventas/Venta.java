package co.com.api.co.com.api.domain.ventas;

import co.com.api.co.com.api.domain.productos.Producto;
import co.com.api.co.com.api.domain.clientes.Cliente;
import co.com.api.co.com.api.domain.empleados.Empleado;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @ManyToMany
    @JoinTable(
        name = "venta_productos",
        joinColumns = @JoinColumn(name = "venta_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos;
    
    @Column(name = "valor_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorVenta;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Empleado vendedor;
    
    @Column
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVenta estado;
    
    // Enum para estado de venta
    public enum EstadoVenta {
        PENDIENTE, COMPLETADA, CANCELADA
    }
    
    // Constructores
    public Venta() {}
    
    public Venta(LocalDate fecha, List<Producto> productos, BigDecimal valorVenta, 
                Cliente cliente, Empleado vendedor, String descripcion, EstadoVenta estado) {
        this.fecha = fecha;
        this.productos = productos;
        this.valorVenta = valorVenta;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.descripcion = descripcion;
        this.estado = estado;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public List<Producto> getProductos() {
        return productos;
    }
    
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
    
    public BigDecimal getValorVenta() {
        return valorVenta;
    }
    
    public void setValorVenta(BigDecimal valorVenta) {
        this.valorVenta = valorVenta;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public Empleado getVendedor() {
        return vendedor;
    }
    
    public void setVendedor(Empleado vendedor) {
        this.vendedor = vendedor;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public EstadoVenta getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }
}

package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.ventas.Venta;
import co.com.api.co.com.api.domain.ventas.VentaRepository;
import co.com.api.co.com.api.domain.ventas.DetalleVenta;
import co.com.api.co.com.api.domain.clientes.Cliente;
import co.com.api.co.com.api.domain.clientes.ClienteRepository;
import co.com.api.co.com.api.domain.empleados.Empleado;
import co.com.api.co.com.api.domain.empleados.EmpleadoRepository;
import co.com.api.co.com.api.domain.productos.Producto;
import co.com.api.co.com.api.domain.productos.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // GET - Obtener todas las ventas
    @GetMapping
    public ResponseEntity<List<Venta>> getAllVentas() {
        try {
            List<Venta> ventas = ventaRepository.findAll();
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener venta por ID
    @GetMapping("/{id}")
    public ResponseEntity<Venta> getVentaById(@PathVariable Long id) {
        try {
            Optional<Venta> venta = ventaRepository.findById(id);
            return venta.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener ventas por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Venta>> getVentasByCliente(@PathVariable Long clienteId) {
        try {
            Optional<Cliente> cliente = clienteRepository.findById(clienteId);
            if (cliente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<Venta> ventas = ventaRepository.findByCliente(cliente.get());
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener ventas por vendedor
    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<List<Venta>> getVentasByVendedor(@PathVariable Long vendedorId) {
        try {
            Optional<Empleado> vendedor = empleadoRepository.findById(vendedorId);
            if (vendedor.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<Venta> ventas = ventaRepository.findByVendedor(vendedor.get());
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener ventas por rango de fechas
    @GetMapping("/fechas")
    public ResponseEntity<List<Venta>> getVentasByFechas(
            @RequestParam String fechaInicio, 
            @RequestParam String fechaFin) {
        try {
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);
            List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener ventas por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Venta>> getVentasByEstado(@PathVariable String estado) {
        try {
            Venta.EstadoVenta estadoVenta = Venta.EstadoVenta.valueOf(estado.toUpperCase());
            List<Venta> ventas = ventaRepository.findByEstado(estadoVenta);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // POST - Crear nueva venta
    @PostMapping
    public ResponseEntity<Venta> createVenta(@RequestBody Venta venta) {
        try {
            // Verificar que el cliente existe
            if (venta.getCliente() != null && venta.getCliente().getId() != null) {
                Optional<Cliente> cliente = clienteRepository.findById(venta.getCliente().getId());
                if (cliente.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                venta.setCliente(cliente.get());
            }

            // Verificar que el vendedor existe
            if (venta.getVendedor() != null && venta.getVendedor().getId() != null) {
                Optional<Empleado> vendedor = empleadoRepository.findById(venta.getVendedor().getId());
                if (vendedor.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                venta.setVendedor(vendedor.get());
            }

            // Establecer fecha actual si no se proporciona
            if (venta.getFecha() == null) {
                venta.setFecha(LocalDate.now());
            }

            // Establecer estado por defecto
            if (venta.getEstado() == null) {
                venta.setEstado(Venta.EstadoVenta.PENDIENTE);
            }

            Venta nuevaVenta = ventaRepository.save(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT - Actualizar venta
    @PutMapping("/{id}")
    public ResponseEntity<Venta> updateVenta(@PathVariable Long id, @RequestBody Venta ventaActualizada) {
        try {
            Optional<Venta> ventaExistente = ventaRepository.findById(id);
            if (ventaExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Venta venta = ventaExistente.get();
            venta.setFecha(ventaActualizada.getFecha());
            venta.setValorVenta(ventaActualizada.getValorVenta());
            venta.setDescripcion(ventaActualizada.getDescripcion());
            venta.setEstado(ventaActualizada.getEstado());
            venta.setProductos(ventaActualizada.getProductos());

            // Verificar que el cliente existe si se está actualizando
            if (ventaActualizada.getCliente() != null && ventaActualizada.getCliente().getId() != null) {
                Optional<Cliente> cliente = clienteRepository.findById(ventaActualizada.getCliente().getId());
                if (cliente.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                venta.setCliente(cliente.get());
            }

            // Verificar que el vendedor existe si se está actualizando
            if (ventaActualizada.getVendedor() != null && ventaActualizada.getVendedor().getId() != null) {
                Optional<Empleado> vendedor = empleadoRepository.findById(ventaActualizada.getVendedor().getId());
                if (vendedor.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                venta.setVendedor(vendedor.get());
            }

            Venta ventaGuardada = ventaRepository.save(venta);
            return ResponseEntity.ok(ventaGuardada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar estado de venta
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Venta> updateEstado(@PathVariable Long id, @RequestParam String estado) {
        try {
            Optional<Venta> venta = ventaRepository.findById(id);
            if (venta.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Venta.EstadoVenta nuevoEstado = Venta.EstadoVenta.valueOf(estado.toUpperCase());
            Venta ventaActualizada = venta.get();
            ventaActualizada.setEstado(nuevoEstado);
            ventaRepository.save(ventaActualizada);

            return ResponseEntity.ok(ventaActualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // PATCH - Agregar producto a la venta
    @PatchMapping("/{id}/agregar-producto")
    public ResponseEntity<Venta> agregarProducto(@PathVariable Long id, @RequestParam Long productoId) {
        try {
            Optional<Venta> venta = ventaRepository.findById(id);
            if (venta.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Producto> producto = productoRepository.findById(productoId);
            if (producto.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Venta ventaActualizada = venta.get();
            if (!ventaActualizada.getProductos().contains(producto.get())) {
                ventaActualizada.getProductos().add(producto.get());
                ventaRepository.save(ventaActualizada);
            }

            return ResponseEntity.ok(ventaActualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Remover producto de la venta
    @PatchMapping("/{id}/remover-producto")
    public ResponseEntity<Venta> removerProducto(@PathVariable Long id, @RequestParam Long productoId) {
        try {
            Optional<Venta> venta = ventaRepository.findById(id);
            if (venta.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Producto> producto = productoRepository.findById(productoId);
            if (producto.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Venta ventaActualizada = venta.get();
            ventaActualizada.getProductos().remove(producto.get());
            ventaRepository.save(ventaActualizada);

            return ResponseEntity.ok(ventaActualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE - Eliminar venta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long id) {
        try {
            if (!ventaRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            ventaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

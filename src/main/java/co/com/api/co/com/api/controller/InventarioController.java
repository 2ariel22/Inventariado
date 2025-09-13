package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.inventario.Inventario;
import co.com.api.co.com.api.domain.inventario.InventarioRepository;
import co.com.api.co.com.api.domain.empleados.Empleado;
import co.com.api.co.com.api.domain.empleados.EmpleadoRepository;
import co.com.api.co.com.api.domain.productos.Producto;
import co.com.api.co.com.api.domain.productos.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // GET - Obtener todos los inventarios
    @GetMapping
    public ResponseEntity<List<Inventario>> getAllInventarios() {
        try {
            List<Inventario> inventarios = inventarioRepository.findAll();
            return ResponseEntity.ok(inventarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener inventario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> getInventarioById(@PathVariable Long id) {
        try {
            Optional<Inventario> inventario = inventarioRepository.findById(id);
            return inventario.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener inventarios por responsable
    @GetMapping("/responsable/{responsableId}")
    public ResponseEntity<List<Inventario>> getInventariosByResponsable(@PathVariable Long responsableId) {
        try {
            Optional<Empleado> responsable = empleadoRepository.findById(responsableId);
            if (responsable.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<Inventario> inventarios = inventarioRepository.findByResponsable(responsable.get());
            return ResponseEntity.ok(inventarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear nuevo inventario
    @PostMapping
    public ResponseEntity<Inventario> createInventario(@RequestBody Inventario inventario) {
        try {
            // Verificar que el responsable existe
            if (inventario.getResponsable() != null && inventario.getResponsable().getId() != null) {
                Optional<Empleado> responsable = empleadoRepository.findById(inventario.getResponsable().getId());
                if (responsable.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                inventario.setResponsable(responsable.get());
            }

            // Establecer fecha de última actualización
            inventario.setUltimaActualizacion(LocalDateTime.now());

            Inventario nuevoInventario = inventarioRepository.save(inventario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInventario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT - Actualizar inventario
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> updateInventario(@PathVariable Long id, @RequestBody Inventario inventarioActualizado) {
        try {
            Optional<Inventario> inventarioExistente = inventarioRepository.findById(id);
            if (inventarioExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Inventario inventario = inventarioExistente.get();
            inventario.setDescripcion(inventarioActualizado.getDescripcion());
            inventario.setProductos(inventarioActualizado.getProductos());
            inventario.setUltimaActualizacion(LocalDateTime.now());

            // Verificar que el responsable existe si se está actualizando
            if (inventarioActualizado.getResponsable() != null && inventarioActualizado.getResponsable().getId() != null) {
                Optional<Empleado> responsable = empleadoRepository.findById(inventarioActualizado.getResponsable().getId());
                if (responsable.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }
                inventario.setResponsable(responsable.get());
            }

            Inventario inventarioGuardado = inventarioRepository.save(inventario);
            return ResponseEntity.ok(inventarioGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Agregar producto al inventario
    @PatchMapping("/{id}/agregar-producto")
    public ResponseEntity<Inventario> agregarProducto(@PathVariable Long id, @RequestParam Long productoId) {
        try {
            Optional<Inventario> inventario = inventarioRepository.findById(id);
            if (inventario.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Producto> producto = productoRepository.findById(productoId);
            if (producto.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Inventario inventarioActualizado = inventario.get();
            if (!inventarioActualizado.getProductos().contains(producto.get())) {
                inventarioActualizado.getProductos().add(producto.get());
                inventarioActualizado.setUltimaActualizacion(LocalDateTime.now());
                inventarioRepository.save(inventarioActualizado);
            }

            return ResponseEntity.ok(inventarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Remover producto del inventario
    @PatchMapping("/{id}/remover-producto")
    public ResponseEntity<Inventario> removerProducto(@PathVariable Long id, @RequestParam Long productoId) {
        try {
            Optional<Inventario> inventario = inventarioRepository.findById(id);
            if (inventario.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Producto> producto = productoRepository.findById(productoId);
            if (producto.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Inventario inventarioActualizado = inventario.get();
            inventarioActualizado.getProductos().remove(producto.get());
            inventarioActualizado.setUltimaActualizacion(LocalDateTime.now());
            inventarioRepository.save(inventarioActualizado);

            return ResponseEntity.ok(inventarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar responsable del inventario
    @PatchMapping("/{id}/responsable")
    public ResponseEntity<Inventario> updateResponsable(@PathVariable Long id, @RequestParam Long responsableId) {
        try {
            Optional<Inventario> inventario = inventarioRepository.findById(id);
            if (inventario.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Empleado> responsable = empleadoRepository.findById(responsableId);
            if (responsable.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Inventario inventarioActualizado = inventario.get();
            inventarioActualizado.setResponsable(responsable.get());
            inventarioActualizado.setUltimaActualizacion(LocalDateTime.now());
            inventarioRepository.save(inventarioActualizado);

            return ResponseEntity.ok(inventarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE - Eliminar inventario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventario(@PathVariable Long id) {
        try {
            if (!inventarioRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            inventarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

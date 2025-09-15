package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.productos.Producto;
import co.com.api.co.com.api.domain.productos.ProductoRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@Tag(name = "Productos", description = "Gestión de productos del inventario")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoRepository productoRepository;

    // GET - Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        try {
            List<Producto> productos = productoRepository.findAll();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            return producto.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener productos por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> getProductosByCategoria(@PathVariable String categoria) {
        try {
            List<Producto> productos = productoRepository.findByCategoria(categoria);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener productos con stock bajo
    @GetMapping("/stock-bajo/{stock}")
    public ResponseEntity<List<Producto>> getProductosStockBajo(@PathVariable Integer stock) {
        try {
            List<Producto> productos = productoRepository.findByStockLessThan(stock);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Buscar productos por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String nombre) {
        try {
            List<Producto> productos = productoRepository.findByNombreContainingIgnoreCase(nombre);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear nuevo producto
    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        logger.info("Intentando crear producto: {}", producto.getNombre());
        try {
            // Validar datos de entrada
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                logger.warn("Nombre de producto vacío");
                return ResponseEntity.badRequest().build();
            }

            if (producto.getCategoria() == null || producto.getCategoria().trim().isEmpty()) {
                logger.warn("Categoría de producto vacía");
                return ResponseEntity.badRequest().build();
            }

            if (producto.getStock() == null || producto.getStock() < 0) {
                logger.warn("Stock de producto inválido: {}", producto.getStock());
                return ResponseEntity.badRequest().build();
            }

            if (producto.getPrecio() == null || producto.getPrecio().compareTo(java.math.BigDecimal.ZERO) < 0) {
                logger.warn("Precio de producto inválido: {}", producto.getPrecio());
                return ResponseEntity.badRequest().build();
            }

            // Verificar si ya existe un producto con el mismo nombre
            Optional<Producto> productoExistente = productoRepository.findByNombre(producto.getNombre().trim());
            if (productoExistente.isPresent()) {
                logger.warn("Ya existe un producto con el nombre: {}", producto.getNombre());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Limpiar y preparar datos
            producto.setNombre(producto.getNombre().trim());
            producto.setCategoria(producto.getCategoria().trim());

            Producto nuevoProducto = productoRepository.save(producto);
            logger.info("Producto creado exitosamente: {} (ID: {})", nuevoProducto.getNombre(), nuevoProducto.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (Exception e) {
            logger.error("Error al crear producto: {}", producto.getNombre(), e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT - Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        try {
            Optional<Producto> productoExistente = productoRepository.findById(id);
            if (productoExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Producto producto = productoExistente.get();
            producto.setNombre(productoActualizado.getNombre());
            producto.setFechaExpiracion(productoActualizado.getFechaExpiracion());
            producto.setFechaVencimiento(productoActualizado.getFechaVencimiento());
            producto.setCategoria(productoActualizado.getCategoria());
            producto.setStock(productoActualizado.getStock());
            producto.setPrecio(productoActualizado.getPrecio());

            Producto productoGuardado = productoRepository.save(producto);
            return ResponseEntity.ok(productoGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar stock de producto
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Producto> updateStock(@PathVariable Long id, @RequestParam Integer nuevoStock) {
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            if (producto.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Producto productoActualizado = producto.get();
            productoActualizado.setStock(nuevoStock);
            Producto productoGuardado = productoRepository.save(productoActualizado);
            return ResponseEntity.ok(productoGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE - Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        try {
            if (!productoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            productoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

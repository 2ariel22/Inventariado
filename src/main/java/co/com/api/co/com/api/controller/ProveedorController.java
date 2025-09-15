package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.proveedores.Proveedor;
import co.com.api.co.com.api.domain.proveedores.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);

    @Autowired
    private ProveedorRepository proveedorRepository;

    // GET - Obtener todos los proveedores
    @GetMapping
    public ResponseEntity<List<Proveedor>> getAllProveedores() {
        try {
            List<Proveedor> proveedores = proveedorRepository.findAll();
            return ResponseEntity.ok(proveedores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener proveedor por ID
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getProveedorById(@PathVariable Long id) {
        try {
            Optional<Proveedor> proveedor = proveedorRepository.findById(id);
            return proveedor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener proveedor por NIT
    @GetMapping("/nit/{nit}")
    public ResponseEntity<Proveedor> getProveedorByNit(@PathVariable String nit) {
        try {
            Optional<Proveedor> proveedor = proveedorRepository.findByNit(nit);
            return proveedor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener proveedor por correo
    @GetMapping("/correo/{correo}")
    public ResponseEntity<Proveedor> getProveedorByCorreo(@PathVariable String correo) {
        try {
            Optional<Proveedor> proveedor = proveedorRepository.findByCorreo(correo);
            return proveedor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear nuevo proveedor
    @PostMapping
    public ResponseEntity<Proveedor> createProveedor(@RequestBody Proveedor proveedor) {
        logger.info("Intentando crear proveedor: {}", proveedor.getNombre());
        try {
            // Validar datos de entrada
            if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
                logger.warn("Nombre de proveedor vacío");
                return ResponseEntity.badRequest().build();
            }

            if (proveedor.getUbicacion() == null || proveedor.getUbicacion().trim().isEmpty()) {
                logger.warn("Ubicación de proveedor vacía");
                return ResponseEntity.badRequest().build();
            }

            if (proveedor.getAntiguedad() == null) {
                logger.warn("Fecha de antigüedad de proveedor vacía");
                return ResponseEntity.badRequest().build();
            }

            // Verificar si ya existe un proveedor con el mismo NIT
            if (proveedor.getNit() != null && !proveedor.getNit().trim().isEmpty()) {
                Optional<Proveedor> proveedorExistente = proveedorRepository.findByNit(proveedor.getNit().trim());
                if (proveedorExistente.isPresent()) {
                    logger.warn("Ya existe un proveedor con el NIT: {}", proveedor.getNit());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }

            // Verificar si ya existe un proveedor con el mismo correo
            if (proveedor.getCorreo() != null && !proveedor.getCorreo().trim().isEmpty()) {
                Optional<Proveedor> proveedorConCorreo = proveedorRepository.findByCorreo(proveedor.getCorreo().trim());
                if (proveedorConCorreo.isPresent()) {
                    logger.warn("Ya existe un proveedor con el correo: {}", proveedor.getCorreo());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }

            // Limpiar y preparar datos
            proveedor.setNombre(proveedor.getNombre().trim());
            proveedor.setUbicacion(proveedor.getUbicacion().trim());
            if (proveedor.getNit() != null) {
                proveedor.setNit(proveedor.getNit().trim());
            }
            if (proveedor.getTelefono() != null) {
                proveedor.setTelefono(proveedor.getTelefono().trim());
            }
            if (proveedor.getCorreo() != null) {
                proveedor.setCorreo(proveedor.getCorreo().trim());
            }

            Proveedor nuevoProveedor = proveedorRepository.save(proveedor);
            logger.info("Proveedor creado exitosamente: {} (ID: {})", nuevoProveedor.getNombre(), nuevoProveedor.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
        } catch (Exception e) {
            logger.error("Error al crear proveedor: {}", proveedor.getNombre(), e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT - Actualizar proveedor
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> updateProveedor(@PathVariable Long id, @RequestBody Proveedor proveedorActualizado) {
        try {
            Optional<Proveedor> proveedorExistente = proveedorRepository.findById(id);
            if (proveedorExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Proveedor proveedor = proveedorExistente.get();
            proveedor.setNombre(proveedorActualizado.getNombre());
            proveedor.setUbicacion(proveedorActualizado.getUbicacion());
            proveedor.setAntiguedad(proveedorActualizado.getAntiguedad());
            proveedor.setUltimaEntrega(proveedorActualizado.getUltimaEntrega());
            proveedor.setNit(proveedorActualizado.getNit());
            proveedor.setTelefono(proveedorActualizado.getTelefono());
            proveedor.setCorreo(proveedorActualizado.getCorreo());

            Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
            return ResponseEntity.ok(proveedorGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar última entrega
    @PatchMapping("/{id}/ultima-entrega")
    public ResponseEntity<Proveedor> updateUltimaEntrega(@PathVariable Long id, 
                                                        @RequestParam String fechaEntrega) {
        try {
            Optional<Proveedor> proveedor = proveedorRepository.findById(id);
            if (proveedor.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Proveedor proveedorActualizado = proveedor.get();
            proveedorActualizado.setUltimaEntrega(java.time.LocalDate.parse(fechaEntrega));

            Proveedor proveedorGuardado = proveedorRepository.save(proveedorActualizado);
            return ResponseEntity.ok(proveedorGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar datos de contacto
    @PatchMapping("/{id}/contacto")
    public ResponseEntity<Proveedor> updateContacto(@PathVariable Long id, 
                                                   @RequestParam(required = false) String telefono,
                                                   @RequestParam(required = false) String correo) {
        try {
            Optional<Proveedor> proveedor = proveedorRepository.findById(id);
            if (proveedor.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Proveedor proveedorActualizado = proveedor.get();
            if (telefono != null) {
                proveedorActualizado.setTelefono(telefono);
            }
            if (correo != null) {
                proveedorActualizado.setCorreo(correo);
            }

            Proveedor proveedorGuardado = proveedorRepository.save(proveedorActualizado);
            return ResponseEntity.ok(proveedorGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE - Eliminar proveedor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProveedor(@PathVariable Long id) {
        try {
            if (!proveedorRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            proveedorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

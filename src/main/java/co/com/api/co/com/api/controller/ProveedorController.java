package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.proveedores.Proveedor;
import co.com.api.co.com.api.domain.proveedores.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorController {

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
        try {
            // Verificar si ya existe un proveedor con el mismo NIT
            if (proveedor.getNit() != null) {
                Optional<Proveedor> proveedorExistente = proveedorRepository.findByNit(proveedor.getNit());
                if (proveedorExistente.isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }

            // Verificar si ya existe un proveedor con el mismo correo
            if (proveedor.getCorreo() != null) {
                Optional<Proveedor> proveedorConCorreo = proveedorRepository.findByCorreo(proveedor.getCorreo());
                if (proveedorConCorreo.isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }

            Proveedor nuevoProveedor = proveedorRepository.save(proveedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
        } catch (Exception e) {
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

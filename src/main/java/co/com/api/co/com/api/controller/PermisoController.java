package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.roles.Permiso;
import co.com.api.co.com.api.domain.roles.PermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permisos")
@CrossOrigin(origins = "*")
public class PermisoController {

    @Autowired
    private PermisoRepository permisoRepository;

    // GET - Obtener todos los permisos
    @GetMapping
    public ResponseEntity<List<Permiso>> getAllPermisos() {
        try {
            List<Permiso> permisos = permisoRepository.findAll();
            return ResponseEntity.ok(permisos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener permiso por ID
    @GetMapping("/{id}")
    public ResponseEntity<Permiso> getPermisoById(@PathVariable Long id) {
        try {
            Optional<Permiso> permiso = permisoRepository.findById(id);
            return permiso.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener permiso por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Permiso> getPermisoByNombre(@PathVariable String nombre) {
        try {
            Optional<Permiso> permiso = permisoRepository.findByNombre(nombre);
            return permiso.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener permisos por recurso
    @GetMapping("/recurso/{recurso}")
    public ResponseEntity<List<Permiso>> getPermisosByRecurso(@PathVariable String recurso) {
        try {
            List<Permiso> permisos = permisoRepository.findAll().stream()
                    .filter(p -> p.getRecurso() != null && p.getRecurso().equalsIgnoreCase(recurso))
                    .toList();
            return ResponseEntity.ok(permisos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener permisos por acción
    @GetMapping("/accion/{accion}")
    public ResponseEntity<List<Permiso>> getPermisosByAccion(@PathVariable String accion) {
        try {
            Permiso.Accion accionEnum = Permiso.Accion.valueOf(accion.toUpperCase());
            List<Permiso> permisos = permisoRepository.findAll().stream()
                    .filter(p -> p.getAccion() == accionEnum)
                    .toList();
            return ResponseEntity.ok(permisos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // POST - Crear nuevo permiso
    @PostMapping
    public ResponseEntity<Permiso> createPermiso(@RequestBody Permiso permiso) {
        try {
            // Verificar si ya existe un permiso con el mismo nombre
            Optional<Permiso> permisoExistente = permisoRepository.findByNombre(permiso.getNombre());
            if (permisoExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Permiso nuevoPermiso = permisoRepository.save(permiso);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPermiso);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT - Actualizar permiso
    @PutMapping("/{id}")
    public ResponseEntity<Permiso> updatePermiso(@PathVariable Long id, @RequestBody Permiso permisoActualizado) {
        try {
            Optional<Permiso> permisoExistente = permisoRepository.findById(id);
            if (permisoExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Permiso permiso = permisoExistente.get();
            permiso.setNombre(permisoActualizado.getNombre());
            permiso.setDescripcion(permisoActualizado.getDescripcion());
            permiso.setRecurso(permisoActualizado.getRecurso());
            permiso.setAccion(permisoActualizado.getAccion());

            Permiso permisoGuardado = permisoRepository.save(permiso);
            return ResponseEntity.ok(permisoGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar descripción del permiso
    @PatchMapping("/{id}/descripcion")
    public ResponseEntity<Permiso> updateDescripcion(@PathVariable Long id, @RequestParam String descripcion) {
        try {
            Optional<Permiso> permiso = permisoRepository.findById(id);
            if (permiso.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Permiso permisoActualizado = permiso.get();
            permisoActualizado.setDescripcion(descripcion);
            permisoRepository.save(permisoActualizado);

            return ResponseEntity.ok(permisoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar recurso del permiso
    @PatchMapping("/{id}/recurso")
    public ResponseEntity<Permiso> updateRecurso(@PathVariable Long id, @RequestParam String recurso) {
        try {
            Optional<Permiso> permiso = permisoRepository.findById(id);
            if (permiso.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Permiso permisoActualizado = permiso.get();
            permisoActualizado.setRecurso(recurso);
            permisoRepository.save(permisoActualizado);

            return ResponseEntity.ok(permisoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar acción del permiso
    @PatchMapping("/{id}/accion")
    public ResponseEntity<Permiso> updateAccion(@PathVariable Long id, @RequestParam String accion) {
        try {
            Optional<Permiso> permiso = permisoRepository.findById(id);
            if (permiso.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Permiso.Accion accionEnum = Permiso.Accion.valueOf(accion.toUpperCase());
            Permiso permisoActualizado = permiso.get();
            permisoActualizado.setAccion(accionEnum);
            permisoRepository.save(permisoActualizado);

            return ResponseEntity.ok(permisoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // DELETE - Eliminar permiso
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermiso(@PathVariable Long id) {
        try {
            if (!permisoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            permisoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

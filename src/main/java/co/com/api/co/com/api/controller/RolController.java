package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.roles.Rol;
import co.com.api.co.com.api.domain.roles.RolRepository;
import co.com.api.co.com.api.domain.roles.Permiso;
import co.com.api.co.com.api.domain.roles.PermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    // GET - Obtener todos los roles
    @GetMapping
    public ResponseEntity<List<Rol>> getAllRoles() {
        try {
            List<Rol> roles = rolRepository.findAll();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener rol por ID
    @GetMapping("/{id}")
    public ResponseEntity<Rol> getRolById(@PathVariable Long id) {
        try {
            Optional<Rol> rol = rolRepository.findById(id);
            return rol.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener rol por nombre
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Rol> getRolByNombre(@PathVariable String nombre) {
        try {
            Optional<Rol> rol = rolRepository.findByNombre(nombre);
            return rol.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear nuevo rol
    @PostMapping
    public ResponseEntity<Rol> createRol(@RequestBody Rol rol) {
        try {
            // Verificar si ya existe un rol con el mismo nombre
            Optional<Rol> rolExistente = rolRepository.findByNombre(rol.getNombre());
            if (rolExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Rol nuevoRol = rolRepository.save(rol);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT - Actualizar rol
    @PutMapping("/{id}")
    public ResponseEntity<Rol> updateRol(@PathVariable Long id, @RequestBody Rol rolActualizado) {
        try {
            Optional<Rol> rolExistente = rolRepository.findById(id);
            if (rolExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Rol rol = rolExistente.get();
            rol.setNombre(rolActualizado.getNombre());
            rol.setDescripcion(rolActualizado.getDescripcion());
            rol.setPermisos(rolActualizado.getPermisos());

            Rol rolGuardado = rolRepository.save(rol);
            return ResponseEntity.ok(rolGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Agregar permiso al rol
    @PatchMapping("/{id}/agregar-permiso")
    public ResponseEntity<Rol> agregarPermiso(@PathVariable Long id, @RequestParam Long permisoId) {
        try {
            Optional<Rol> rol = rolRepository.findById(id);
            if (rol.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Permiso> permiso = permisoRepository.findById(permisoId);
            if (permiso.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Rol rolActualizado = rol.get();
            if (!rolActualizado.getPermisos().contains(permiso.get())) {
                rolActualizado.getPermisos().add(permiso.get());
                rolRepository.save(rolActualizado);
            }

            return ResponseEntity.ok(rolActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Remover permiso del rol
    @PatchMapping("/{id}/remover-permiso")
    public ResponseEntity<Rol> removerPermiso(@PathVariable Long id, @RequestParam Long permisoId) {
        try {
            Optional<Rol> rol = rolRepository.findById(id);
            if (rol.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Permiso> permiso = permisoRepository.findById(permisoId);
            if (permiso.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Rol rolActualizado = rol.get();
            rolActualizado.getPermisos().remove(permiso.get());
            rolRepository.save(rolActualizado);

            return ResponseEntity.ok(rolActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar descripción del rol
    @PatchMapping("/{id}/descripcion")
    public ResponseEntity<Rol> updateDescripcion(@PathVariable Long id, @RequestParam String descripcion) {
        try {
            Optional<Rol> rol = rolRepository.findById(id);
            if (rol.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Rol rolActualizado = rol.get();
            rolActualizado.setDescripcion(descripcion);
            rolRepository.save(rolActualizado);

            return ResponseEntity.ok(rolActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE - Eliminar rol
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        try {
            if (!rolRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            rolRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

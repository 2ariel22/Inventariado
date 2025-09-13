package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.usuarios.Usuario;
import co.com.api.co.com.api.domain.usuarios.UsuarioReposotiry;
import co.com.api.co.com.api.domain.roles.Rol;
import co.com.api.co.com.api.domain.roles.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioReposotiry usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // GET - Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            return usuario.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener usuario por nombre de usuario
    @GetMapping("/user/{user}")
    public ResponseEntity<Usuario> getUsuarioByUser(@PathVariable String user) {
        try {
            // Buscar por nombre de usuario usando findAll y filtrar
            List<Usuario> usuarios = usuarioRepository.findAll();
            Optional<Usuario> usuario = usuarios.stream()
                    .filter(u -> u.getUser().equals(user))
                    .findFirst();
            return usuario.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener usuario por email
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> getUsuarioByEmail(@PathVariable String email) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
            return usuario.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener usuarios activos
    @GetMapping("/activos")
    public ResponseEntity<List<Usuario>> getUsuariosActivos() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll().stream()
                    .filter(Usuario::getActivo)
                    .toList();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear nuevo usuario
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        try {
            // Verificar si ya existe un usuario con el mismo nombre de usuario
            List<Usuario> usuarios = usuarioRepository.findAll();
            boolean usuarioExiste = usuarios.stream()
                    .anyMatch(u -> u.getUser().equals(usuario.getUser()));
            if (usuarioExiste) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Verificar si ya existe un usuario con el mismo email
            Optional<Usuario> usuarioConEmail = usuarioRepository.findByEmail(usuario.getEmail());
            if (usuarioConEmail.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Usuario nuevoUsuario = usuarioRepository.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT - Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        try {
            Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
            if (usuarioExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioExistente.get();
            usuario.setUser(usuarioActualizado.getUser());
            usuario.setPassword(usuarioActualizado.getPassword());
            usuario.setEmail(usuarioActualizado.getEmail());
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setApellido(usuarioActualizado.getApellido());
            usuario.setRoles(usuarioActualizado.getRoles());
            usuario.setActivo(usuarioActualizado.getActivo());

            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuarioGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar datos personales del usuario
    @PatchMapping("/{id}/datos-personales")
    public ResponseEntity<Usuario> updateDatosPersonales(@PathVariable Long id, 
                                                        @RequestParam(required = false) String nombre,
                                                        @RequestParam(required = false) String apellido,
                                                        @RequestParam(required = false) String email) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuarioActualizado = usuario.get();
            if (nombre != null) {
                usuarioActualizado.setNombre(nombre);
            }
            if (apellido != null) {
                usuarioActualizado.setApellido(apellido);
            }
            if (email != null) {
                usuarioActualizado.setEmail(email);
            }

            Usuario usuarioGuardado = usuarioRepository.save(usuarioActualizado);
            return ResponseEntity.ok(usuarioGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Cambiar contraseña
    @PatchMapping("/{id}/password")
    public ResponseEntity<Usuario> changePassword(@PathVariable Long id, @RequestParam String nuevaPassword) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuarioActualizado = usuario.get();
            usuarioActualizado.setPassword(nuevaPassword);
            usuarioRepository.save(usuarioActualizado);

            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Activar/Desactivar usuario
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Usuario> toggleEstado(@PathVariable Long id) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Usuario usuarioActualizado = usuario.get();
            usuarioActualizado.setActivo(!usuarioActualizado.getActivo());
            usuarioRepository.save(usuarioActualizado);

            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Agregar rol al usuario
    @PatchMapping("/{id}/agregar-rol")
    public ResponseEntity<Usuario> agregarRol(@PathVariable Long id, @RequestParam Long rolId) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Rol> rol = rolRepository.findById(rolId);
            if (rol.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Usuario usuarioActualizado = usuario.get();
            if (!usuarioActualizado.getRoles().contains(rol.get())) {
                usuarioActualizado.getRoles().add(rol.get());
                usuarioRepository.save(usuarioActualizado);
            }

            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Remover rol del usuario
    @PatchMapping("/{id}/remover-rol")
    public ResponseEntity<Usuario> removerRol(@PathVariable Long id, @RequestParam Long rolId) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Rol> rol = rolRepository.findById(rolId);
            if (rol.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Usuario usuarioActualizado = usuario.get();
            usuarioActualizado.getRoles().remove(rol.get());
            usuarioRepository.save(usuarioActualizado);

            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE - Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        try {
            if (!usuarioRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

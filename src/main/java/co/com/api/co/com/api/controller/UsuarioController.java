package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.usuarios.Usuario;
import co.com.api.co.com.api.domain.usuarios.UsuarioReposotiry;
import co.com.api.co.com.api.domain.roles.Rol;
import co.com.api.co.com.api.domain.roles.RolRepository;
import co.com.api.co.com.api.dto.UpdateUsuarioRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioReposotiry usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody UpdateUsuarioRequest request) {
        logger.info("Intentando actualizar usuario con ID: {}", id);
        try {
            // Validar ID
            if (id == null || id <= 0) {
                logger.warn("ID de usuario inválido: {}", id);
                return ResponseEntity.badRequest().build();
            }

            // Validar datos de entrada
            if (request == null) {
                logger.warn("Datos de usuario nulos para ID: {}", id);
                return ResponseEntity.badRequest().build();
            }

            // Buscar usuario existente
            Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
            if (usuarioExistente.isEmpty()) {
                logger.warn("Usuario no encontrado con ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioExistente.get();
            logger.info("Usuario encontrado: {} (ID: {})", usuario.getUser(), usuario.getId());

            // Validar y actualizar campos
            if (request.user() != null && !request.user().trim().isEmpty()) {
                // Verificar si el nuevo nombre de usuario ya existe en otro usuario
                Optional<Usuario> usuarioConMismoUser = usuarioRepository.findByUser(request.user().trim());
                if (usuarioConMismoUser.isPresent() && !usuarioConMismoUser.get().getId().equals(id)) {
                    logger.warn("Ya existe un usuario con el nombre: {}", request.user());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                usuario.setUser(request.user().trim());
                logger.info("Nombre de usuario actualizado a: {}", usuario.getUser());
            }

            // Actualizar contraseña si se proporciona
            if (request.password() != null && !request.password().trim().isEmpty()) {
                // Encriptar la nueva contraseña
                String passwordEncriptada = passwordEncoder.encode(request.password().trim());
                usuario.setPassword(passwordEncriptada);
                logger.info("Contraseña actualizada para usuario: {}", usuario.getUser());
            }

            // Actualizar email si se proporciona
            if (request.email() != null && !request.email().trim().isEmpty()) {
                // Verificar si el nuevo email ya existe en otro usuario
                Optional<Usuario> usuarioConMismoEmail = usuarioRepository.findByEmail(request.email().trim());
                if (usuarioConMismoEmail.isPresent() && !usuarioConMismoEmail.get().getId().equals(id)) {
                    logger.warn("Ya existe un usuario con el email: {}", request.email());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                usuario.setEmail(request.email().trim());
                logger.info("Email actualizado a: {}", usuario.getEmail());
            }

            // Actualizar datos personales
            if (request.nombre() != null) {
                usuario.setNombre(request.nombre().trim());
            }
            if (request.apellido() != null) {
                usuario.setApellido(request.apellido().trim());
            }

            // Actualizar estado activo
            if (request.activo() != null) {
                usuario.setActivo(request.activo());
                logger.info("Estado activo actualizado a: {}", usuario.getActivo());
            }

            // Actualizar roles si se proporcionan
            if (request.rolIds() != null && !request.rolIds().isEmpty()) {
                // Validar que todos los roles existan
                List<Rol> rolesValidos = rolRepository.findAllById(request.rolIds());
                
                if (rolesValidos.size() != request.rolIds().size()) {
                    logger.warn("Algunos roles no existen. IDs proporcionados: {}, roles encontrados: {}", 
                               request.rolIds(), rolesValidos.stream().map(Rol::getId).toList());
                    return ResponseEntity.badRequest().build();
                }
                
                usuario.setRoles(rolesValidos);
                logger.info("Roles actualizados para usuario: {}", usuario.getUser());
            }

            // Guardar usuario actualizado
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            logger.info("Usuario actualizado exitosamente: {} (ID: {})", usuarioGuardado.getUser(), usuarioGuardado.getId());
            
            return ResponseEntity.ok(usuarioGuardado);
        } catch (Exception e) {
            logger.error("Error al actualizar usuario con ID: {}", id, e);
            e.printStackTrace();
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
        logger.info("Intentando cambiar contraseña para usuario ID: {}", id);
        try {
            // Validar ID
            if (id == null || id <= 0) {
                logger.warn("ID de usuario inválido: {}", id);
                return ResponseEntity.badRequest().build();
            }

            // Validar contraseña
            if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
                logger.warn("Contraseña vacía para usuario ID: {}", id);
                return ResponseEntity.badRequest().build();
            }

            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isEmpty()) {
                logger.warn("Usuario no encontrado con ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            Usuario usuarioActualizado = usuario.get();
            // Encriptar la nueva contraseña
            String passwordEncriptada = passwordEncoder.encode(nuevaPassword.trim());
            usuarioActualizado.setPassword(passwordEncriptada);
            usuarioRepository.save(usuarioActualizado);

            logger.info("Contraseña actualizada exitosamente para usuario: {}", usuarioActualizado.getUser());
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            logger.error("Error al cambiar contraseña para usuario ID: {}", id, e);
            e.printStackTrace();
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

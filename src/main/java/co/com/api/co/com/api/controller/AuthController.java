package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.dto.AuthResponse;
import co.com.api.co.com.api.dto.LoginRequest;
import co.com.api.co.com.api.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import co.com.api.co.com.api.domain.usuarios.Usuario;
import co.com.api.co.com.api.domain.usuarios.UsuarioReposotiry;
import co.com.api.co.com.api.domain.roles.Rol;
import co.com.api.co.com.api.domain.roles.RolRepository;
import co.com.api.co.com.api.infra.security.DatosJWT;
import co.com.api.co.com.api.infra.security.TokenGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de autenticación")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioReposotiry usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TokenGenerate tokenGenerate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // POST - Registro de usuario
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema con roles opcionales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "Usuario o email ya existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        logger.info("Intentando registrar usuario: {}", request.user());
        try {
            // Validar datos de entrada
            if (request.user() == null || request.user().trim().isEmpty()) {
                logger.warn("Intento de registro con usuario vacío");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (request.password() == null || request.password().trim().isEmpty()) {
                logger.warn("Intento de registro con contraseña vacía para usuario: {}", request.user());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (request.email() == null || request.email().trim().isEmpty()) {
                logger.warn("Intento de registro con email vacío para usuario: {}", request.user());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Verificar si ya existe un usuario con el mismo nombre de usuario
            Optional<Usuario> usuarioExistente = usuarioRepository.findByUser(request.user());
            if (usuarioExistente.isPresent()) {
                logger.warn("Usuario ya existe: {}", request.user());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Verificar si ya existe un usuario con el mismo email
            Optional<Usuario> usuarioConEmail = usuarioRepository.findByEmail(request.email());
            if (usuarioConEmail.isPresent()) {
                logger.warn("Email ya existe: {}", request.email());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUser(request.user().trim());
            nuevoUsuario.setPassword(passwordEncoder.encode(request.password()));
            nuevoUsuario.setEmail(request.email().trim());
            nuevoUsuario.setNombre(request.nombre() != null ? request.nombre().trim() : null);
            nuevoUsuario.setApellido(request.apellido() != null ? request.apellido().trim() : null);
            nuevoUsuario.setActivo(true);

            // Asignar roles si se proporcionan, sino asignar rol VENDEDOR por defecto
            if (request.rolIds() != null && !request.rolIds().isEmpty()) {
                List<Rol> roles = rolRepository.findAllById(request.rolIds());
                nuevoUsuario.setRoles(roles);
            } else {
                // Asignar rol VENDEDOR por defecto (ID = 2)
                Optional<Rol> rolVendedor = rolRepository.findById(2L);
                if (rolVendedor.isPresent()) {
                    nuevoUsuario.setRoles(List.of(rolVendedor.get()));
                }
            }

            // Guardar usuario
            Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            // Generar token
            String token = tokenGenerate.generarToken(usuarioGuardado);

            // Crear respuesta
            AuthResponse response = new AuthResponse(
                    token,
                    usuarioGuardado.getUser(),
                    usuarioGuardado.getEmail(),
                    usuarioGuardado.getNombre(),
                    usuarioGuardado.getApellido(),
                    usuarioGuardado.getRoles() != null ? 
                        usuarioGuardado.getRoles().stream().map(Rol::getNombre).toList() : 
                        List.of(),
                    usuarioGuardado.getActivo()
            );

            logger.info("Usuario registrado exitosamente: {}", usuarioGuardado.getUser());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error al registrar usuario: {}", request.user(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Login de usuario
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            // Buscar usuario en la base de datos
            Optional<Usuario> usuario = usuarioRepository.findByUser(request.user());

            if (usuario.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Usuario usuarioEncontrado = usuario.get();

            // Verificar si el usuario está activo
            if (!usuarioEncontrado.getActivo()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Verificar contraseña
            if (!passwordEncoder.matches(request.password(), usuarioEncontrado.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Generar token
            String token = tokenGenerate.generarToken(usuarioEncontrado);

            // Crear respuesta
            AuthResponse response = new AuthResponse(
                    token,
                    usuarioEncontrado.getUser(),
                    usuarioEncontrado.getEmail(),
                    usuarioEncontrado.getNombre(),
                    usuarioEncontrado.getApellido(),
                    usuarioEncontrado.getRoles() != null ? 
                        usuarioEncontrado.getRoles().stream().map(Rol::getNombre).toList() : 
                        List.of(),
                    usuarioEncontrado.getActivo()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Para debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Validar token
    @PostMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestParam String token) {
        try {
            String username = tokenGenerate.getSujet(token);
            
            // Buscar usuario
            Optional<Usuario> usuario = usuarioRepository.findByUser(username);

            if (usuario.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Usuario usuarioEncontrado = usuario.get();

            // Verificar si el usuario está activo
            if (!usuarioEncontrado.getActivo()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Crear respuesta
            AuthResponse response = new AuthResponse(
                    token,
                    usuarioEncontrado.getUser(),
                    usuarioEncontrado.getEmail(),
                    usuarioEncontrado.getNombre(),
                    usuarioEncontrado.getApellido(),
                    usuarioEncontrado.getRoles() != null ? 
                        usuarioEncontrado.getRoles().stream().map(Rol::getNombre).toList() : 
                        List.of(),
                    usuarioEncontrado.getActivo()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Para debugging
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // POST - Logout (opcional, ya que JWT es stateless)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // En un sistema JWT stateless, el logout se maneja en el cliente
        // eliminando el token del almacenamiento local
        return ResponseEntity.ok().build();
    }

}

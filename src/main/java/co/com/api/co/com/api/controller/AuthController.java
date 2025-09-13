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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de autenticación")
public class AuthController {

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
        try {
            // Verificar si ya existe un usuario con el mismo nombre de usuario
            List<Usuario> usuarios = usuarioRepository.findAll();
            boolean usuarioExiste = usuarios.stream()
                    .anyMatch(u -> u.getUser().equals(request.user()));
            if (usuarioExiste) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Verificar si ya existe un usuario con el mismo email
            Optional<Usuario> usuarioConEmail = usuarioRepository.findByEmail(request.email());
            if (usuarioConEmail.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUser(request.user());
            nuevoUsuario.setPassword(passwordEncoder.encode(request.password()));
            nuevoUsuario.setEmail(request.email());
            nuevoUsuario.setNombre(request.nombre());
            nuevoUsuario.setApellido(request.apellido());
            nuevoUsuario.setActivo(true);

            // Asignar roles si se proporcionan
            if (request.rolIds() != null && !request.rolIds().isEmpty()) {
                List<Rol> roles = rolRepository.findAllById(request.rolIds());
                nuevoUsuario.setRoles(roles);
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
                    usuarioGuardado.getRoles().stream().map(Rol::getNombre).toList(),
                    usuarioGuardado.getActivo()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
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
            System.out.println("Intentando login para usuario: " + request.user());
            
            // Buscar usuario en la base de datos primero
            List<Usuario> usuarios = usuarioRepository.findAll();
            Optional<Usuario> usuario = usuarios.stream()
                    .filter(u -> u.getUser().equals(request.user()))
                    .findFirst();

            if (usuario.isEmpty()) {
                System.out.println("Usuario no encontrado: " + request.user());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Usuario usuarioEncontrado = usuario.get();
            System.out.println("Usuario encontrado: " + usuarioEncontrado.getUser() + ", Activo: " + usuarioEncontrado.getActivo());

            // Verificar si el usuario está activo
            if (!usuarioEncontrado.getActivo()) {
                System.out.println("Usuario inactivo: " + request.user());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Verificar contraseña manualmente
            if (!passwordEncoder.matches(request.password(), usuarioEncontrado.getPassword())) {
                System.out.println("Contraseña incorrecta para usuario: " + request.user());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            System.out.println("Autenticación exitosa para usuario: " + request.user());

            // Generar token
            String token = tokenGenerate.generarToken(usuarioEncontrado);
            System.out.println("Token generado exitosamente");

            // Crear respuesta
            AuthResponse response = new AuthResponse(
                    token,
                    usuarioEncontrado.getUser(),
                    usuarioEncontrado.getEmail(),
                    usuarioEncontrado.getNombre(),
                    usuarioEncontrado.getApellido(),
                    usuarioEncontrado.getRoles().stream().map(Rol::getNombre).toList(),
                    usuarioEncontrado.getActivo()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Validar token
    @PostMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestParam String token) {
        try {
            String username = tokenGenerate.getSujet(token);
            
            // Buscar usuario
            List<Usuario> usuarios = usuarioRepository.findAll();
            Optional<Usuario> usuario = usuarios.stream()
                    .filter(u -> u.getUser().equals(username))
                    .findFirst();

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
                    usuarioEncontrado.getRoles().stream().map(Rol::getNombre).toList(),
                    usuarioEncontrado.getActivo()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
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

    // GET - Diagnóstico del sistema de autenticación
    @GetMapping("/debug")
    public ResponseEntity<String> debugAuth() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            StringBuilder debug = new StringBuilder();
            debug.append("=== DIAGNÓSTICO DE AUTENTICACIÓN ===\n");
            debug.append("Total usuarios: ").append(usuarios.size()).append("\n");
            
            for (Usuario usuario : usuarios) {
                debug.append("Usuario: ").append(usuario.getUser())
                     .append(", Email: ").append(usuario.getEmail())
                     .append(", Activo: ").append(usuario.getActivo())
                     .append(", Roles: ").append(usuario.getRoles().size())
                     .append("\n");
            }
            
            debug.append("\n=== CONFIGURACIÓN ===\n");
            debug.append("PasswordEncoder: ").append(passwordEncoder != null ? "OK" : "NULL").append("\n");
            debug.append("TokenGenerate: ").append(tokenGenerate != null ? "OK" : "NULL").append("\n");
            debug.append("UsuarioRepository: ").append(usuarioRepository != null ? "OK" : "NULL").append("\n");
            
            return ResponseEntity.ok(debug.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en diagnóstico: " + e.getMessage());
        }
    }
}

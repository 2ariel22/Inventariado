package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.roles.Permiso;
import co.com.api.co.com.api.domain.roles.PermisoRepository;
import co.com.api.co.com.api.domain.roles.Rol;
import co.com.api.co.com.api.domain.roles.RolRepository;
import co.com.api.co.com.api.domain.usuarios.Usuario;
import co.com.api.co.com.api.domain.usuarios.UsuarioReposotiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/init")
@CrossOrigin(origins = "*")
public class InitController {

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioReposotiry usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // POST - Inicializar permisos básicos
    @PostMapping("/permisos")
    public ResponseEntity<String> initPermisos() {
        try {
            // Verificar si ya existen permisos
            if (!permisoRepository.findAll().isEmpty()) {
                return ResponseEntity.ok("Los permisos ya han sido inicializados anteriormente");
            }
            
            // Crear permisos básicos
            List<Permiso> permisos = List.of(
                new Permiso("PRODUCTOS_CREAR", "Crear productos", "PRODUCTOS", Permiso.Accion.CREAR),
                new Permiso("PRODUCTOS_LEER", "Leer productos", "PRODUCTOS", Permiso.Accion.LEER),
                new Permiso("PRODUCTOS_ACTUALIZAR", "Actualizar productos", "PRODUCTOS", Permiso.Accion.ACTUALIZAR),
                new Permiso("PRODUCTOS_ELIMINAR", "Eliminar productos", "PRODUCTOS", Permiso.Accion.ELIMINAR),
                
                new Permiso("VENTAS_CREAR", "Crear ventas", "VENTAS", Permiso.Accion.CREAR),
                new Permiso("VENTAS_LEER", "Leer ventas", "VENTAS", Permiso.Accion.LEER),
                new Permiso("VENTAS_ACTUALIZAR", "Actualizar ventas", "VENTAS", Permiso.Accion.ACTUALIZAR),
                new Permiso("VENTAS_ELIMINAR", "Eliminar ventas", "VENTAS", Permiso.Accion.ELIMINAR),
                
                new Permiso("INVENTARIO_CREAR", "Crear inventario", "INVENTARIO", Permiso.Accion.CREAR),
                new Permiso("INVENTARIO_LEER", "Leer inventario", "INVENTARIO", Permiso.Accion.LEER),
                new Permiso("INVENTARIO_ACTUALIZAR", "Actualizar inventario", "INVENTARIO", Permiso.Accion.ACTUALIZAR),
                new Permiso("INVENTARIO_ELIMINAR", "Eliminar inventario", "INVENTARIO", Permiso.Accion.ELIMINAR),
                
                new Permiso("USUARIOS_CREAR", "Crear usuarios", "USUARIOS", Permiso.Accion.CREAR),
                new Permiso("USUARIOS_LEER", "Leer usuarios", "USUARIOS", Permiso.Accion.LEER),
                new Permiso("USUARIOS_ACTUALIZAR", "Actualizar usuarios", "USUARIOS", Permiso.Accion.ACTUALIZAR),
                new Permiso("USUARIOS_ELIMINAR", "Eliminar usuarios", "USUARIOS", Permiso.Accion.ELIMINAR),
                
                new Permiso("CONTABILIDAD_CREAR", "Crear registros contables", "CONTABILIDAD", Permiso.Accion.CREAR),
                new Permiso("CONTABILIDAD_LEER", "Leer registros contables", "CONTABILIDAD", Permiso.Accion.LEER),
                new Permiso("CONTABILIDAD_ACTUALIZAR", "Actualizar registros contables", "CONTABILIDAD", Permiso.Accion.ACTUALIZAR),
                new Permiso("CONTABILIDAD_ELIMINAR", "Eliminar registros contables", "CONTABILIDAD", Permiso.Accion.ELIMINAR),
                
                new Permiso("ADMINISTRAR", "Administrar sistema", "SISTEMA", Permiso.Accion.ADMINISTRAR)
            );

            permisoRepository.saveAll(permisos);
            return ResponseEntity.ok("Permisos inicializados correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al inicializar permisos: " + e.getMessage());
        }
    }

    // POST - Inicializar roles básicos
    @PostMapping("/roles")
    public ResponseEntity<String> initRoles() {
        try {
            // Verificar si ya existen roles
            if (!rolRepository.findAll().isEmpty()) {
                return ResponseEntity.ok("Los roles ya han sido inicializados anteriormente");
            }
            
            // Obtener todos los permisos
            List<Permiso> todosLosPermisos = permisoRepository.findAll();
            
            // Crear roles
            Rol adminRol = new Rol("ADMIN", "Administrador del sistema");
            adminRol.setPermisos(todosLosPermisos);
            
            Rol vendedorRol = new Rol("VENDEDOR", "Vendedor");
            vendedorRol.setPermisos(todosLosPermisos.stream()
                .filter(p -> p.getRecurso().equals("PRODUCTOS") || 
                           p.getRecurso().equals("VENTAS") || 
                           p.getRecurso().equals("INVENTARIO"))
                .toList());
            
            Rol contadorRol = new Rol("CONTADOR", "Contador");
            contadorRol.setPermisos(todosLosPermisos.stream()
                .filter(p -> p.getRecurso().equals("CONTABILIDAD") || 
                           p.getRecurso().equals("VENTAS") ||
                           p.getRecurso().equals("PRODUCTOS"))
                .toList());

            rolRepository.saveAll(List.of(adminRol, vendedorRol, contadorRol));
            return ResponseEntity.ok("Roles inicializados correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al inicializar roles: " + e.getMessage());
        }
    }

    // POST - Crear usuario administrador por defecto
    @PostMapping("/admin")
    public ResponseEntity<String> createAdminUser() {
        try {
            // Verificar si ya existe un admin
            List<Usuario> usuarios = usuarioRepository.findAll();
            boolean adminExiste = usuarios.stream()
                .anyMatch(u -> u.getUser().equals("admin"));
            
            if (adminExiste) {
                return ResponseEntity.ok("Usuario administrador ya existe. Usuario: admin, Contraseña: admin123");
            }

            // Obtener rol de administrador
            Rol adminRol = rolRepository.findByNombre("ADMIN").orElse(null);
            if (adminRol == null) {
                return ResponseEntity.badRequest().body("Rol ADMIN no encontrado. Ejecute primero /api/init/roles");
            }

            // Crear usuario administrador
            Usuario admin = new Usuario();
            admin.setUser("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@inventariado.com");
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setActivo(true);
            admin.setRoles(List.of(adminRol));

            usuarioRepository.save(admin);
            return ResponseEntity.ok("Usuario administrador creado correctamente. Usuario: admin, Contraseña: admin123");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear usuario administrador: " + e.getMessage());
        }
    }

    // POST - Resetear contraseña del admin
    @PostMapping("/reset-admin")
    public ResponseEntity<String> resetAdminPassword() {
        try {
            // Buscar usuario admin
            List<Usuario> usuarios = usuarioRepository.findAll();
            Optional<Usuario> adminOpt = usuarios.stream()
                .filter(u -> u.getUser().equals("admin"))
                .findFirst();
            
            if (adminOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario admin no encontrado. Ejecute primero /api/init/admin");
            }

            // Resetear contraseña
            Usuario admin = adminOpt.get();
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActivo(true);
            usuarioRepository.save(admin);
            
            return ResponseEntity.ok("Contraseña del admin reseteada correctamente. Usuario: admin, Contraseña: admin123");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al resetear contraseña: " + e.getMessage());
        }
    }

    // GET - Verificar estado del sistema
    @GetMapping("/status")
    public ResponseEntity<String> getSystemStatus() {
        try {
            long permisosCount = permisoRepository.count();
            long rolesCount = rolRepository.count();
            long usuariosCount = usuarioRepository.count();
            
            String status = String.format(
                "Estado del Sistema:\n" +
                "- Permisos: %d\n" +
                "- Roles: %d\n" +
                "- Usuarios: %d\n" +
                "- Sistema: %s",
                permisosCount,
                rolesCount,
                usuariosCount,
                (permisosCount > 0 && rolesCount > 0 && usuariosCount > 0) ? "INICIALIZADO" : "NO INICIALIZADO"
            );
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al verificar estado: " + e.getMessage());
        }
    }

    // POST - Inicializar todo el sistema
    @PostMapping("/all")
    public ResponseEntity<String> initAll() {
        try {
            // Verificar estado actual
            long permisosCount = permisoRepository.count();
            long rolesCount = rolRepository.count();
            long usuariosCount = usuarioRepository.count();
            
            if (permisosCount > 0 && rolesCount > 0 && usuariosCount > 0) {
                return ResponseEntity.ok("El sistema ya está inicializado. Usuario admin disponible: admin/admin123");
            }
            
            // Inicializar permisos
            initPermisos();
            
            // Inicializar roles
            initRoles();
            
            // Crear usuario administrador
            createAdminUser();
            
            return ResponseEntity.ok("Sistema inicializado correctamente. Usuario admin creado con contraseña: admin123");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al inicializar sistema: " + e.getMessage());
        }
    }
}

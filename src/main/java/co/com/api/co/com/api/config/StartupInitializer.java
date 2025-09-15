package co.com.api.co.com.api.config;

import co.com.api.co.com.api.domain.roles.Permiso;
import co.com.api.co.com.api.domain.roles.PermisoRepository;
import co.com.api.co.com.api.domain.roles.Rol;
import co.com.api.co.com.api.domain.roles.RolRepository;
import co.com.api.co.com.api.domain.usuarios.Usuario;
import co.com.api.co.com.api.domain.usuarios.UsuarioReposotiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StartupInitializer implements CommandLineRunner {

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioReposotiry usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Inicializar permisos
            initializePermisos();
            
            // Inicializar roles
            initializeRoles();
            
            // Crear usuario admin
            createAdminUser();
            
        } catch (Exception e) {
            System.err.println("Error durante la inicialización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializePermisos() {
        if (permisoRepository.count() > 0) {
            return;
        }
        
        List<Permiso> permisos = List.of(
            // Permisos de Productos
            new Permiso("PRODUCTOS_CREAR", "Crear productos", "PRODUCTOS", Permiso.Accion.CREAR),
            new Permiso("PRODUCTOS_LEER", "Leer productos", "PRODUCTOS", Permiso.Accion.LEER),
            new Permiso("PRODUCTOS_ACTUALIZAR", "Actualizar productos", "PRODUCTOS", Permiso.Accion.ACTUALIZAR),
            new Permiso("PRODUCTOS_ELIMINAR", "Eliminar productos", "PRODUCTOS", Permiso.Accion.ELIMINAR),
            
            // Permisos de Ventas
            new Permiso("VENTAS_CREAR", "Crear ventas", "VENTAS", Permiso.Accion.CREAR),
            new Permiso("VENTAS_LEER", "Leer ventas", "VENTAS", Permiso.Accion.LEER),
            new Permiso("VENTAS_ACTUALIZAR", "Actualizar ventas", "VENTAS", Permiso.Accion.ACTUALIZAR),
            new Permiso("VENTAS_ELIMINAR", "Eliminar ventas", "VENTAS", Permiso.Accion.ELIMINAR),
            
            // Permisos de Inventario
            new Permiso("INVENTARIO_CREAR", "Crear inventario", "INVENTARIO", Permiso.Accion.CREAR),
            new Permiso("INVENTARIO_LEER", "Leer inventario", "INVENTARIO", Permiso.Accion.LEER),
            new Permiso("INVENTARIO_ACTUALIZAR", "Actualizar inventario", "INVENTARIO", Permiso.Accion.ACTUALIZAR),
            new Permiso("INVENTARIO_ELIMINAR", "Eliminar inventario", "INVENTARIO", Permiso.Accion.ELIMINAR),
            
            // Permisos de Usuarios
            new Permiso("USUARIOS_CREAR", "Crear usuarios", "USUARIOS", Permiso.Accion.CREAR),
            new Permiso("USUARIOS_LEER", "Leer usuarios", "USUARIOS", Permiso.Accion.LEER),
            new Permiso("USUARIOS_ACTUALIZAR", "Actualizar usuarios", "USUARIOS", Permiso.Accion.ACTUALIZAR),
            new Permiso("USUARIOS_ELIMINAR", "Eliminar usuarios", "USUARIOS", Permiso.Accion.ELIMINAR),
            
            // Permisos de Contabilidad
            new Permiso("CONTABILIDAD_CREAR", "Crear registros contables", "CONTABILIDAD", Permiso.Accion.CREAR),
            new Permiso("CONTABILIDAD_LEER", "Leer registros contables", "CONTABILIDAD", Permiso.Accion.LEER),
            new Permiso("CONTABILIDAD_ACTUALIZAR", "Actualizar registros contables", "CONTABILIDAD", Permiso.Accion.ACTUALIZAR),
            new Permiso("CONTABILIDAD_ELIMINAR", "Eliminar registros contables", "CONTABILIDAD", Permiso.Accion.ELIMINAR),
            
            // Permisos de Empleados
            new Permiso("EMPLEADOS_CREAR", "Crear empleados", "EMPLEADOS", Permiso.Accion.CREAR),
            new Permiso("EMPLEADOS_LEER", "Leer empleados", "EMPLEADOS", Permiso.Accion.LEER),
            new Permiso("EMPLEADOS_ACTUALIZAR", "Actualizar empleados", "EMPLEADOS", Permiso.Accion.ACTUALIZAR),
            new Permiso("EMPLEADOS_ELIMINAR", "Eliminar empleados", "EMPLEADOS", Permiso.Accion.ELIMINAR),
            
            // Permisos de Clientes
            new Permiso("CLIENTES_CREAR", "Crear clientes", "CLIENTES", Permiso.Accion.CREAR),
            new Permiso("CLIENTES_LEER", "Leer clientes", "CLIENTES", Permiso.Accion.LEER),
            new Permiso("CLIENTES_ACTUALIZAR", "Actualizar clientes", "CLIENTES", Permiso.Accion.ACTUALIZAR),
            new Permiso("CLIENTES_ELIMINAR", "Eliminar clientes", "CLIENTES", Permiso.Accion.ELIMINAR),
            
            // Permisos de Proveedores
            new Permiso("PROVEEDORES_CREAR", "Crear proveedores", "PROVEEDORES", Permiso.Accion.CREAR),
            new Permiso("PROVEEDORES_LEER", "Leer proveedores", "PROVEEDORES", Permiso.Accion.LEER),
            new Permiso("PROVEEDORES_ACTUALIZAR", "Actualizar proveedores", "PROVEEDORES", Permiso.Accion.ACTUALIZAR),
            new Permiso("PROVEEDORES_ELIMINAR", "Eliminar proveedores", "PROVEEDORES", Permiso.Accion.ELIMINAR),
            
            // Permisos de Roles
            new Permiso("ROLES_CREAR", "Crear roles", "ROLES", Permiso.Accion.CREAR),
            new Permiso("ROLES_LEER", "Leer roles", "ROLES", Permiso.Accion.LEER),
            new Permiso("ROLES_ACTUALIZAR", "Actualizar roles", "ROLES", Permiso.Accion.ACTUALIZAR),
            new Permiso("ROLES_ELIMINAR", "Eliminar roles", "ROLES", Permiso.Accion.ELIMINAR),
            
            // Permisos de Permisos
            new Permiso("PERMISOS_CREAR", "Crear permisos", "PERMISOS", Permiso.Accion.CREAR),
            new Permiso("PERMISOS_LEER", "Leer permisos", "PERMISOS", Permiso.Accion.LEER),
            new Permiso("PERMISOS_ACTUALIZAR", "Actualizar permisos", "PERMISOS", Permiso.Accion.ACTUALIZAR),
            new Permiso("PERMISOS_ELIMINAR", "Eliminar permisos", "PERMISOS", Permiso.Accion.ELIMINAR),
            
            // Permiso de Administración
            new Permiso("ADMINISTRAR", "Administrar sistema", "SISTEMA", Permiso.Accion.ADMINISTRAR)
        );

        permisoRepository.saveAll(permisos);
    }

    private void initializeRoles() {
        if (rolRepository.count() > 0) {
            return;
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
                       p.getRecurso().equals("INVENTARIO") ||
                       p.getRecurso().equals("CLIENTES"))
            .toList());
        
        Rol contadorRol = new Rol("CONTADOR", "Contador");
        contadorRol.setPermisos(todosLosPermisos.stream()
            .filter(p -> p.getRecurso().equals("CONTABILIDAD") || 
                       p.getRecurso().equals("VENTAS") ||
                       p.getRecurso().equals("PRODUCTOS") ||
                       p.getRecurso().equals("CLIENTES"))
            .toList());

        Rol gerenteRol = new Rol("GERENTE", "Gerente");
        gerenteRol.setPermisos(todosLosPermisos.stream()
            .filter(p -> p.getRecurso().equals("PRODUCTOS") || 
                       p.getRecurso().equals("VENTAS") || 
                       p.getRecurso().equals("INVENTARIO") || 
                       p.getRecurso().equals("CLIENTES") || 
                       p.getRecurso().equals("EMPLEADOS") || 
                       p.getRecurso().equals("CONTABILIDAD") || 
                       p.getRecurso().equals("PROVEEDORES"))
            .filter(p -> p.getAccion() == Permiso.Accion.LEER || p.getAccion() == Permiso.Accion.ACTUALIZAR)
            .toList());

        rolRepository.saveAll(List.of(adminRol, vendedorRol, contadorRol, gerenteRol));
    }

    private void createAdminUser() {
        // Obtener rol de administrador
        Optional<Rol> adminRolOpt = rolRepository.findByNombre("ADMIN");
        if (adminRolOpt.isEmpty()) {
            System.err.println("Error: Rol ADMIN no encontrado");
            return;
        }

        // Buscar usuario admin existente
        Optional<Usuario> adminExistente = usuarioRepository.findByUser("admin");
        
        if (adminExistente.isPresent()) {
            // Actualizar contraseña del admin existente para asegurar que sea correcta
            Usuario admin = adminExistente.get();
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@inventariado.com");
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setActivo(true);
            admin.setRoles(List.of(adminRolOpt.get()));
            usuarioRepository.save(admin);
            System.out.println("Usuario admin actualizado con contraseña correcta");
        } else {
            // Crear nuevo usuario administrador
            Usuario admin = new Usuario();
            admin.setUser("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@inventariado.com");
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setActivo(true);
            admin.setRoles(List.of(adminRolOpt.get()));
            usuarioRepository.save(admin);
            System.out.println("Usuario admin creado exitosamente");
        }
    }
}

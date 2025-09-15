package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.empleados.Empleado;
import co.com.api.co.com.api.domain.empleados.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "*")
public class EmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);

    @Autowired
    private EmpleadoRepository empleadoRepository;

    // GET - Obtener todos los empleados
    @GetMapping
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        try {
            List<Empleado> empleados = empleadoRepository.findAll();
            return ResponseEntity.ok(empleados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener empleado por ID
    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable Long id) {
        try {
            Optional<Empleado> empleado = empleadoRepository.findById(id);
            return empleado.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener empleado por cédula
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<Empleado> getEmpleadoByCedula(@PathVariable String cedula) {
        try {
            Optional<Empleado> empleado = empleadoRepository.findByCedula(cedula);
            return empleado.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener empleado por correo
    @GetMapping("/correo/{correo}")
    public ResponseEntity<Empleado> getEmpleadoByCorreo(@PathVariable String correo) {
        try {
            Optional<Empleado> empleado = empleadoRepository.findByCorreo(correo);
            return empleado.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear nuevo empleado
    @PostMapping
    public ResponseEntity<Empleado> createEmpleado(@RequestBody Empleado empleado) {
        logger.info("Intentando crear empleado: {}", empleado.getNombre());
        try {
            // Validar datos de entrada
            if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
                logger.warn("Nombre de empleado vacío");
                return ResponseEntity.badRequest().build();
            }

            if (empleado.getCedula() == null || empleado.getCedula().trim().isEmpty()) {
                logger.warn("Cédula de empleado vacía");
                return ResponseEntity.badRequest().build();
            }

            if (empleado.getEdad() == null || empleado.getEdad() <= 0) {
                logger.warn("Edad de empleado inválida: {}", empleado.getEdad());
                return ResponseEntity.badRequest().build();
            }

            if (empleado.getTelefono() == null || empleado.getTelefono().trim().isEmpty()) {
                logger.warn("Teléfono de empleado vacío");
                return ResponseEntity.badRequest().build();
            }

            if (empleado.getCorreo() == null || empleado.getCorreo().trim().isEmpty()) {
                logger.warn("Correo de empleado vacío");
                return ResponseEntity.badRequest().build();
            }

            if (empleado.getAntiguedad() == null) {
                logger.warn("Fecha de antigüedad de empleado vacía");
                return ResponseEntity.badRequest().build();
            }

            // Verificar si ya existe un empleado con la misma cédula
            Optional<Empleado> empleadoExistente = empleadoRepository.findByCedula(empleado.getCedula().trim());
            if (empleadoExistente.isPresent()) {
                logger.warn("Ya existe un empleado con la cédula: {}", empleado.getCedula());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Verificar si ya existe un empleado con el mismo correo
            Optional<Empleado> empleadoConCorreo = empleadoRepository.findByCorreo(empleado.getCorreo().trim());
            if (empleadoConCorreo.isPresent()) {
                logger.warn("Ya existe un empleado con el correo: {}", empleado.getCorreo());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Limpiar y preparar datos
            empleado.setNombre(empleado.getNombre().trim());
            empleado.setCedula(empleado.getCedula().trim());
            empleado.setTelefono(empleado.getTelefono().trim());
            empleado.setCorreo(empleado.getCorreo().trim());

            Empleado nuevoEmpleado = empleadoRepository.save(empleado);
            logger.info("Empleado creado exitosamente: {} (ID: {})", nuevoEmpleado.getNombre(), nuevoEmpleado.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
        } catch (Exception e) {
            logger.error("Error al crear empleado: {}", empleado.getNombre(), e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT - Actualizar empleado
    @PutMapping("/{id}")
    public ResponseEntity<Empleado> updateEmpleado(@PathVariable Long id, @RequestBody Empleado empleadoActualizado) {
        try {
            Optional<Empleado> empleadoExistente = empleadoRepository.findById(id);
            if (empleadoExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Empleado empleado = empleadoExistente.get();
            empleado.setNombre(empleadoActualizado.getNombre());
            empleado.setCedula(empleadoActualizado.getCedula());
            empleado.setEdad(empleadoActualizado.getEdad());
            empleado.setTelefono(empleadoActualizado.getTelefono());
            empleado.setCorreo(empleadoActualizado.getCorreo());
            empleado.setAntiguedad(empleadoActualizado.getAntiguedad());

            Empleado empleadoGuardado = empleadoRepository.save(empleado);
            return ResponseEntity.ok(empleadoGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar datos específicos del empleado
    @PatchMapping("/{id}/datos-personales")
    public ResponseEntity<Empleado> updateDatosPersonales(@PathVariable Long id, 
                                                         @RequestParam(required = false) String telefono,
                                                         @RequestParam(required = false) String correo) {
        try {
            Optional<Empleado> empleado = empleadoRepository.findById(id);
            if (empleado.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Empleado empleadoActualizado = empleado.get();
            if (telefono != null) {
                empleadoActualizado.setTelefono(telefono);
            }
            if (correo != null) {
                empleadoActualizado.setCorreo(correo);
            }

            Empleado empleadoGuardado = empleadoRepository.save(empleadoActualizado);
            return ResponseEntity.ok(empleadoGuardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE - Eliminar empleado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long id) {
        try {
            if (!empleadoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            empleadoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

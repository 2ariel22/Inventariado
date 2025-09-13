package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.empleados.Empleado;
import co.com.api.co.com.api.domain.empleados.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "*")
public class EmpleadoController {

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
        try {
            // Verificar si ya existe un empleado con la misma cédula
            Optional<Empleado> empleadoExistente = empleadoRepository.findByCedula(empleado.getCedula());
            if (empleadoExistente.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Verificar si ya existe un empleado con el mismo correo
            Optional<Empleado> empleadoConCorreo = empleadoRepository.findByCorreo(empleado.getCorreo());
            if (empleadoConCorreo.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Empleado nuevoEmpleado = empleadoRepository.save(empleado);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
        } catch (Exception e) {
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

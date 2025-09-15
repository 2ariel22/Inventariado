package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.contabilidad.Contabilidad;
import co.com.api.co.com.api.domain.contabilidad.ContabilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contabilidad")
@CrossOrigin(origins = "*")
public class ContabilidadController {

    private static final Logger logger = LoggerFactory.getLogger(ContabilidadController.class);

    @Autowired
    private ContabilidadRepository contabilidadRepository;

    // GET - Obtener todos los registros contables
    @GetMapping
    public ResponseEntity<List<Contabilidad>> getAllContabilidad() {
        try {
            List<Contabilidad> contabilidad = contabilidadRepository.findAll();
            return ResponseEntity.ok(contabilidad);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener registro contable por ID
    @GetMapping("/{id}")
    public ResponseEntity<Contabilidad> getContabilidadById(@PathVariable Long id) {
        try {
            Optional<Contabilidad> contabilidad = contabilidadRepository.findById(id);
            return contabilidad.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener registros por rango de fechas
    @GetMapping("/fechas")
    public ResponseEntity<List<Contabilidad>> getContabilidadByFechas(
            @RequestParam String fechaInicio, 
            @RequestParam String fechaFin) {
        try {
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);
            List<Contabilidad> contabilidad = contabilidadRepository.findByFechaBetween(inicio, fin);
            return ResponseEntity.ok(contabilidad);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Obtener registros por tipo de movimiento
    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<Contabilidad>> getContabilidadByTipo(@PathVariable String tipoMovimiento) {
        try {
            Contabilidad.TipoMovimiento tipo = Contabilidad.TipoMovimiento.valueOf(tipoMovimiento.toUpperCase());
            List<Contabilidad> contabilidad = contabilidadRepository.findByTipoMovimiento(tipo);
            return ResponseEntity.ok(contabilidad);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // POST - Crear nuevo registro contable
    @PostMapping
    public ResponseEntity<Contabilidad> createContabilidad(@RequestBody Contabilidad contabilidad) {
        logger.info("Intentando crear registro contable");
        try {
            // Validar datos de entrada
            if (contabilidad.getTipoMovimiento() == null) {
                logger.warn("Tipo de movimiento es obligatorio");
                return ResponseEntity.badRequest().build();
            }

            if (contabilidad.getFecha() == null) {
                logger.warn("Fecha es obligatoria");
                return ResponseEntity.badRequest().build();
            }

            if (contabilidad.getGastos() == null && contabilidad.getIngresos() == null) {
                logger.warn("Debe proporcionar al menos un monto (gastos o ingresos)");
                return ResponseEntity.badRequest().build();
            }

            // Validar montos
            if (contabilidad.getGastos() != null && contabilidad.getGastos().compareTo(java.math.BigDecimal.ZERO) < 0) {
                logger.warn("Los gastos no pueden ser negativos: {}", contabilidad.getGastos());
                return ResponseEntity.badRequest().build();
            }

            if (contabilidad.getIngresos() != null && contabilidad.getIngresos().compareTo(java.math.BigDecimal.ZERO) < 0) {
                logger.warn("Los ingresos no pueden ser negativos: {}", contabilidad.getIngresos());
                return ResponseEntity.badRequest().build();
            }

            // Establecer valores por defecto si no se proporcionan
            if (contabilidad.getGastos() == null) {
                contabilidad.setGastos(java.math.BigDecimal.ZERO);
            }
            if (contabilidad.getIngresos() == null) {
                contabilidad.setIngresos(java.math.BigDecimal.ZERO);
            }

            // Limpiar descripción si se proporciona
            if (contabilidad.getDescripcion() != null) {
                contabilidad.setDescripcion(contabilidad.getDescripcion().trim());
            }

            Contabilidad nuevoRegistro = contabilidadRepository.save(contabilidad);
            logger.info("Registro contable creado exitosamente: ID {}, Tipo: {}, Fecha: {}", 
                       nuevoRegistro.getId(), nuevoRegistro.getTipoMovimiento(), nuevoRegistro.getFecha());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRegistro);
        } catch (Exception e) {
            logger.error("Error al crear registro contable", e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear registro de gasto
    @PostMapping("/gasto")
    public ResponseEntity<Contabilidad> createGasto(@RequestBody Contabilidad contabilidad) {
        logger.info("Intentando crear registro de gasto");
        try {
            // Validar datos de entrada
            if (contabilidad.getGastos() == null || contabilidad.getGastos().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                logger.warn("Monto de gasto inválido: {}", contabilidad.getGastos());
                return ResponseEntity.badRequest().build();
            }

            if (contabilidad.getFecha() == null) {
                logger.warn("Fecha es obligatoria para el gasto");
                return ResponseEntity.badRequest().build();
            }

            // Configurar como gasto
            contabilidad.setTipoMovimiento(Contabilidad.TipoMovimiento.GASTO);
            contabilidad.setIngresos(java.math.BigDecimal.ZERO);

            // Limpiar descripción si se proporciona
            if (contabilidad.getDescripcion() != null) {
                contabilidad.setDescripcion(contabilidad.getDescripcion().trim());
            }

            Contabilidad nuevoGasto = contabilidadRepository.save(contabilidad);
            logger.info("Gasto creado exitosamente: ID {}, Monto: {}, Fecha: {}", 
                       nuevoGasto.getId(), nuevoGasto.getGastos(), nuevoGasto.getFecha());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoGasto);
        } catch (Exception e) {
            logger.error("Error al crear gasto", e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear registro de ingreso
    @PostMapping("/ingreso")
    public ResponseEntity<Contabilidad> createIngreso(@RequestBody Contabilidad contabilidad) {
        logger.info("Intentando crear registro de ingreso");
        try {
            // Validar datos de entrada
            if (contabilidad.getIngresos() == null || contabilidad.getIngresos().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                logger.warn("Monto de ingreso inválido: {}", contabilidad.getIngresos());
                return ResponseEntity.badRequest().build();
            }

            if (contabilidad.getFecha() == null) {
                logger.warn("Fecha es obligatoria para el ingreso");
                return ResponseEntity.badRequest().build();
            }

            // Configurar como ingreso
            contabilidad.setTipoMovimiento(Contabilidad.TipoMovimiento.INGRESO);
            contabilidad.setGastos(java.math.BigDecimal.ZERO);

            // Limpiar descripción si se proporciona
            if (contabilidad.getDescripcion() != null) {
                contabilidad.setDescripcion(contabilidad.getDescripcion().trim());
            }

            Contabilidad nuevoIngreso = contabilidadRepository.save(contabilidad);
            logger.info("Ingreso creado exitosamente: ID {}, Monto: {}, Fecha: {}", 
                       nuevoIngreso.getId(), nuevoIngreso.getIngresos(), nuevoIngreso.getFecha());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoIngreso);
        } catch (Exception e) {
            logger.error("Error al crear ingreso", e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT - Actualizar registro contable
    @PutMapping("/{id}")
    public ResponseEntity<Contabilidad> updateContabilidad(@PathVariable Long id, @RequestBody Contabilidad contabilidadActualizada) {
        try {
            Optional<Contabilidad> contabilidadExistente = contabilidadRepository.findById(id);
            if (contabilidadExistente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Contabilidad contabilidad = contabilidadExistente.get();
            contabilidad.setGastos(contabilidadActualizada.getGastos());
            contabilidad.setIngresos(contabilidadActualizada.getIngresos());
            contabilidad.setFecha(contabilidadActualizada.getFecha());
            contabilidad.setDescripcion(contabilidadActualizada.getDescripcion());
            contabilidad.setTipoMovimiento(contabilidadActualizada.getTipoMovimiento());

            Contabilidad contabilidadGuardada = contabilidadRepository.save(contabilidad);
            return ResponseEntity.ok(contabilidadGuardada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar monto de gasto
    @PatchMapping("/{id}/gasto")
    public ResponseEntity<Contabilidad> updateGasto(@PathVariable Long id, 
                                                   @RequestParam String monto) {
        try {
            Optional<Contabilidad> contabilidad = contabilidadRepository.findById(id);
            if (contabilidad.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Contabilidad contabilidadActualizada = contabilidad.get();
            contabilidadActualizada.setGastos(new java.math.BigDecimal(monto));
            contabilidadActualizada.setTipoMovimiento(Contabilidad.TipoMovimiento.GASTO);
            contabilidadActualizada.setIngresos(java.math.BigDecimal.ZERO);

            Contabilidad contabilidadGuardada = contabilidadRepository.save(contabilidadActualizada);
            return ResponseEntity.ok(contabilidadGuardada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PATCH - Actualizar monto de ingreso
    @PatchMapping("/{id}/ingreso")
    public ResponseEntity<Contabilidad> updateIngreso(@PathVariable Long id, 
                                                     @RequestParam String monto) {
        try {
            Optional<Contabilidad> contabilidad = contabilidadRepository.findById(id);
            if (contabilidad.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Contabilidad contabilidadActualizada = contabilidad.get();
            contabilidadActualizada.setIngresos(new java.math.BigDecimal(monto));
            contabilidadActualizada.setTipoMovimiento(Contabilidad.TipoMovimiento.INGRESO);
            contabilidadActualizada.setGastos(java.math.BigDecimal.ZERO);

            Contabilidad contabilidadGuardada = contabilidadRepository.save(contabilidadActualizada);
            return ResponseEntity.ok(contabilidadGuardada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE - Eliminar registro contable
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContabilidad(@PathVariable Long id) {
        try {
            if (!contabilidadRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            contabilidadRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

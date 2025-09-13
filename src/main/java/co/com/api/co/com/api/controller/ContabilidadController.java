package co.com.api.co.com.api.controller;

import co.com.api.co.com.api.domain.contabilidad.Contabilidad;
import co.com.api.co.com.api.domain.contabilidad.ContabilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contabilidad")
@CrossOrigin(origins = "*")
public class ContabilidadController {

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
        try {
            Contabilidad nuevoRegistro = contabilidadRepository.save(contabilidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRegistro);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear registro de gasto
    @PostMapping("/gasto")
    public ResponseEntity<Contabilidad> createGasto(@RequestBody Contabilidad contabilidad) {
        try {
            contabilidad.setTipoMovimiento(Contabilidad.TipoMovimiento.GASTO);
            contabilidad.setIngresos(java.math.BigDecimal.ZERO);
            Contabilidad nuevoGasto = contabilidadRepository.save(contabilidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoGasto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Crear registro de ingreso
    @PostMapping("/ingreso")
    public ResponseEntity<Contabilidad> createIngreso(@RequestBody Contabilidad contabilidad) {
        try {
            contabilidad.setTipoMovimiento(Contabilidad.TipoMovimiento.INGRESO);
            contabilidad.setGastos(java.math.BigDecimal.ZERO);
            Contabilidad nuevoIngreso = contabilidadRepository.save(contabilidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoIngreso);
        } catch (Exception e) {
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

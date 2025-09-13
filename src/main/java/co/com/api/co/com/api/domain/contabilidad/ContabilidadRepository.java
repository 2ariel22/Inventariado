package co.com.api.co.com.api.domain.contabilidad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContabilidadRepository extends JpaRepository<Contabilidad, Long> {
    List<Contabilidad> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<Contabilidad> findByTipoMovimiento(Contabilidad.TipoMovimiento tipoMovimiento);
}

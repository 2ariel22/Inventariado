package co.com.api.co.com.api.domain.inventario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.com.api.co.com.api.domain.empleados.Empleado;
import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByResponsable(Empleado responsable);
}

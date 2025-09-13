package co.com.api.co.com.api.domain.ventas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.com.api.co.com.api.domain.clientes.Cliente;
import co.com.api.co.com.api.domain.empleados.Empleado;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByCliente(Cliente cliente);
    List<Venta> findByVendedor(Empleado vendedor);
    List<Venta> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<Venta> findByEstado(Venta.EstadoVenta estado);
}

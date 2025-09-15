package co.com.api.co.com.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateVentaRequest(
    Long clienteId,
    Long vendedorId,
    String descripcion,
    LocalDate fecha,
    BigDecimal valorVenta,
    List<Long> productoIds,
    String estado
) {
    // Constructor vacío para Jackson
    public CreateVentaRequest() {
        this(null, null, null, null, null, null, null);
    }
}

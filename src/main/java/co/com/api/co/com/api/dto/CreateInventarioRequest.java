package co.com.api.co.com.api.dto;

import java.util.List;

public record CreateInventarioRequest(
    Long responsableId,
    String descripcion,
    List<Long> productoIds
) {}

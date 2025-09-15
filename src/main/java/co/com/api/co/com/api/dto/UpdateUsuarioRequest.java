package co.com.api.co.com.api.dto;

import java.util.List;

public record UpdateUsuarioRequest(
    String user,
    String password,
    String email,
    String nombre,
    String apellido,
    Boolean activo,
    List<Long> rolIds
) {
    // Constructor vacío para Jackson
    public UpdateUsuarioRequest() {
        this(null, null, null, null, null, null, null);
    }
}

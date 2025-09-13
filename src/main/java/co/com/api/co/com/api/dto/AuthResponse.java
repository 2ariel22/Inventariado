package co.com.api.co.com.api.dto;

import co.com.api.co.com.api.domain.roles.Rol;
import java.util.List;

public record AuthResponse(
    String token,
    String user,
    String email,
    String nombre,
    String apellido,
    List<String> roles,
    boolean activo
) {}

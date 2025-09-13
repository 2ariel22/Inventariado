package co.com.api.co.com.api.dto;

import java.util.List;

public record RegisterRequest(
    String user,
    String password,
    String email,
    String nombre,
    String apellido,
    List<Long> rolIds
) {}

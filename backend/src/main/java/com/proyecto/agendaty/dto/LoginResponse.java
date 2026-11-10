package com.proyecto.agendaty.dto;

public record LoginResponse(
        String token,
        String tipo,
        Integer usuarioId,
        String username,
        String rol
) {
}

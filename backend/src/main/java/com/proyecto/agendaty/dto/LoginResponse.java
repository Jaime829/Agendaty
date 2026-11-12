package com.proyecto.agendaty.dto;

public record LoginResponse(
        Integer id,
        String nombre,
        String correo,
        String rol
) {
}

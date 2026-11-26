package com.proyecto.agendaty.dto;

public record TipoTramiteResponse(
        Long id,
        String nombre,
        String codigo,
        Boolean esPrioritarioPorDefecto,
        Boolean activo
) {
}

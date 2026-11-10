package com.proyecto.agendaty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoTramiteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El código es obligatorio")
        @Size(max = 10, message = "El código no puede superar los 10 caracteres")
        String codigo,

        Boolean esPrioritarioPorDefecto,

        Boolean activo
) {
}

package com.proyecto.agendaty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtencionRequest(
        @NotBlank(message = "El resultado es obligatorio")
        @Size(max = 1000, message = "El resultado no puede superar los 1000 caracteres")
        String resultado,

        @Size(max = 1000, message = "Las observaciones no pueden superar los 1000 caracteres")
        String observaciones
) {
}

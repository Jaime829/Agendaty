package com.proyecto.agendaty.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TurnoRequest(
        @NotNull(message = "El ciudadano es obligatorio")
        @Positive(message = "El id del ciudadano debe ser positivo")
        Integer ciudadanoId,

        @NotNull(message = "El tipo de trámite es obligatorio")
        @Positive(message = "El id del tipo de trámite debe ser positivo")
        Long tipoTramiteId,

        Boolean prioritario
) {
}

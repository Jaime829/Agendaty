package com.proyecto.agendaty.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CiudadanoUpdateRequest(
        @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
        String nombre,

        @Pattern(regexp = "^\\d{7,10}$", message = "El telefono debe tener entre 7 y 10 digitos")
        String telefono) {
}

package com.proyecto.agendaty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CiudadanoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
        String nombre,

        @NotBlank(message = "El documento es obligatorio")
        @Pattern(regexp = "^\\d{5,10}$", message = "El documento debe tener entre 5 y 10 digitos")
        String documento,

        @NotBlank(message = "El telefono es obligatorio")
        @Pattern(regexp = "^\\d{7,10}$", message = "El telefono debe tener entre 7 y 10 digitos")
        String telefono) {
}
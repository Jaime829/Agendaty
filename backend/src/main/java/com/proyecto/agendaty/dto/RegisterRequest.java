package com.proyecto.agendaty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        @NotBlank(message = "El documento es obligatorio")
        @Pattern(regexp = "^\\d{5,10}$", message = "El documento debe tener entre 5 y 10 digitos")
        String documento,

        @NotBlank(message = "El telefono es obligatorio")
        @Pattern(regexp = "^\\d{7,10}$", message = "El telefono debe tener entre 7 y 10 digitos")
        String telefono,

        String rol
) {
}

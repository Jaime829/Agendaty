package com.proyecto.agendaty.dto;

import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

public record VentanillaDTO(
        @Min(value = 1, message = "Solo puedes ingresar numeros mayores o iguales a 1") Integer numero,
        @Length(max = 255, message = "El maximo de caracteres es de 255") String descripcion
) {
}

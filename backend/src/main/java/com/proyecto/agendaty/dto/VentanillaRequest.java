package com.proyecto.agendaty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentanillaRequest {
    @NotNull(message = "El numero de ventanilla es obligatorio")
    @Min(value = 1)
    private Integer numero;

    @Length(max = 255, message = "La descripcion no puede superar los 255 caracteres")
    private String descripcion;
}

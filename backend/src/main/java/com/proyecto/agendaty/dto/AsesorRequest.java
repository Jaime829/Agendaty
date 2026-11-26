package com.proyecto.agendaty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsesorRequest {
    @Length(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombre;
}

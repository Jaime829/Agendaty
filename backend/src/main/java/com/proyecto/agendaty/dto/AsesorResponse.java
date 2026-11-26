package com.proyecto.agendaty.dto;

import com.proyecto.agendaty.entity.Ventanilla;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsesorResponse {
    private Integer id;
    private String nombre;
    private Boolean disponible;
    private Integer numeroVentanilla;
}

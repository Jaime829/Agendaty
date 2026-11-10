package com.proyecto.agendaty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentanillaResponse {
    private Long id;
    private Integer numero;
    private String descripcion;
    private Integer asesorId;
}

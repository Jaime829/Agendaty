package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.AsesorRequest;
import com.proyecto.agendaty.dto.AsesorResponse;

import java.util.List;

public interface AsesorService {
    AsesorResponse crearAsesor(AsesorRequest asesor, Long numeroVentanilla);
    List<AsesorResponse> listarAsesores();
    AsesorResponse obtenerAsesor(Integer id);
    AsesorResponse cambiarDisponibilidad(Integer id, Boolean disponible);
}

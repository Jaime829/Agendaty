package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.AsesorRequest;
import com.proyecto.agendaty.dto.AsesorResponse;

public interface AsesorService {
    public AsesorResponse crearAsesor(AsesorRequest asesor, Integer numeroVentanilla);
}

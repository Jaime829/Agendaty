package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.AsesorRequest;
import com.proyecto.agendaty.dto.AsesorResponse;
import com.proyecto.agendaty.entity.Ciudadano;

public interface UsuarioService {
    public Ciudadano crearCiudadano(Ciudadano ciudadano);

    public AsesorResponse crearAsesor(AsesorRequest asesor, Integer numeroVentanilla);
}

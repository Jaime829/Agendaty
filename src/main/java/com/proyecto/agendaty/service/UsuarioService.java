package com.proyecto.agendaty.service;

import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ciudadano;

public interface UsuarioService {
    public Ciudadano crearCiudadano(Ciudadano ciudadano);

    public Asesor crearAsesor(Asesor asesor, Integer numeroVentanilla);
}

package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.VentanillaDTO;
import com.proyecto.agendaty.entity.Ventanilla;

import java.util.List;

public interface VentanillaService {
    VentanillaDTO crearVentanilla(VentanillaDTO ventanilla);

    List<VentanillaDTO> listarVentanillas();
}

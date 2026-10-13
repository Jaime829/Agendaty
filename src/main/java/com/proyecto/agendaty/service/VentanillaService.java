package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.VentanillaRequest;
import com.proyecto.agendaty.dto.VentanillaResponse;

import java.util.List;

public interface VentanillaService {
    VentanillaResponse crearVentanilla(VentanillaRequest ventanilla);
    List<VentanillaResponse> listarVentanillas();
}

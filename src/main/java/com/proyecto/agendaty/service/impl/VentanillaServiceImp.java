package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.entity.Ventanilla;
import com.proyecto.agendaty.repository.VentanillaRepository;
import com.proyecto.agendaty.service.VentanillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VentanillaServiceImp implements VentanillaService {

    private final VentanillaRepository ventanillaRepository;

    @Override
    public Ventanilla crearVentanilla(Ventanilla ventanilla) {
        return ventanillaRepository.save(ventanilla);
    }
}

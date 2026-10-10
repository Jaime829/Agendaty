package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.VentanillaDTO;
import com.proyecto.agendaty.entity.Ventanilla;
import com.proyecto.agendaty.mapper.VentanillaMapper;
import com.proyecto.agendaty.repository.VentanillaRepository;
import com.proyecto.agendaty.service.VentanillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VentanillaServiceImp implements VentanillaService {

    private final VentanillaRepository ventanillaRepository;
    private final VentanillaMapper ventanillaMapper;

    @Override
    public VentanillaDTO crearVentanilla(VentanillaDTO ventanilla) {

        Ventanilla ventanillaSave = ventanillaRepository.save(ventanillaMapper.toEntity(ventanilla));

        return ventanillaMapper.toResponse(ventanillaSave);
    }

    @Override
    public List<VentanillaDTO> listarVentanillas() {
        List<Ventanilla> ventanillaList = ventanillaRepository.findAll();

        return ventanillaMapper.toResponseList(ventanillaList);
    }
}

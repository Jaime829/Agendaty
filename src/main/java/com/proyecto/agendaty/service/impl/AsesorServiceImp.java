package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.AsesorRequest;
import com.proyecto.agendaty.dto.AsesorResponse;
import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ventanilla;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.mapper.AsesorMapper;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import com.proyecto.agendaty.repository.VentanillaRepository;
import com.proyecto.agendaty.service.AsesorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsesorServiceImp implements AsesorService {

    private final AsesorRepository asesorRepository;
    private final VentanillaRepository ventanillaRepository;
    private final AsesorMapper asesorMapper;

    @Override
    public AsesorResponse crearAsesor(AsesorRequest asesor, Integer numeroVentanilla) {

        Ventanilla ventanilla = ventanillaRepository.findById(numeroVentanilla)
                .orElseThrow(()-> new ResourceNotFoundException("La ventanilla " + numeroVentanilla.toString() + " no existe, primero debes crearla"));



        boolean yaAsignada = asesorRepository.existsByVentanillaActual(ventanilla);
        if(yaAsignada){
            throw new BadRequestException("La ventanilla ".concat(numeroVentanilla.toString().concat(" ya esta ocupada por un asesor")));
        }

        Asesor asesorEntity = asesorMapper.toEntity(asesor);

        assert asesorEntity != null;
        asesorEntity.setVentanillaActual(ventanilla);

        Asesor asesorSave = asesorRepository.save(asesorEntity);

        return asesorMapper.toResponse(asesorSave);
    }
}

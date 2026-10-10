package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.AsesorRequest;
import com.proyecto.agendaty.dto.AsesorResponse;
import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.entity.Ventanilla;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.mapper.AsesorMapper;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import com.proyecto.agendaty.repository.VentanillaRepository;
import com.proyecto.agendaty.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImp implements UsuarioService {

    private final AsesorRepository asesorRepository;
    private final CiudadanoRepository ciudadanoRepository;
    private final VentanillaRepository ventanillaRepository;
    private final AsesorMapper asesorMapper;

    @Override
    public Ciudadano crearCiudadano(Ciudadano ciudadano) {

        if(ciudadanoRepository.existsByDocumento(ciudadano.getDocumento())){
            throw new BadRequestException("Ya existe un ciudadano con ese documento");
        }
        return ciudadanoRepository.save(ciudadano);
    }

    @Override
    public AsesorResponse crearAsesor(AsesorRequest asesor, Integer numeroVentanilla) {

        Ventanilla ventanilla = ventanillaRepository.findById(numeroVentanilla)
                .orElseThrow(()-> new ResourceNotFoundException("La ventanilla " + numeroVentanilla.toString() + " no existe, primero debes crearla"));



        boolean yaAsignada = asesorRepository.existsByVentanillaActual(ventanilla);
        if(yaAsignada){
            throw new BadRequestException("La ventanilla ".concat(numeroVentanilla.toString().concat(" ya esta ocupada por un asesor")));
        }

        Asesor asesorEntity = asesorMapper.toEntity(asesor);

        asesorEntity.setVentanillaActual(ventanilla);

        Asesor asesorSave = asesorRepository.save(asesorEntity);

        return asesorMapper.toResponse(asesorSave);
    }
}

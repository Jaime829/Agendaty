package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.entity.Ventanilla;
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

    @Override
    public Ciudadano crearCiudadano(Ciudadano ciudadano) {

        if(ciudadanoRepository.existsByDocumento(ciudadano.getDocumento())){
            throw new RuntimeException("Ya existe un ciudadano con ese documento");
        }
        return ciudadanoRepository.save(ciudadano);
    }

    @Override
    public Asesor crearAsesor(Asesor asesor, Integer numeroVentanilla) {

        Ventanilla ventanilla = ventanillaRepository.findById(numeroVentanilla)
                .orElseThrow(()-> new RuntimeException("La ventanilla " + numeroVentanilla.toString() + " no existe, primero debes crearla"));



        boolean yaAsignada = asesorRepository.existsByVentanillaActual(ventanilla);
        if(yaAsignada){
            throw new RuntimeException("La ventanilla ".concat(numeroVentanilla.toString().concat(" ya esta ocupada por un asesor")));
        }

        asesor.setVentanillaActual(ventanilla);
        return asesorRepository.save(asesor);
    }
}

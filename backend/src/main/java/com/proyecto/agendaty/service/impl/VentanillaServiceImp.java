package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.VentanillaRequest;
import com.proyecto.agendaty.dto.VentanillaResponse;
import com.proyecto.agendaty.entity.Ventanilla;
import com.proyecto.agendaty.exceptions.BadRequestException;
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
    public VentanillaResponse crearVentanilla(VentanillaRequest ventanilla) {

        if(ventanillaRepository.existsByNumero(ventanilla.getNumero())) {
            throw new BadRequestException("Ya existe una ventanilla con el numero " + ventanilla.getNumero());
        }

        Ventanilla ventanillaSave = ventanillaRepository.save(ventanillaMapper.toEntity(ventanilla));

        return ventanillaMapper.toResponse(ventanillaSave);
    }

    @Override
    public List<VentanillaResponse> listarVentanillas() {
        List<Ventanilla> ventanillaList = ventanillaRepository.findAll();

        return ventanillaList.stream()
                .map(ventanillaMapper::toResponse)
                .toList();
    }

    @Override
    public VentanillaResponse obtenerVentanilla(Long id) {
        Ventanilla v = ventanillaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Ventanilla " + id + " no encontrada"));
        return ventanillaMapper.toResponse(v);
    }

    @Override
    public VentanillaResponse actualizarVentanilla(Long id, VentanillaRequest request) {
        Ventanilla v = ventanillaRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Ventanilla " + id + " no encontrada"));
        v.setNumero(request.getNumero());
        v.setDescripcion(request.getDescripcion());
        return ventanillaMapper.toResponse(ventanillaRepository.save(v));
    }

    @Override
    public void eliminarVentanilla(Long id) {
        if (!ventanillaRepository.existsById(id)) {
            throw new BadRequestException("Ventanilla " + id + " no encontrada");
        }
        ventanillaRepository.deleteById(id);
    }
}

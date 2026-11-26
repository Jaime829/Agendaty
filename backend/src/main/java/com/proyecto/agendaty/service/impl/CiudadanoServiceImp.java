package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.CiudadanoRequest;
import com.proyecto.agendaty.dto.CiudadanoResponse;
import com.proyecto.agendaty.dto.CiudadanoUpdateRequest;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.mapper.CiudadanoMapper;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import com.proyecto.agendaty.service.CiudadanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CiudadanoServiceImp implements CiudadanoService {

    private final CiudadanoRepository ciudadanoRepository;
    private final CiudadanoMapper ciudadanoMapper;

    @Override
    public CiudadanoResponse crearCiudadano(CiudadanoRequest request) {
        if (Boolean.TRUE.equals(ciudadanoRepository.existsByDocumento(request.documento()))) {
            throw new BadRequestException("Ya existe un ciudadano con ese documento");
        }
        Ciudadano guardado = ciudadanoRepository.save(ciudadanoMapper.toEntity(request));
        return ciudadanoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public CiudadanoResponse obtenerCiudadano(Integer id) {
        return ciudadanoMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CiudadanoResponse> listarCiudadanos() {
        return ciudadanoRepository.findAll().stream()
                .map(ciudadanoMapper::toResponse)
                .toList();
    }

    @Override
    public CiudadanoResponse actualizarCiudadano(Integer id, CiudadanoUpdateRequest request) {
        Ciudadano ciudadano = buscar(id);
        if (request.nombre() != null && !request.nombre().isBlank()) {
            ciudadano.setNombre(request.nombre());
        }
        if (request.telefono() != null && !request.telefono().isBlank()) {
            ciudadano.setTelefono(request.telefono());
        }
        return ciudadanoMapper.toResponse(ciudadanoRepository.save(ciudadano));
    }

    private Ciudadano buscar(Integer id) {
        return ciudadanoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ciudadano " + id + " no encontrado"));
    }
}

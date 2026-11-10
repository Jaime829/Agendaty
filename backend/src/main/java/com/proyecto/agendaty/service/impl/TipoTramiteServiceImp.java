package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.TipoTramiteRequest;
import com.proyecto.agendaty.dto.TipoTramiteResponse;
import com.proyecto.agendaty.entity.TipoTramite;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.mapper.TipoTramiteMapper;
import com.proyecto.agendaty.repository.TipoTramiteRepository;
import com.proyecto.agendaty.service.TipoTramiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoTramiteServiceImp implements TipoTramiteService {

    private final TipoTramiteRepository tipoTramiteRepository;
    private final TipoTramiteMapper tipoTramiteMapper;

    @Override
    public TipoTramiteResponse crear(TipoTramiteRequest request) {
        if (tipoTramiteRepository.existsByCodigo(request.codigo())) {
            throw new BadRequestException("Ya existe un tipo de trámite con el código " + request.codigo());
        }
        TipoTramite tipo = tipoTramiteMapper.toEntity(request);
        return tipoTramiteMapper.toResponse(tipoTramiteRepository.save(tipo));
    }

    @Override
    @Transactional(readOnly = true)
    public TipoTramiteResponse obtener(Long id) {
        TipoTramite tipo = tipoTramiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de trámite " + id + " no encontrado"));
        return tipoTramiteMapper.toResponse(tipo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoTramiteResponse> listar() {
        return tipoTramiteRepository.findAll().stream()
                .map(tipoTramiteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TipoTramiteResponse actualizar(Long id, TipoTramiteRequest request) {
        TipoTramite tipo = tipoTramiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de trámite " + id + " no encontrado"));
        tipo.setNombre(request.nombre());
        tipo.setCodigo(request.codigo());
        tipo.setEsPrioritarioPorDefecto(request.esPrioritarioPorDefecto() != null ? request.esPrioritarioPorDefecto() : false);
        tipo.setActivo(request.activo() != null ? request.activo() : true);
        return tipoTramiteMapper.toResponse(tipoTramiteRepository.save(tipo));
    }

    @Override
    public void eliminar(Long id) {
        if (!tipoTramiteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de trámite " + id + " no encontrado");
        }
        tipoTramiteRepository.deleteById(id);
    }
}

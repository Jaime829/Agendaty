package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.TipoTramiteRequest;
import com.proyecto.agendaty.dto.TipoTramiteResponse;
import com.proyecto.agendaty.entity.TipoTramite;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.mapper.TipoTramiteMapper;
import com.proyecto.agendaty.repository.TipoTramiteRepository;
import com.proyecto.agendaty.service.TipoTramiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoTramiteServiceImpTest {

    @Mock private TipoTramiteRepository tipoTramiteRepository;
    @Mock private TipoTramiteMapper tipoTramiteMapper;

    @InjectMocks
    private TipoTramiteServiceImp service;

    private TipoTramite tipo;

    @BeforeEach
    void setUp() {
        tipo = new TipoTramite();
        tipo.setId(1L);
        tipo.setNombre("Cedula");
        tipo.setCodigo("CED");
        tipo.setEsPrioritarioPorDefecto(false);
        tipo.setActivo(true);
    }

    @Test
    void crear_deberiaGuardarTipoTramite() {
        TipoTramiteRequest request = new TipoTramiteRequest("Cedula", "CED", false, true);
        when(tipoTramiteRepository.existsByCodigo("CED")).thenReturn(false);
        when(tipoTramiteMapper.toEntity(request)).thenReturn(tipo);
        when(tipoTramiteRepository.save(tipo)).thenReturn(tipo);
        when(tipoTramiteMapper.toResponse(tipo)).thenReturn(new TipoTramiteResponse(1L, "Cedula", "CED", false, true));

        TipoTramiteResponse respuesta = service.crear(request);

        assertNotNull(respuesta);
        assertEquals("CED", respuesta.codigo());
        verify(tipoTramiteRepository).save(tipo);
    }

    @Test
    void crear_cuandoCodigoYaExiste_deberiaLanzarExcepcion() {
        TipoTramiteRequest request = new TipoTramiteRequest("Cedula", "CED", false, true);
        when(tipoTramiteRepository.existsByCodigo("CED")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.crear(request));
    }

    @Test
    void obtener_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(tipoTramiteRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.obtener(1L));
    }

    @Test
    void listar_deberiaRetornarTipos() {
        when(tipoTramiteRepository.findAll()).thenReturn(List.of(tipo));
        when(tipoTramiteMapper.toResponse(tipo)).thenReturn(new TipoTramiteResponse(1L, "Cedula", "CED", false, true));

        List<TipoTramiteResponse> respuesta = service.listar();
        assertEquals(1, respuesta.size());
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(tipoTramiteRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.eliminar(1L));
    }
}

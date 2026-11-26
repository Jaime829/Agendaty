package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.VentanillaRequest;
import com.proyecto.agendaty.dto.VentanillaResponse;
import com.proyecto.agendaty.entity.Ventanilla;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.mapper.VentanillaMapper;
import com.proyecto.agendaty.repository.VentanillaRepository;
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
class VentanillaServiceImpTest {

    @Mock
    private VentanillaRepository ventanillaRepository;

    @Mock
    private VentanillaMapper ventanillaMapper;

    @InjectMocks
    private VentanillaServiceImp ventanillaService;

    @Test
    void crearVentanilla_guardaYRetornaResponse() {
        VentanillaRequest req = new VentanillaRequest();
        req.setNumero(3);
        req.setDescripcion("Atención");

        Ventanilla guardada = new Ventanilla();
        guardada.setId(1L);
        guardada.setNumero(3);
        guardada.setDescripcion("Atención");

        VentanillaResponse response = new VentanillaResponse(1L, 3, "Atención", null);

        when(ventanillaRepository.existsByNumero(3)).thenReturn(false);
        when(ventanillaMapper.toEntity(req)).thenReturn(guardada);
        when(ventanillaRepository.save(guardada)).thenReturn(guardada);
        when(ventanillaMapper.toResponse(guardada)).thenReturn(response);

        VentanillaResponse r = ventanillaService.crearVentanilla(req);

        assertEquals(3, r.getNumero());
        assertEquals("Atención", r.getDescripcion());
        verify(ventanillaRepository).save(guardada);
    }

    @Test
    void crearVentanilla_numeroDuplicado_lanzaExcepcion() {
        VentanillaRequest req = new VentanillaRequest();
        req.setNumero(7);

        when(ventanillaRepository.existsByNumero(7)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> ventanillaService.crearVentanilla(req));
        verify(ventanillaRepository, never()).save(any());
    }

    @Test
    void listarVentanillas_retornaMapeadas() {
        Ventanilla v1 = new Ventanilla();
        v1.setId(1L);
        v1.setNumero(1);
        Ventanilla v2 = new Ventanilla();
        v2.setId(2L);
        v2.setNumero(2);

        when(ventanillaRepository.findAll()).thenReturn(List.of(v1, v2));
        when(ventanillaMapper.toResponse(v1)).thenReturn(new VentanillaResponse(1L, 1, "V1", null));
        when(ventanillaMapper.toResponse(v2)).thenReturn(new VentanillaResponse(2L, 2, "V2", 4));

        List<VentanillaResponse> r = ventanillaService.listarVentanillas();

        assertEquals(2, r.size());
        assertEquals(1, r.get(0).getNumero());
        assertEquals(2, r.get(1).getNumero());
        assertEquals(4, r.get(1).getAsesorId());
    }

    @Test
    void obtenerVentanilla_existente_retornaResponse() {
        Ventanilla v = new Ventanilla();
        v.setId(5L);
        v.setNumero(5);
        v.setDescripcion("Prioridad");

        when(ventanillaRepository.findById(5L)).thenReturn(Optional.of(v));
        when(ventanillaMapper.toResponse(v)).thenReturn(new VentanillaResponse(5L, 5, "Prioridad", 2));

        VentanillaResponse r = ventanillaService.obtenerVentanilla(5L);

        assertEquals(5, r.getNumero());
        assertEquals("Prioridad", r.getDescripcion());
        assertEquals(2, r.getAsesorId());
    }

    @Test
    void obtenerVentanilla_inexistente_lanzaExcepcion() {
        when(ventanillaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> ventanillaService.obtenerVentanilla(99L));
    }

    @Test
    void actualizarVentanilla_existente_actualizaCampos() {
        Ventanilla existente = new Ventanilla();
        existente.setId(3L);
        existente.setNumero(1);
        existente.setDescripcion("Vieja");

        VentanillaRequest req = new VentanillaRequest();
        req.setNumero(2);
        req.setDescripcion("Nueva");

        when(ventanillaRepository.findById(3L)).thenReturn(Optional.of(existente));
        when(ventanillaRepository.save(existente)).thenReturn(existente);
        when(ventanillaMapper.toResponse(existente))
                .thenReturn(new VentanillaResponse(3L, 2, "Nueva", null));

        VentanillaResponse r = ventanillaService.actualizarVentanilla(3L, req);

        assertEquals(2, existente.getNumero());
        assertEquals("Nueva", existente.getDescripcion());
        assertEquals(2, r.getNumero());
    }

    @Test
    void actualizarVentanilla_inexistente_lanzaExcepcion() {
        when(ventanillaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> ventanillaService.actualizarVentanilla(99L, new VentanillaRequest()));
    }

    @Test
    void eliminarVentanilla_existente_elimina() {
        when(ventanillaRepository.existsById(4L)).thenReturn(true);

        ventanillaService.eliminarVentanilla(4L);

        verify(ventanillaRepository).deleteById(4L);
    }

    @Test
    void eliminarVentanilla_inexistente_lanzaExcepcion() {
        when(ventanillaRepository.existsById(99L)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> ventanillaService.eliminarVentanilla(99L));
        verify(ventanillaRepository, never()).deleteById(anyLong());
    }
}

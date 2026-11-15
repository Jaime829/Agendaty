package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.AsesorRequest;
import com.proyecto.agendaty.dto.AsesorResponse;
import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ventanilla;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.mapper.AsesorMapper;
import com.proyecto.agendaty.repository.AsesorRepository;
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
class AsesorServiceImpTest {

    @Mock
    private AsesorRepository asesorRepository;

    @Mock
    private VentanillaRepository ventanillaRepository;

    @Mock
    private AsesorMapper asesorMapper;

    @InjectMocks
    private AsesorServiceImp asesorService;

    private Ventanilla crearVentanilla(long id, int numero) {
        Ventanilla v = new Ventanilla();
        v.setId(id);
        v.setNumero(numero);
        v.setDescripcion("Test");
        return v;
    }

    @Test
    void crearAsesor_ventanillaInexistente_lanzaResourceNotFound() {
        when(ventanillaRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> asesorService.crearAsesor(new AsesorRequest(), 9L));
    }

    @Test
    void crearAsesor_ventanillaYaAsignada_lanzaBadRequest() {
        Ventanilla ventanilla = crearVentanilla(2L, 5);
        when(ventanillaRepository.findById(2L)).thenReturn(Optional.of(ventanilla));
        when(asesorRepository.existsByVentanillaActual(ventanilla)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> asesorService.crearAsesor(new AsesorRequest(), 2L));
    }

    @Test
    void crearAsesor_ok_guardaYRetornaResponse() {
        Ventanilla ventanilla = crearVentanilla(2L, 5);
        AsesorRequest req = new AsesorRequest();
        req.setNombre("María");

        Asesor entidad = new Asesor();
        entidad.setNombre("María");

        when(ventanillaRepository.findById(2L)).thenReturn(Optional.of(ventanilla));
        when(asesorRepository.existsByVentanillaActual(ventanilla)).thenReturn(false);
        when(asesorMapper.toEntity(req)).thenReturn(entidad);
        when(asesorRepository.save(entidad)).thenReturn(entidad);
        when(asesorMapper.toResponse(entidad)).thenReturn(new AsesorResponse(5, "María", true, 5));

        AsesorResponse r = asesorService.crearAsesor(req, 2L);

        assertEquals("María", r.getNombre());
        assertEquals(5, r.getNumeroVentanilla());
        assertEquals(ventanilla, entidad.getVentanillaActual());
    }

    @Test
    void listarAsesores_retornaTodosMapeados() {
        Asesor a1 = new Asesor();
        a1.setId(1);
        a1.setNombre("A1");
        a1.setDisponible(true);
        Asesor a2 = new Asesor();
        a2.setId(2);
        a2.setNombre("A2");
        a2.setDisponible(false);

        when(asesorRepository.findAll()).thenReturn(List.of(a1, a2));
        when(asesorMapper.toResponse(a1)).thenReturn(new AsesorResponse(1, "A1", true, 1));
        when(asesorMapper.toResponse(a2)).thenReturn(new AsesorResponse(2, "A2", false, 2));

        List<AsesorResponse> r = asesorService.listarAsesores();

        assertEquals(2, r.size());
    }

    @Test
    void obtenerAsesor_existente_retornaResponse() {
        Asesor a = new Asesor();
        a.setId(7);
        a.setNombre("A7");
        a.setDisponible(true);
        Ventanilla v = crearVentanilla(1L, 1);
        a.setVentanillaActual(v);

        when(asesorRepository.findById(7)).thenReturn(Optional.of(a));
        when(asesorMapper.toResponse(a)).thenReturn(new AsesorResponse(7, "A7", true, 1));

        AsesorResponse r = asesorService.obtenerAsesor(7);

        assertEquals("A7", r.getNombre());
        assertEquals(7, r.getId());
    }

    @Test
    void obtenerAsesor_inexistente_lanzaResourceNotFound() {
        when(asesorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> asesorService.obtenerAsesor(99));
    }

    @Test
    void cambiarDisponibilidad_existente_cambiaBandera() {
        Asesor a = new Asesor();
        a.setId(3);
        a.setNombre("A3");
        a.setDisponible(true);
        Ventanilla v = crearVentanilla(1L, 1);
        a.setVentanillaActual(v);

        when(asesorRepository.findById(3)).thenReturn(Optional.of(a));
        when(asesorRepository.save(a)).thenReturn(a);
        when(asesorMapper.toResponse(a)).thenReturn(new AsesorResponse(3, "A3", false, 1));

        AsesorResponse r = asesorService.cambiarDisponibilidad(3, false);

        assertEquals(false, a.getDisponible());
        assertEquals(false, r.getDisponible());
    }

    @Test
    void cambiarDisponibilidad_inexistente_lanzaResourceNotFound() {
        when(asesorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> asesorService.cambiarDisponibilidad(99, true));
    }
}

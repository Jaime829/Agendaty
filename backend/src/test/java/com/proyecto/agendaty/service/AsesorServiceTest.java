package com.proyecto.agendaty.service;

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
import com.proyecto.agendaty.service.impl.AsesorServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AsesorServiceTest {

    private AsesorRepository asesorRepository;
    private VentanillaRepository ventanillaRepository;
    private AsesorServiceImp service;

    @BeforeEach
    void setUp() {
        this.asesorRepository = mock(AsesorRepository.class);
        this.ventanillaRepository = mock(VentanillaRepository.class);
        AsesorMapper asesorMapper = new AsesorMapper();

        this.service = new AsesorServiceImp(
                asesorRepository,
                ventanillaRepository,
                asesorMapper
        );
    }

    @Test
    void crearDeberiaRetornarAsesorCreado() {
        AsesorRequest asesorRequest = new AsesorRequest();
        asesorRequest.setNombre("Test");

        Integer numeroVentanilla = 1;
        Ventanilla ventanillaGuardada = new Ventanilla(1L, numeroVentanilla, "Ventanilla 1", null);

        Asesor asesorGuardadoEnBD = new Asesor();
        asesorGuardadoEnBD.setId(1);
        asesorGuardadoEnBD.setNombre("Test");
        asesorGuardadoEnBD.setVentanillaActual(ventanillaGuardada);

        when(ventanillaRepository.findById(numeroVentanilla)).thenReturn(Optional.of(ventanillaGuardada));
        when(asesorRepository.existsByVentanillaActual(ventanillaGuardada)).thenReturn(false);
        when(asesorRepository.save(any(Asesor.class))).thenReturn(asesorGuardadoEnBD);

        AsesorResponse resultado = service.crearAsesor(asesorRequest, numeroVentanilla);

        assertNotNull(resultado, "El objeto devuelto no debe ser nulo");
        assertEquals(1, resultado.getId());
        assertEquals("Test", resultado.getNombre());
    }

    @Test
    void ventanillaInexistenteDebeLanzarExcepcion() {
        when(ventanillaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.crearAsesor(new AsesorRequest(), 1));
    }

    @Test
    void ventanillaYaOcupadaDebeLanzarExcepcion() {
        Ventanilla ventanillaDb = new Ventanilla(1L, 1, "Ventanilla 1", new Asesor());

        when(ventanillaRepository.findById(1)).thenReturn(Optional.of(ventanillaDb));
        when(asesorRepository.existsByVentanillaActual(ventanillaDb)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> service.crearAsesor(new AsesorRequest(), 1));

        verify(asesorRepository, never()).save(any());
    }
}

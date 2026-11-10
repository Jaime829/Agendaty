package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.CiudadanoRequest;
import com.proyecto.agendaty.dto.CiudadanoResponse;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.mapper.CiudadanoMapper;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CiudadanoServiceImpTest {

    // Objetos "falsos": no usan base de datos real
    @Mock
    private CiudadanoRepository ciudadanoRepository;

    @Mock
    private CiudadanoMapper ciudadanoMapper;

    // La clase que vamos a probar (Mockito le inyecta los mocks de arriba)
    @InjectMocks
    private CiudadanoServiceImp ciudadanoService;

    // PRUEBA 1: caso feliz -> se crea el ciudadano correctamente
    @Test
    void crearCiudadano_cuandoDocumentoNoExiste_deberiaGuardarYRetornarRespuesta() {
        // ARRANGE (preparar)
        CiudadanoRequest request = new CiudadanoRequest("Juan Perez", "12345678", "3001234567");
        Ciudadano entidad = new Ciudadano();
        CiudadanoResponse respuestaEsperada =
                new CiudadanoResponse(1, "Juan Perez", "12345678", "3001234567");

        when(ciudadanoRepository.existsByDocumento("12345678")).thenReturn(false);
        when(ciudadanoMapper.toEntity(request)).thenReturn(entidad);
        when(ciudadanoRepository.save(entidad)).thenReturn(entidad);
        when(ciudadanoMapper.toResponse(entidad)).thenReturn(respuestaEsperada);

        // ACT (ejecutar)
        CiudadanoResponse resultado = ciudadanoService.crearCiudadano(request);

        // ASSERT (verificar)
        assertEquals(respuestaEsperada, resultado);
        verify(ciudadanoRepository).save(entidad);
    }

    // PRUEBA 2: el documento ya existe -> debe lanzar BadRequestException
    @Test
    void crearCiudadano_cuandoDocumentoYaExiste_deberiaLanzarBadRequestException() {
        // ARRANGE
        CiudadanoRequest request = new CiudadanoRequest("Juan Perez", "12345678", "3001234567");
        when(ciudadanoRepository.existsByDocumento("12345678")).thenReturn(true);

        // ACT + ASSERT
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> ciudadanoService.crearCiudadano(request));

        assertEquals("Ya existe un ciudadano con ese documento", ex.getMessage());
        verify(ciudadanoRepository, never()).save(any()); // nunca debió guardar
    }

    // PRUEBA 3: buscar un id que no existe -> debe lanzar ResourceNotFoundException
    @Test
    void obtenerCiudadano_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        // ARRANGE
        when(ciudadanoRepository.findById(99)).thenReturn(Optional.empty());

        // ACT + ASSERT
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> ciudadanoService.obtenerCiudadano(99));

        assertEquals("Ciudadano 99 no encontrado", ex.getMessage());
    }
}
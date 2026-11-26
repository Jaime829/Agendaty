package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.AtencionRequest;
import com.proyecto.agendaty.dto.AtencionResponse;
import com.proyecto.agendaty.service.AtencionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtencionControllerTest {

    @Mock
    private AtencionService service;

    @InjectMocks
    private AtencionController controller;

    @Test
    void iniciar_deberiaRetornarAtencion() {
        AtencionResponse respuesta = new AtencionResponse(
                1L, 1L, "CEDN-00001", 7, "Maria Asesora",
                null, null, null, null, null, null, null);
        when(service.iniciarAtencion(1L, 7)).thenReturn(respuesta);

        ResponseEntity<AtencionResponse> response = controller.iniciar(1L, 7);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(7, response.getBody().asesorId());
    }

    @Test
    void finalizar_deberiaRetornarResultado() {
        AtencionResponse respuesta = new AtencionResponse(
                1L, 1L, "CEDN-00001", 7, "Maria Asesora",
                null, null, null, "Atendido", 5, 15, 0);
        when(service.finalizarAtencion(1L, 7,
                new AtencionRequest("Atendido", null))).thenReturn(respuesta);

        ResponseEntity<AtencionResponse> response = controller.finalizar(1L, 7,
                new AtencionRequest("Atendido", null));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Atendido", response.getBody().resultado());
        assertEquals(Integer.valueOf(5), response.getBody().tiempoEsperaMinutos());
    }

    @Test
    void pausar_deberiaRetornarAtencion() {
        AtencionResponse respuesta = new AtencionResponse(
                1L, 1L, "CEDN-00001", 7, "Maria Asesora",
                null, null, null, null, null, null, null);
        when(service.pausarAtencion(1L, 7)).thenReturn(respuesta);

        ResponseEntity<AtencionResponse> response = controller.pausar(1L, 7);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void reanudar_deberiaRetornarAtencion() {
        AtencionResponse respuesta = new AtencionResponse(
                1L, 1L, "CEDN-00001", 7, "Maria Asesora",
                null, null, null, null, null, null, null);
        when(service.reanudarAtencion(1L, 7)).thenReturn(respuesta);

        ResponseEntity<AtencionResponse> response = controller.reanudar(1L, 7);

        assertEquals(200, response.getStatusCode().value());
    }
}

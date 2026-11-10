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
    private AtencionService atencionService;

    @InjectMocks
    private AtencionController atencionController;

    @Test
    void iniciarAtencion_deberiaRetornarAtencion() {
        AtencionResponse respuesta = new AtencionResponse(
                1L, 1L, "CEDN-00001", 7, "Maria Asesora",
                null, null, null, null, null, null, null);
        when(atencionService.iniciarAtencion(1L, 7)).thenReturn(respuesta);

        ResponseEntity<AtencionResponse> response = atencionController.iniciarAtencion(1L, 7);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(7, response.getBody().asesorId());
        assertEquals("CEDN-00001", response.getBody().numeroTurno());
    }

    @Test
    void finalizarAtencion_deberiaRetornarResultadoYTiempos() {
        AtencionResponse respuesta = new AtencionResponse(
                1L, 1L, "CEDN-00001", 7, "Maria Asesora",
                null, null, null, "Atendido", 5, 15, 0);
        when(atencionService.finalizarAtencion(1L, 7,
                new AtencionRequest("Atendido", null))).thenReturn(respuesta);

        ResponseEntity<AtencionResponse> response = atencionController.finalizarAtencion(1L, 7,
                new AtencionRequest("Atendido", null));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Atendido", response.getBody().resultado());
        assertEquals(Integer.valueOf(5), response.getBody().tiempoEsperaMinutos());
        assertEquals(Integer.valueOf(15), response.getBody().tiempoAtencionMinutos());
    }

    @Test
    void pausarAtencion_deberiaRetornarAtencion() {
        AtencionResponse respuesta = new AtencionResponse(
                1L, 1L, "CEDN-00001", 7, "Maria Asesora",
                null, null, null, null, null, null, null);
        when(atencionService.pausarAtencion(1L, 7)).thenReturn(respuesta);

        ResponseEntity<AtencionResponse> response = atencionController.pausarAtencion(1L, 7);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void reanudarAtencion_deberiaRetornarAtencion() {
        AtencionResponse respuesta = new AtencionResponse(
                1L, 1L, "CEDN-00001", 7, "Maria Asesora",
                null, null, null, null, null, null, null);
        when(atencionService.reanudarAtencion(1L, 7)).thenReturn(respuesta);

        ResponseEntity<AtencionResponse> response = atencionController.reanudarAtencion(1L, 7);

        assertEquals(200, response.getStatusCode().value());
    }
}

package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.TurnoRequest;
import com.proyecto.agendaty.dto.TurnoResponse;
import com.proyecto.agendaty.model.enums.EstadoTurno;
import com.proyecto.agendaty.model.enums.PrioridadTurno;
import com.proyecto.agendaty.service.TurnoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnoControllerTest {

    @Mock
    private TurnoService service;

    @InjectMocks
    private TurnoController controller;

    @Test
    void generar_deberiaRetornar201_yTurno() {
        TurnoRequest request = new TurnoRequest(1, 10L, false);
        TurnoResponse respuesta = new TurnoResponse(
                1L, "CEDN-00001", EstadoTurno.EN_ESPERA, PrioridadTurno.NORMAL,
                null, null, null, 1, "Juan Perez", 10L, "Cedula", null, null);
        when(service.generarTurno(request, 1)).thenReturn(respuesta);

        ResponseEntity<TurnoResponse> response = controller.generar(1, request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("CEDN-00001", response.getBody().numeroTurno());
        assertEquals(EstadoTurno.EN_ESPERA, response.getBody().estado());
    }

    @Test
    void llamarSiguiente_deberiaRetornarTurnoLlamado() {
        TurnoResponse respuesta = new TurnoResponse(
                1L, "CEDN-00001", EstadoTurno.LLAMADO, PrioridadTurno.NORMAL,
                null, null, null, 1, "Juan Perez", 10L, "Cedula", 7, "Maria Asesora");
        when(service.llamarSiguiente(7)).thenReturn(respuesta);

        ResponseEntity<TurnoResponse> response = controller.llamarSiguiente(7);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(EstadoTurno.LLAMADO, response.getBody().estado());
    }

    @Test
    void cancelar_deberiaRetornarCancelado() {
        TurnoResponse respuesta = new TurnoResponse(
                1L, "CEDN-00001", EstadoTurno.CANCELADO, PrioridadTurno.NORMAL,
                null, null, null, 1, "Juan Perez", 10L, "Cedula", null, null);
        when(service.cancelarTurno(1L, 1)).thenReturn(respuesta);

        ResponseEntity<TurnoResponse> response = controller.cancelar(1L, 1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(EstadoTurno.CANCELADO, response.getBody().estado());
    }

    @Test
    void marcarNoAtendido_deberiaRetornarNoAtendido() {
        TurnoResponse respuesta = new TurnoResponse(
                1L, "CEDN-00001", EstadoTurno.NO_ATENDIDO, PrioridadTurno.NORMAL,
                null, null, null, 1, "Juan Perez", 10L, "Cedula", null, null);
        when(service.marcarNoAtendido(1L)).thenReturn(respuesta);

        ResponseEntity<TurnoResponse> response = controller.marcarNoAtendido(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(EstadoTurno.NO_ATENDIDO, response.getBody().estado());
    }

    @Test
    void listar_deberiaRetornarLista() {
        when(service.listarTurnos(null, null, null, null)).thenReturn(java.util.List.of());

        ResponseEntity<java.util.List<TurnoResponse>> response = controller.listar(null, null, null, null);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
    }
}

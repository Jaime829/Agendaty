package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.TurnoRequest;
import com.proyecto.agendaty.dto.TurnoResponse;
import com.proyecto.agendaty.model.enums.EstadoTurno;
import com.proyecto.agendaty.model.enums.PrioridadTurno;
import com.proyecto.agendaty.service.TurnoService;
import org.junit.jupiter.api.BeforeEach;
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
    private TurnoService turnoService;

    @InjectMocks
    private TurnoController turnoController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void generarTurno_deberiaRetornar201_yTurno() {
        TurnoRequest request = new TurnoRequest(1, 10L, false);
        TurnoResponse respuesta = new TurnoResponse(
                1L, "CEDN-00001", EstadoTurno.EN_ESPERA, PrioridadTurno.NORMAL,
                null, null, null, 1, "Juan Perez", 10L, "Cedula", null, null);
        when(turnoService.generarTurno(request, 1)).thenReturn(respuesta);

        ResponseEntity<TurnoResponse> response = turnoController.generarTurno(1, request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("CEDN-00001", response.getBody().numeroTurno());
        assertEquals(EstadoTurno.EN_ESPERA, response.getBody().estado());
    }

    @Test
    void llamarSiguiente_deberiaRetornarTurnoLlamado() {
        TurnoResponse respuesta = new TurnoResponse(
                1L, "CEDN-00001", EstadoTurno.LLAMADO, PrioridadTurno.NORMAL,
                null, null, null, 1, "Juan Perez", 10L, "Cedula", 7, "Maria Asesora");
        when(turnoService.llamarSiguiente(7)).thenReturn(respuesta);

        ResponseEntity<TurnoResponse> response = turnoController.llamarSiguiente(7);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(EstadoTurno.LLAMADO, response.getBody().estado());
        assertEquals(7, response.getBody().asesorId());
    }

    @Test
    void cancelarTurno_deberiaRetornarCancelado() {
        TurnoResponse respuesta = new TurnoResponse(
                1L, "CEDN-00001", EstadoTurno.CANCELADO, PrioridadTurno.NORMAL,
                null, null, null, 1, "Juan Perez", 10L, "Cedula", null, null);
        when(turnoService.cancelarTurno(1L, 1)).thenReturn(respuesta);

        ResponseEntity<TurnoResponse> response = turnoController.cancelarTurno(1L, 1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(EstadoTurno.CANCELADO, response.getBody().estado());
    }

    @Test
    void marcarNoAtendido_deberiaRetornarNoAtendido() {
        TurnoResponse respuesta = new TurnoResponse(
                1L, "CEDN-00001", EstadoTurno.NO_ATENDIDO, PrioridadTurno.NORMAL,
                null, null, null, 1, "Juan Perez", 10L, "Cedula", null, null);
        when(turnoService.marcarNoAtendido(1L)).thenReturn(respuesta);

        ResponseEntity<TurnoResponse> response = turnoController.marcarNoAtendido(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(EstadoTurno.NO_ATENDIDO, response.getBody().estado());
    }

    @Test
    void listarTurnos_deberiaRetornarLista() {
        when(turnoService.listarTurnos(null, null, null, null)).thenReturn(java.util.List.of());

        ResponseEntity<java.util.List<TurnoResponse>> response = turnoController.listarTurnos(null, null, null, null);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
    }
}

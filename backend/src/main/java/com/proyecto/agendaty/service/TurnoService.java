package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.TurnoRequest;
import com.proyecto.agendaty.dto.TurnoResponse;

import java.util.List;

public interface TurnoService {
    TurnoResponse generarTurno(TurnoRequest request, Integer usuarioId);
    TurnoResponse llamarSiguiente(Integer asesorId);
    TurnoResponse cancelarTurno(Long turnoId, Integer usuarioId);
    TurnoResponse marcarNoAtendido(Long turnoId);
    TurnoResponse obtenerTurno(Long turnoId);
    List<TurnoResponse> listarTurnos(String estado, String prioridad, Integer ciudadanoId, Long tipoTramiteId);
}

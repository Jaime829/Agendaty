package com.proyecto.agendaty.dto;

import com.proyecto.agendaty.model.enums.EstadoTurno;
import com.proyecto.agendaty.model.enums.PrioridadTurno;

import java.time.LocalDateTime;

public record TurnoResponse(
        Long id,
        String numeroTurno,
        EstadoTurno estado,
        PrioridadTurno prioridad,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaLlamado,
        LocalDateTime fechaFinEspera,
        Integer ciudadanoId,
        String ciudadanoNombre,
        Long tipoTramiteId,
        String tipoTramiteNombre,
        Integer asesorId,
        String asesorNombre
) {
}

package com.proyecto.agendaty.dto;

import java.time.LocalDateTime;

public record AtencionResponse(
        Long id,
        Long turnoId,
        String numeroTurno,
        Integer asesorId,
        String asesorNombre,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        String observaciones,
        String resultado,
        Integer tiempoEsperaMinutos,
        Integer tiempoAtencionMinutos,
        Integer totalPausasMinutos
) {
}

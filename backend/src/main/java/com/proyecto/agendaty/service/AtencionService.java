package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.AtencionRequest;
import com.proyecto.agendaty.dto.AtencionResponse;

public interface AtencionService {
    AtencionResponse iniciarAtencion(Long turnoId, Integer asesorId);
    AtencionResponse pausarAtencion(Long turnoId, Integer asesorId);
    AtencionResponse reanudarAtencion(Long turnoId, Integer asesorId);
    AtencionResponse finalizarAtencion(Long turnoId, Integer asesorId, AtencionRequest request);
    AtencionResponse obtenerAtencionPorTurno(Long turnoId);
}

package com.proyecto.agendaty.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReporteService {
    Map<String, Object> tiempoEsperaPromedio(LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                             Integer asesorId, Long tipoTramiteId);
    Map<String, Object> turnosAtendidos(LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                        Integer asesorId, Long tipoTramiteId);
    List<Map<String, Object>> tramitesFrecuentes(LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                                 Long tipoTramiteId);
}

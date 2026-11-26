package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.entity.Atencion;
import com.proyecto.agendaty.entity.Turno;
import com.proyecto.agendaty.model.enums.EstadoTurno;
import com.proyecto.agendaty.repository.AtencionRepository;
import com.proyecto.agendaty.repository.TurnoRepository;
import com.proyecto.agendaty.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImp implements ReporteService {

    private final AtencionRepository atencionRepository;
    private final TurnoRepository turnoRepository;

    @Override
    public Map<String, Object> tiempoEsperaPromedio(LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                                    Integer asesorId, Long tipoTramiteId) {
        List<Atencion> atenciones = atencionRepository.findAll().stream()
                .filter(a -> a.getTiempoEsperaMinutos() != null)
                .filter(a -> fechaDesde == null || !a.getFechaInicio().isBefore(fechaDesde))
                .filter(a -> fechaHasta == null || !a.getFechaInicio().isAfter(fechaHasta))
                .filter(a -> asesorId == null || a.getAsesor().getId().equals(asesorId))
                .filter(a -> tipoTramiteId == null
                        || a.getTurno().getTipoTramite().getId().equals(tipoTramiteId))
                .collect(Collectors.toList());

        double promedio = atenciones.isEmpty()
                ? 0.0
                : atenciones.stream().mapToInt(Atencion::getTiempoEsperaMinutos).average().orElse(0.0);

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("metrica", "tiempo_espera_promedio_minutos");
        resultado.put("valor", Math.round(promedio * 100.0) / 100.0);
        resultado.put("cantidad_atenciones", atenciones.size());
        resultado.put("fecha_desde", fechaDesde);
        resultado.put("fecha_hasta", fechaHasta);
        return resultado;
    }

    @Override
    public Map<String, Object> turnosAtendidos(LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                               Integer asesorId, Long tipoTramiteId) {
        List<Turno> turnos = turnoRepository.findAll().stream()
                .filter(t -> fechaDesde == null || !t.getFechaCreacion().isBefore(fechaDesde))
                .filter(t -> fechaHasta == null || !t.getFechaCreacion().isAfter(fechaHasta))
                .filter(t -> asesorId == null || (t.getAsesor() != null && t.getAsesor().getId().equals(asesorId)))
                .filter(t -> tipoTramiteId == null || t.getTipoTramite().getId().equals(tipoTramiteId))
                .filter(t -> t.getEstado() == EstadoTurno.FINALIZADO || t.getEstado() == EstadoTurno.NO_ATENDIDO)
                .collect(Collectors.toList());

        long atendidos = turnos.stream().filter(t -> t.getEstado() == EstadoTurno.FINALIZADO).count();
        long noAtendidos = turnos.stream().filter(t -> t.getEstado() == EstadoTurno.NO_ATENDIDO).count();

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("metrica", "turnos_atendidos");
        resultado.put("atendidos", atendidos);
        resultado.put("no_atendidos", noAtendidos);
        resultado.put("total", atendidos + noAtendidos);
        resultado.put("fecha_desde", fechaDesde);
        resultado.put("fecha_hasta", fechaHasta);
        return resultado;
    }

    @Override
    public List<Map<String, Object>> tramitesFrecuentes(LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                                        Long tipoTramiteId) {
        List<Turno> turnos = turnoRepository.findAll().stream()
                .filter(t -> fechaDesde == null || !t.getFechaCreacion().isBefore(fechaDesde))
                .filter(t -> fechaHasta == null || !t.getFechaCreacion().isAfter(fechaHasta))
                .filter(t -> tipoTramiteId == null || t.getTipoTramite().getId().equals(tipoTramiteId))
                .collect(Collectors.toList());

        Map<Long, Map<String, Object>> agrupado = turnos.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTipoTramite().getId(),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                lista -> {
                                    Map<String, Object> m = new LinkedHashMap<>();
                                    m.put("tipo_tramite_id", lista.get(0).getTipoTramite().getId());
                                    m.put("tipo_tramite_nombre", lista.get(0).getTipoTramite().getNombre());
                                    m.put("cantidad", lista.size());
                                    return m;
                                }
                        )
                ));

        return agrupado.values().stream()
                .sorted((a, b) -> Integer.compare(
                        (Integer) b.get("cantidad"),
                        (Integer) a.get("cantidad")))
                .collect(Collectors.toList());
    }
}

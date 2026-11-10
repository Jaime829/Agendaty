package com.proyecto.agendaty.mapper;

import com.proyecto.agendaty.dto.AtencionRequest;
import com.proyecto.agendaty.dto.AtencionResponse;
import com.proyecto.agendaty.entity.Atencion;
import org.springframework.stereotype.Component;

@Component
public class AtencionMapper {

    public AtencionResponse toResponse(Atencion atencion) {
        if (atencion == null) return null;
        return new AtencionResponse(
                atencion.getId(),
                atencion.getTurno() != null ? atencion.getTurno().getId() : null,
                atencion.getTurno() != null ? atencion.getTurno().getNumeroTurno() : null,
                atencion.getAsesor() != null ? atencion.getAsesor().getId() : null,
                atencion.getAsesor() != null ? atencion.getAsesor().getNombre() : null,
                atencion.getFechaInicio(),
                atencion.getFechaFin(),
                atencion.getObservaciones(),
                atencion.getResultado(),
                atencion.getTiempoEsperaMinutos(),
                atencion.getTiempoAtencionMinutos(),
                atencion.getTotalPausasMinutos()
        );
    }
}

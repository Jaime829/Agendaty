package com.proyecto.agendaty.mapper;

import com.proyecto.agendaty.dto.TurnoRequest;
import com.proyecto.agendaty.dto.TurnoResponse;
import com.proyecto.agendaty.entity.Turno;
import com.proyecto.agendaty.model.enums.PrioridadTurno;
import org.springframework.stereotype.Component;

@Component
public class TurnoMapper {

    public Turno toEntity(TurnoRequest request) {
        if (request == null) return null;
        Turno turno = new Turno();
        turno.setPrioridad(request.prioritario() != null && request.prioritario()
                ? PrioridadTurno.PRIORITARIO : PrioridadTurno.NORMAL);
        return turno;
    }

    public TurnoResponse toResponse(Turno turno) {
        if (turno == null) return null;
        return new TurnoResponse(
                turno.getId(),
                turno.getNumeroTurno(),
                turno.getEstado(),
                turno.getPrioridad(),
                turno.getFechaCreacion(),
                turno.getFechaLlamado(),
                turno.getFechaFinEspera(),
                turno.getCiudadano() != null ? turno.getCiudadano().getId() : null,
                turno.getCiudadano() != null ? turno.getCiudadano().getNombre() : null,
                turno.getTipoTramite() != null ? turno.getTipoTramite().getId() : null,
                turno.getTipoTramite() != null ? turno.getTipoTramite().getNombre() : null,
                turno.getAsesor() != null ? turno.getAsesor().getId() : null,
                turno.getAsesor() != null ? turno.getAsesor().getNombre() : null
        );
    }
}

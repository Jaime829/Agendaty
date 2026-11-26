package com.proyecto.agendaty.mapper;

import com.proyecto.agendaty.dto.AsesorRequest;
import com.proyecto.agendaty.dto.AsesorResponse;
import com.proyecto.agendaty.entity.Asesor;
import org.springframework.stereotype.Component;

@Component
public class AsesorMapper {

    public AsesorResponse toResponse(Asesor asesor) {

        if (asesor == null) return null;

        return new AsesorResponse(asesor.getId(), asesor.getNombre(), asesor.getDisponible(), asesor.getVentanillaActual().getNumero());
    }

    public Asesor toEntity(AsesorRequest asesorRequest) {
        if (asesorRequest == null) return null;

        Asesor asesor = new Asesor();
        asesor.setNombre(asesorRequest.getNombre());

        return asesor;
    }
}

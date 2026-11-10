package com.proyecto.agendaty.mapper;

import com.proyecto.agendaty.dto.CiudadanoRequest;
import com.proyecto.agendaty.dto.CiudadanoResponse;
import com.proyecto.agendaty.entity.Ciudadano;
import org.springframework.stereotype.Component;

@Component
public class CiudadanoMapper {

    public Ciudadano toEntity(CiudadanoRequest request) {
        Ciudadano c = new Ciudadano();
        c.setNombre(request.nombre());
        c.setDocumento(request.documento());
        c.setTelefono(request.telefono());
        return c;
    }

    public CiudadanoResponse toResponse(Ciudadano c) {
        return new CiudadanoResponse(c.getId(), c.getNombre(), c.getDocumento(), c.getTelefono());
    }
}
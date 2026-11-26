package com.proyecto.agendaty.mapper;

import com.proyecto.agendaty.dto.TipoTramiteRequest;
import com.proyecto.agendaty.dto.TipoTramiteResponse;
import com.proyecto.agendaty.entity.TipoTramite;
import org.springframework.stereotype.Component;

@Component
public class TipoTramiteMapper {

    public TipoTramite toEntity(TipoTramiteRequest request) {
        if (request == null) return null;
        TipoTramite tipo = new TipoTramite();
        tipo.setNombre(request.nombre());
        tipo.setCodigo(request.codigo());
        tipo.setEsPrioritarioPorDefecto(request.esPrioritarioPorDefecto() != null ? request.esPrioritarioPorDefecto() : false);
        tipo.setActivo(request.activo() != null ? request.activo() : true);
        return tipo;
    }

    public TipoTramiteResponse toResponse(TipoTramite tipo) {
        if (tipo == null) return null;
        return new TipoTramiteResponse(
                tipo.getId(),
                tipo.getNombre(),
                tipo.getCodigo(),
                tipo.getEsPrioritarioPorDefecto(),
                tipo.getActivo()
        );
    }
}

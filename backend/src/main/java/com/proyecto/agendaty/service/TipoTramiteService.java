package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.TipoTramiteRequest;
import com.proyecto.agendaty.dto.TipoTramiteResponse;

import java.util.List;

public interface TipoTramiteService {
    TipoTramiteResponse crear(TipoTramiteRequest request);
    TipoTramiteResponse obtener(Long id);
    List<TipoTramiteResponse> listar();
    TipoTramiteResponse actualizar(Long id, TipoTramiteRequest request);
    void eliminar(Long id);
}

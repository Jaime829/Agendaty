package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.CiudadanoRequest;
import com.proyecto.agendaty.dto.CiudadanoResponse;
import com.proyecto.agendaty.dto.CiudadanoUpdateRequest;

import java.util.List;

public interface CiudadanoService {

    CiudadanoResponse crearCiudadano(CiudadanoRequest request);

    CiudadanoResponse obtenerCiudadano(Integer id);

    List<CiudadanoResponse> listarCiudadanos();

    CiudadanoResponse actualizarCiudadano(Integer id, CiudadanoUpdateRequest request);
}
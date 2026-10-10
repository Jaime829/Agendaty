package com.proyecto.agendaty.mapper;

import com.proyecto.agendaty.dto.VentanillaRequest;
import com.proyecto.agendaty.dto.VentanillaResponse;
import com.proyecto.agendaty.entity.Ventanilla;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VentanillaMapper {

    public VentanillaResponse toResponse(Ventanilla ventanilla) {

        if(ventanilla == null) return null;

        Integer asesorId = ventanilla.getAsesorActual() != null ? ventanilla.getAsesorActual().getId() : null;

        return new VentanillaResponse(ventanilla.getId(), ventanilla.getNumero(), ventanilla.getDescripcion(),asesorId);
    }

    public Ventanilla toEntity(VentanillaRequest ventanillaRequest) {
        Ventanilla ventanilla = new Ventanilla();
        ventanilla.setNumero(ventanillaRequest.getNumero());
        ventanilla.setDescripcion(ventanillaRequest.getDescripcion());

        return ventanilla;
    }

    public List<VentanillaResponse> toListResponse(List<Ventanilla> ventanillaList) {
        if(ventanillaList == null) return List.of();

        return ventanillaList.stream()
                .map(this::toResponse)
                .toList();

    }

}

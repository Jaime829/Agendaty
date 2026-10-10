package com.proyecto.agendaty.mapper;

import com.proyecto.agendaty.dto.VentanillaDTO;
import com.proyecto.agendaty.entity.Ventanilla;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VentanillaMapper {
    VentanillaDTO toResponse(Ventanilla ventanilla);
    Ventanilla toEntity(VentanillaDTO ventanillaDTO);
    List<VentanillaDTO> toResponseList(List<Ventanilla> ventanillas);
}

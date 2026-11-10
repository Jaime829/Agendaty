package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.AsesorResponse;
import com.proyecto.agendaty.service.AsesorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asesores")
@RequiredArgsConstructor
public class AsesorController {

    private final AsesorService asesorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ASESOR', 'RECEPCIONISTA', 'COORDINADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<AsesorResponse>> listarAsesores() {
        return ResponseEntity.ok(asesorService.listarAsesores());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ASESOR', 'RECEPCIONISTA', 'COORDINADOR', 'ADMINISTRADOR')")
    public ResponseEntity<AsesorResponse> obtenerAsesor(@PathVariable Integer id) {
        return ResponseEntity.ok(asesorService.obtenerAsesor(id));
    }

    @PutMapping("/{id}/disponible")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<AsesorResponse> cambiarDisponibilidad(
            @PathVariable Integer id,
            @RequestParam Boolean disponible) {
        return ResponseEntity.ok(asesorService.cambiarDisponibilidad(id, disponible));
    }
}

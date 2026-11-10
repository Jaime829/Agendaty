package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.AtencionRequest;
import com.proyecto.agendaty.dto.AtencionResponse;
import com.proyecto.agendaty.service.AtencionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/atenciones")
@RequiredArgsConstructor
public class AtencionController {

    private final AtencionService atencionService;

    @PutMapping("/iniciar")
    @PreAuthorize("hasRole('ASESOR')")
    public ResponseEntity<AtencionResponse> iniciarAtencion(
            @RequestParam Long turnoId,
            @RequestParam Integer asesorId) {
        return ResponseEntity.ok(atencionService.iniciarAtencion(turnoId, asesorId));
    }

    @PutMapping("/{turnoId}/pausar")
    @PreAuthorize("hasRole('ASESOR')")
    public ResponseEntity<AtencionResponse> pausarAtencion(
            @PathVariable Long turnoId,
            @RequestParam Integer asesorId) {
        return ResponseEntity.ok(atencionService.pausarAtencion(turnoId, asesorId));
    }

    @PutMapping("/{turnoId}/reanudar")
    @PreAuthorize("hasRole('ASESOR')")
    public ResponseEntity<AtencionResponse> reanudarAtencion(
            @PathVariable Long turnoId,
            @RequestParam Integer asesorId) {
        return ResponseEntity.ok(atencionService.reanudarAtencion(turnoId, asesorId));
    }

    @PutMapping("/{turnoId}/finalizar")
    @PreAuthorize("hasRole('ASESOR')")
    public ResponseEntity<AtencionResponse> finalizarAtencion(
            @PathVariable Long turnoId,
            @RequestParam Integer asesorId,
            @Valid @RequestBody AtencionRequest request) {
        return ResponseEntity.ok(atencionService.finalizarAtencion(turnoId, asesorId, request));
    }

    @GetMapping("/turno/{turnoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AtencionResponse> obtenerAtencionPorTurno(@PathVariable Long turnoId) {
        return ResponseEntity.ok(atencionService.obtenerAtencionPorTurno(turnoId));
    }
}

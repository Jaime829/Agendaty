package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.TurnoRequest;
import com.proyecto.agendaty.dto.TurnoResponse;
import com.proyecto.agendaty.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'ADMINISTRADOR')")
    public ResponseEntity<TurnoResponse> generarTurno(
            @RequestParam Integer usuarioId,
            @Valid @RequestBody TurnoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.generarTurno(request, usuarioId));
    }

    @PutMapping("/llamar-siguiente")
    @PreAuthorize("hasRole('ASESOR')")
    public ResponseEntity<TurnoResponse> llamarSiguiente(@RequestParam Integer asesorId) {
        return ResponseEntity.ok(turnoService.llamarSiguiente(asesorId));
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'ADMINISTRADOR')")
    public ResponseEntity<TurnoResponse> cancelarTurno(
            @PathVariable Long id,
            @RequestParam Integer usuarioId) {
        return ResponseEntity.ok(turnoService.cancelarTurno(id, usuarioId));
    }

    @PutMapping("/{id}/no-atendido")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'ADMINISTRADOR')")
    public ResponseEntity<TurnoResponse> marcarNoAtendido(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.marcarNoAtendido(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'ASESOR', 'COORDINADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<TurnoResponse>> listarTurnos(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) Integer ciudadanoId,
            @RequestParam(required = false) Long tipoTramiteId) {
        return ResponseEntity.ok(turnoService.listarTurnos(estado, prioridad, ciudadanoId, tipoTramiteId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TurnoResponse> obtenerTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.obtenerTurno(id));
    }
}

package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.TurnoRequest;
import com.proyecto.agendaty.dto.TurnoResponse;
import com.proyecto.agendaty.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/turnos")
@CrossOrigin(origins = "http://localhost:5173")
public class TurnoController {

    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TurnoResponse> generar(@RequestParam Integer usuarioId,
                                                 @Valid @RequestBody TurnoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generarTurno(request, usuarioId));
    }

    @PutMapping("/llamar-siguiente")
    public ResponseEntity<TurnoResponse> llamarSiguiente(@RequestParam Integer asesorId) {
        return ResponseEntity.ok(service.llamarSiguiente(asesorId));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<TurnoResponse> cancelar(@PathVariable Long id,
                                                  @RequestParam Integer usuarioId) {
        return ResponseEntity.ok(service.cancelarTurno(id, usuarioId));
    }

    @PutMapping("/{id}/no-atendido")
    public ResponseEntity<TurnoResponse> marcarNoAtendido(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarNoAtendido(id));
    }

    @GetMapping
    public ResponseEntity<List<TurnoResponse>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) Integer ciudadanoId,
            @RequestParam(required = false) Long tipoTramiteId) {
        return ResponseEntity.ok(service.listarTurnos(estado, prioridad, ciudadanoId, tipoTramiteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerTurno(id));
    }
}

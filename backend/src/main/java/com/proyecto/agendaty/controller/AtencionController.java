package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.AtencionRequest;
import com.proyecto.agendaty.dto.AtencionResponse;
import com.proyecto.agendaty.service.AtencionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atenciones")
@CrossOrigin(origins = "http://localhost:5173")
public class AtencionController {

    private final AtencionService service;

    public AtencionController(AtencionService service) {
        this.service = service;
    }

    @PutMapping("/iniciar")
    public ResponseEntity<AtencionResponse> iniciar(@RequestParam Long turnoId,
                                                    @RequestParam Integer asesorId) {
        return ResponseEntity.ok(service.iniciarAtencion(turnoId, asesorId));
    }

    @PutMapping("/{turnoId}/pausar")
    public ResponseEntity<AtencionResponse> pausar(@PathVariable Long turnoId,
                                                   @RequestParam Integer asesorId) {
        return ResponseEntity.ok(service.pausarAtencion(turnoId, asesorId));
    }

    @PutMapping("/{turnoId}/reanudar")
    public ResponseEntity<AtencionResponse> reanudar(@PathVariable Long turnoId,
                                                     @RequestParam Integer asesorId) {
        return ResponseEntity.ok(service.reanudarAtencion(turnoId, asesorId));
    }

    @PutMapping("/{turnoId}/finalizar")
    public ResponseEntity<AtencionResponse> finalizar(@PathVariable Long turnoId,
                                                      @RequestParam Integer asesorId,
                                                      @Valid @RequestBody AtencionRequest request) {
        return ResponseEntity.ok(service.finalizarAtencion(turnoId, asesorId, request));
    }

    @GetMapping("/turno/{turnoId}")
    public ResponseEntity<AtencionResponse> obtenerPorTurno(@PathVariable Long turnoId) {
        return ResponseEntity.ok(service.obtenerAtencionPorTurno(turnoId));
    }
}

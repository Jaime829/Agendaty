package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "http://localhost:5173")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @GetMapping("/tiempo-espera-promedio")
    public ResponseEntity<Map<String, Object>> tiempoEsperaPromedio(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) Integer asesorId,
            @RequestParam(required = false) Long tipoTramiteId) {
        return ResponseEntity.ok(service.tiempoEsperaPromedio(fechaDesde, fechaHasta, asesorId, tipoTramiteId));
    }

    @GetMapping("/turnos-atendidos")
    public ResponseEntity<Map<String, Object>> turnosAtendidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) Integer asesorId,
            @RequestParam(required = false) Long tipoTramiteId) {
        return ResponseEntity.ok(service.turnosAtendidos(fechaDesde, fechaHasta, asesorId, tipoTramiteId));
    }

    @GetMapping("/tramites-frecuentes")
    public ResponseEntity<List<Map<String, Object>>> tramitesFrecuentes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) Long tipoTramiteId) {
        return ResponseEntity.ok(service.tramitesFrecuentes(fechaDesde, fechaHasta, tipoTramiteId));
    }
}

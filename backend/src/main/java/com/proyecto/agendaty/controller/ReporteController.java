package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/tiempo-espera-promedio")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> tiempoEsperaPromedio(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) Integer asesorId,
            @RequestParam(required = false) Long tipoTramiteId) {
        return ResponseEntity.ok(reporteService.tiempoEsperaPromedio(fechaDesde, fechaHasta, asesorId, tipoTramiteId));
    }

    @GetMapping("/turnos-atendidos")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> turnosAtendidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) Integer asesorId,
            @RequestParam(required = false) Long tipoTramiteId) {
        return ResponseEntity.ok(reporteService.turnosAtendidos(fechaDesde, fechaHasta, asesorId, tipoTramiteId));
    }

    @GetMapping("/tramites-frecuentes")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<Map<String, Object>>> tramitesFrecuentes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) Long tipoTramiteId) {
        return ResponseEntity.ok(reporteService.tramitesFrecuentes(fechaDesde, fechaHasta, tipoTramiteId));
    }
}

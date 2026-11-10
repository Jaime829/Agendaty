package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.AsesorResponse;
import com.proyecto.agendaty.service.AsesorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/asesores")
@CrossOrigin(origins = "http://localhost:5173")
public class AsesorController {

    private final AsesorService service;

    public AsesorController(AsesorService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AsesorResponse>> listar() {
        return ResponseEntity.ok(service.listarAsesores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsesorResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerAsesor(id));
    }

    @PutMapping("/{id}/disponible")
    public ResponseEntity<AsesorResponse> cambiarDisponibilidad(@PathVariable Integer id,
                                                                @RequestParam Boolean disponible) {
        return ResponseEntity.ok(service.cambiarDisponibilidad(id, disponible));
    }
}

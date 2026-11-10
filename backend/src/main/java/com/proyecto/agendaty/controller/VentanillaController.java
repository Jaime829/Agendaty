package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.VentanillaRequest;
import com.proyecto.agendaty.dto.VentanillaResponse;
import com.proyecto.agendaty.service.VentanillaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ventanillas")
@CrossOrigin(origins = "http://localhost:5173")
public class VentanillaController {

    private final VentanillaService service;

    public VentanillaController(VentanillaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VentanillaResponse> crear(@Valid @RequestBody VentanillaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearVentanilla(request));
    }

    @GetMapping
    public ResponseEntity<List<VentanillaResponse>> listar() {
        return ResponseEntity.ok(service.listarVentanillas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentanillaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerVentanilla(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentanillaResponse> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody VentanillaRequest request) {
        return ResponseEntity.ok(service.actualizarVentanilla(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarVentanilla(id);
        return ResponseEntity.noContent().build();
    }
}

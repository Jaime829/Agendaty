package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.VentanillaDTO;
import com.proyecto.agendaty.service.VentanillaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventanillas")
@RequiredArgsConstructor
public class VentanillaController {

    private final VentanillaService service;

    @PostMapping()
    public ResponseEntity<?> crearVentanilla(@Valid @RequestBody VentanillaDTO ventanilla) {
        VentanillaDTO ventanillaSaved = service.crearVentanilla(ventanilla);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventanillaSaved);
    }

    @GetMapping()
    public ResponseEntity<List<VentanillaDTO>> listarVentanillas() {
        List<VentanillaDTO> ventanillaList = service.listarVentanillas();

        return ResponseEntity.ok(ventanillaList);
    }

}

package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.VentanillaRequest;
import com.proyecto.agendaty.dto.VentanillaResponse;
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
    public ResponseEntity<VentanillaResponse> crearVentanilla(@Valid @RequestBody VentanillaRequest ventanilla) {
        VentanillaResponse ventanillaSaved = service.crearVentanilla(ventanilla);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventanillaSaved);
    }

    @GetMapping()
    public ResponseEntity<List<VentanillaResponse>> listarVentanillas() {
        List<VentanillaResponse> ventanillaList = service.listarVentanillas();

        return ResponseEntity.ok(ventanillaList);
    }

}

package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.entity.Ventanilla;
import com.proyecto.agendaty.service.VentanillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ventanillas")
@RequiredArgsConstructor
public class VentanillaController {

    private final VentanillaService service;

    @PostMapping()
    public ResponseEntity<?> crearVentanilla(@RequestBody Ventanilla ventanilla) {
        Ventanilla ventanillaSaved = service.crearVentanilla(ventanilla);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventanilla);
    }

}

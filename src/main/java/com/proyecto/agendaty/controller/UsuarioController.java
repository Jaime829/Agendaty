package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping("/crear-asesor/{ventanilla}")
    public ResponseEntity<?> crearAsesor(@RequestBody Asesor asesor, @PathVariable Integer ventanilla) {
        try {
            Asesor asesorSaved = service.crearAsesor(asesor, ventanilla);
            return ResponseEntity.status(HttpStatus.CREATED).body(asesorSaved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}

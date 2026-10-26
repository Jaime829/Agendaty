package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.AsesorRequest;
import com.proyecto.agendaty.dto.AsesorResponse;
import com.proyecto.agendaty.service.AsesorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final AsesorService service;

    @PostMapping("/crear-asesor/{ventanilla}")
    public ResponseEntity<AsesorResponse> crearAsesor(@RequestBody AsesorRequest asesor, @PathVariable Integer ventanilla) {

        AsesorResponse asesorSaved = service.crearAsesor(asesor, ventanilla);
        return ResponseEntity.status(HttpStatus.CREATED).body(asesorSaved);


    }
}

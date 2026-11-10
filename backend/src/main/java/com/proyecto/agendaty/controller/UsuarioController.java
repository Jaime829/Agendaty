package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.AsesorRequest;
import com.proyecto.agendaty.dto.AsesorResponse;
import com.proyecto.agendaty.service.AsesorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    private final AsesorService service;

    public UsuarioController(AsesorService service) {
        this.service = service;
    }

    @PostMapping("/asesores/{numeroVentanilla}")
    public ResponseEntity<AsesorResponse> crearAsesor(@Valid @RequestBody AsesorRequest asesor,
                                                      @PathVariable Long numeroVentanilla) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearAsesor(asesor, numeroVentanilla));
    }
}

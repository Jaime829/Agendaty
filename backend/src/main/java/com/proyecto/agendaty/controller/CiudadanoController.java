package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.CiudadanoRequest;
import com.proyecto.agendaty.dto.CiudadanoResponse;
import com.proyecto.agendaty.dto.CiudadanoUpdateRequest;
import com.proyecto.agendaty.service.CiudadanoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ciudadanos")
@CrossOrigin(origins = "http://localhost:5173")
public class CiudadanoController {

    private final CiudadanoService service;

    public CiudadanoController(CiudadanoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CiudadanoResponse> crear(@Valid @RequestBody CiudadanoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCiudadano(request));
    }

    @GetMapping
    public ResponseEntity<List<CiudadanoResponse>> listar() {
        return ResponseEntity.ok(service.listarCiudadanos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CiudadanoResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerCiudadano(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CiudadanoResponse> actualizar(@PathVariable Integer id,
                                                        @Valid @RequestBody CiudadanoUpdateRequest request) {
        return ResponseEntity.ok(service.actualizarCiudadano(id, request));
    }
}

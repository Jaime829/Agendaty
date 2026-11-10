package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.TipoTramiteRequest;
import com.proyecto.agendaty.dto.TipoTramiteResponse;
import com.proyecto.agendaty.service.TipoTramiteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tipos-tramite")
@CrossOrigin(origins = "http://localhost:5173")
public class TipoTramiteController {

    private final TipoTramiteService service;

    public TipoTramiteController(TipoTramiteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TipoTramiteResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoTramiteResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<TipoTramiteResponse> crear(@Valid @RequestBody TipoTramiteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoTramiteResponse> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody TipoTramiteRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

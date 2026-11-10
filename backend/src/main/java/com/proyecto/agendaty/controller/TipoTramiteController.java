package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.TipoTramiteRequest;
import com.proyecto.agendaty.dto.TipoTramiteResponse;
import com.proyecto.agendaty.service.TipoTramiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-tramite")
@RequiredArgsConstructor
public class TipoTramiteController {

    private final TipoTramiteService service;

    @GetMapping
    public ResponseEntity<List<TipoTramiteResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR', 'ASESOR', 'RECEPCIONISTA', 'CIUDADANO')")
    public ResponseEntity<TipoTramiteResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TipoTramiteResponse> crear(@Valid @RequestBody TipoTramiteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<TipoTramiteResponse> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody TipoTramiteRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.LoginResponse;
import com.proyecto.agendaty.dto.RegisterRequest;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * RA4: Solo ADMINISTRADOR puede registrar usuarios internos (asesores, recepcionistas,
     * coordinadores, administradores) mediante header X-Usuario-Id.
     */
    @PostMapping
    public ResponseEntity<LoginResponse> registrarUsuarioInterno(
            @RequestHeader(name = "X-Usuario-Id", required = false) Integer xUsuarioId,
            @RequestHeader(name = "X_Usuario_Id", required = false) Integer xUsuarioIdLegacy,
            @Valid @RequestBody RegisterRequest request) {

        // Validar que el usuario autenticado sea ADMINISTRADOR (RA6)
        authService.validarRol(resolverUsuarioId(xUsuarioId, xUsuarioIdLegacy),
                com.proyecto.agendaty.model.enums.RolUsuario.ROLE_ADMINISTRADOR);

        LoginResponse creado = authService.crearUsuarioInterno(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * RA4: Listar todos los usuarios (solo ADMINISTRADOR).
     */
    @GetMapping
    public ResponseEntity<List<LoginResponse>> listarUsuarios(
            @RequestHeader(name = "X-Usuario-Id", required = false) Integer xUsuarioId,
            @RequestHeader(name = "X_Usuario_Id", required = false) Integer xUsuarioIdLegacy) {

        authService.validarRol(resolverUsuarioId(xUsuarioId, xUsuarioIdLegacy),
                com.proyecto.agendaty.model.enums.RolUsuario.ROLE_ADMINISTRADOR);

        List<LoginResponse> usuarios = authService.listarTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Acepta el header documentado (X-Usuario-Id) y el alias heredado (X_Usuario_Id).
     */
    private Integer resolverUsuarioId(Integer xUsuarioId, Integer xUsuarioIdLegacy) {
        if (xUsuarioId != null) {
            return xUsuarioId;
        }
        if (xUsuarioIdLegacy != null) {
            return xUsuarioIdLegacy;
        }
        throw new BadRequestException("Falta el header X-Usuario-Id");
    }
}

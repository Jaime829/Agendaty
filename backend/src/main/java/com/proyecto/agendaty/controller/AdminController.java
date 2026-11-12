package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.LoginResponse;
import com.proyecto.agendaty.dto.RegisterRequest;
import com.proyecto.agendaty.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestHeader Integer X_Usuario_Id,
            @Valid @RequestBody RegisterRequest request) {

        // Validar que el usuario autenticado sea ADMINISTRADOR (RA6)
        authService.validarRol(X_Usuario_Id, com.proyecto.agendaty.model.enums.RolUsuario.ROLE_ADMINISTRADOR);

        LoginResponse creado = authService.crearUsuarioInterno(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}

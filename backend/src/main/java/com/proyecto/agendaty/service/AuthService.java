package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.LoginRequest;
import com.proyecto.agendaty.dto.LoginResponse;
import com.proyecto.agendaty.dto.RegisterRequest;
import com.proyecto.agendaty.model.enums.RolUsuario;

import java.util.List;

public interface AuthService {
    /**
     * Registro público (RA1). Rol SIEMPRE CIUDADANO. Ignora cualquier rol enviado.
     */
    LoginResponse registrar(RegisterRequest request);

    /**
     * Login simple (RA2). Busca por correo en ciudadanos o asesores.
     */
    LoginResponse login(LoginRequest request);

    /**
     * RA4 (extendido): Crea un ciudadano o un usuario interno (asesor, recepcionista,
     * coordinador, administrador). Solo puede ser llamado por un ADMINISTRADOR
     * (validado en el controller).
     */
    LoginResponse crearUsuarioInterno(RegisterRequest request);

    /**
     * RA4: Listar todos los usuarios (solo ADMINISTRADOR).
     */
    List<LoginResponse> listarTodosLosUsuarios();

    /**
     * Valida que el usuario autenticado (por X-Usuario-Id) tenga el rol requerido (RA6).
     */
    void validarRol(Integer usuarioId, RolUsuario rolRequerido);

    /**
     * Valida que el usuario autenticado tenga uno de los roles permitidos (RA6).
     */
    void validarRolCualquiera(Integer usuarioId, RolUsuario[] rolesPermitidos);
}

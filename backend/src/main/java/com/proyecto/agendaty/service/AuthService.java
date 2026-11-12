package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.LoginRequest;
import com.proyecto.agendaty.dto.LoginResponse;
import com.proyecto.agendaty.dto.RegisterRequest;
import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.entity.Usuario;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.model.enums.RolUsuario;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final CiudadanoRepository ciudadanoRepository;
    private final AsesorRepository asesorRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registro público (RA1). Rol SIEMPRE CIUDADANO. Ignora cualquier rol enviado.
     */
    public LoginResponse registrar(RegisterRequest request) {
        if (Boolean.TRUE.equals(ciudadanoRepository.existsByCorreo(request.correo()))) {
            throw new BadRequestException("Ya existe un ciudadano con ese correo");
        }
        if (Boolean.TRUE.equals(ciudadanoRepository.existsByDocumento(request.documento()))) {
            throw new BadRequestException("Ya existe un ciudadano con ese documento");
        }

        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setNombre(request.nombre());
        ciudadano.setCorreo(request.correo());
        ciudadano.setUsername(request.correo());
        ciudadano.setPassword(passwordEncoder.encode(request.password()));
        ciudadano.setRol(RolUsuario.ROLE_CIUDADANO);
        ciudadano.setDocumento(request.documento());
        ciudadano.setTelefono(request.telefono());

        Ciudadano guardado = ciudadanoRepository.save(ciudadano);

        return new LoginResponse(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getCorreo(),
                guardado.getRol().name()
        );
    }

    /**
     * Login simple (RA2). Busca por correo en ciudadanos o asesores.
     */
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = buscarUsuarioPorCorreo(request.correo());

        if (usuario.getPassword() == null
                || !passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        return new LoginResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().name()
        );
    }

    private Usuario buscarUsuarioPorCorreo(String correo) {
        return ciudadanoRepository.findByCorreo(correo)
                .map(u -> (Usuario) u)
                .orElseGet(() -> asesorRepository.findByCorreo(correo)
                        .map(u -> (Usuario) u)
                        .orElseThrow(() -> new BadRequestException("Credenciales inválidas")));
    }

    /**
     * RA4: Crea un usuario interno (asesor, recepcionista, coordinador, administrador).
     * Solo puede ser llamado por un ADMINISTRADOR (validado en el controller).
     */
    public LoginResponse crearUsuarioInterno(RegisterRequest request) {
        if (Boolean.TRUE.equals(ciudadanoRepository.existsByCorreo(request.correo()))) {
            throw new BadRequestException("Ya existe un usuario con ese correo");
        }
        if (Boolean.TRUE.equals(ciudadanoRepository.existsByDocumento(request.documento()))) {
            throw new BadRequestException("Ya existe un usuario con ese documento");
        }

        RolUsuario rol;
        try {
            rol = RolUsuario.valueOf(request.rol());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rol no válido: " + request.rol());
        }

        // Solo se permiten roles internos
        if (rol != RolUsuario.ROLE_ASESOR
                && rol != RolUsuario.ROLE_RECEPCIONISTA
                && rol != RolUsuario.ROLE_COORDINADOR
                && rol != RolUsuario.ROLE_ADMINISTRADOR) {
            throw new BadRequestException("Rol no permitido para usuario interno: " + rol);
        }

        if (rol == RolUsuario.ROLE_ASESOR) {
            Asesor asesor = new Asesor();
            asesor.setNombre(request.nombre());
            asesor.setCorreo(request.correo());
            asesor.setUsername(request.correo());
            asesor.setPassword(passwordEncoder.encode(request.password()));
            asesor.setRol(rol);
            asesor.setDisponible(true);
            Asesor guardado = asesorRepository.save(asesor);
            return new LoginResponse(guardado.getId(), guardado.getNombre(), guardado.getCorreo(), guardado.getRol().name());
        }

        // Para CIUDADANO, RECEPCIONISTA, COORDINADOR, ADMINISTRADOR usamos la entidad Ciudadano
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setNombre(request.nombre());
        ciudadano.setCorreo(request.correo());
        ciudadano.setUsername(request.correo());
        ciudadano.setPassword(passwordEncoder.encode(request.password()));
        ciudadano.setRol(rol);
        ciudadano.setDocumento(request.documento());
        ciudadano.setTelefono(request.telefono());
        Ciudadano guardado = ciudadanoRepository.save(ciudadano);

        return new LoginResponse(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getCorreo(),
                guardado.getRol().name()
        );
    }

    /**
     * Valida que el usuario autenticado (por X-Usuario-Id) tenga el rol requerido (RA6).
     */
    public void validarRol(Integer usuarioId, RolUsuario rolRequerido) {
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario.getRol() != rolRequerido) {
            throw new BadRequestException("No tiene permisos para esta acción (requiere rol " + rolRequerido + ")");
        }
    }

    /**
     * Valida que el usuario autenticado tenga uno de los roles permitidos (RA6).
     */
    public void validarRolCualquiera(Integer usuarioId, RolUsuario[] rolesPermitidos) {
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        boolean tieneRol = false;
        for (RolUsuario r : rolesPermitidos) {
            if (usuario.getRol() == r) {
                tieneRol = true;
                break;
            }
        }
        if (!tieneRol) {
            throw new BadRequestException("No tiene permisos para esta acción");
        }
    }

    private Usuario buscarUsuarioPorId(Integer id) {
        return ciudadanoRepository.findById(id)
                .map(u -> (Usuario) u)
                .orElseGet(() -> asesorRepository.findById(id)
                        .map(u -> (Usuario) u)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuario " + id + " no encontrado")));
    }
}

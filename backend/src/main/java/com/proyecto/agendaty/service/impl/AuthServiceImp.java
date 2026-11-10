package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.LoginRequest;
import com.proyecto.agendaty.dto.LoginResponse;
import com.proyecto.agendaty.dto.RegisterRequest;
import com.proyecto.agendaty.entity.Usuario;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.model.enums.RolUsuario;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.security.JwtService;
import com.proyecto.agendaty.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImp implements AuthService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CiudadanoRepository ciudadanoRepository;
    private final AsesorRepository asesorRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = buscarUsuario(request.username());

        if (usuario.getPassword() == null
                || !passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority(usuario.getRol().name()))
        );

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRol().name()
        );
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        if (ciudadanoRepository.findByUsername(request.username()).isPresent()
                || asesorRepository.findByUsername(request.username()).isPresent()) {
            throw new BadRequestException("El nombre de usuario ya existe");
        }

        RolUsuario rol;
        try {
            rol = RolUsuario.valueOf(request.rol());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rol no válido: " + request.rol());
        }

        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setNombre(request.nombre());
        ciudadano.setUsername(request.username());
        ciudadano.setPassword(passwordEncoder.encode(request.password()));
        ciudadano.setRol(rol);
        // documento <= 10 chars (restricción BD) y único: usa el username truncado
        String documento = request.username().substring(0, Math.min(10, request.username().length()));
        ciudadano.setDocumento(documento);
        ciudadano.setTelefono("N/A");

        Ciudadano guardado = ciudadanoRepository.save(ciudadano);

        String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                guardado.getUsername(),
                guardado.getPassword(),
                List.of(new SimpleGrantedAuthority(guardado.getRol().name()))
        ));

        return new LoginResponse(token, "Bearer", guardado.getId(), guardado.getUsername(), guardado.getRol().name());
    }

    private Usuario buscarUsuario(String username) {
        return ciudadanoRepository.findByUsername(username)
                .map(u -> (Usuario) u)
                .orElseGet(() -> asesorRepository.findByUsername(username)
                        .map(u -> (Usuario) u)
                        .orElseThrow(() -> new BadRequestException("Usuario no encontrado")));
    }
}

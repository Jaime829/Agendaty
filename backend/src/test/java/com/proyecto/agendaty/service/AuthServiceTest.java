package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.LoginRequest;
import com.proyecto.agendaty.dto.LoginResponse;
import com.proyecto.agendaty.dto.RegisterRequest;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.model.enums.RolUsuario;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import com.proyecto.agendaty.security.JwtService;
import com.proyecto.agendaty.service.impl.AuthServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private CiudadanoRepository ciudadanoRepository;
    @Mock private AsesorRepository asesorRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImp service;

    @Test
    void login_credencialesValidas_deberiaRetornarToken() {
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setId(1);
        ciudadano.setUsername("juan");
        ciudadano.setPassword("hashed");
        ciudadano.setRol(RolUsuario.ROLE_CIUDADANO);

        when(ciudadanoRepository.findByUsername("juan")).thenReturn(Optional.of(ciudadano));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("token-jwt");

        LoginResponse respuesta = service.login(new LoginRequest("juan", "secret"));

        assertNotNull(respuesta);
        assertEquals("token-jwt", respuesta.token());
        assertEquals("juan", respuesta.username());
        assertEquals("ROLE_CIUDADANO", respuesta.rol());
    }

    @Test
    void login_credencialesInvalidas_deberiaLanzarExcepcion() {
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setId(1);
        ciudadano.setUsername("juan");
        ciudadano.setPassword("hashed");
        ciudadano.setRol(RolUsuario.ROLE_CIUDADANO);

        when(ciudadanoRepository.findByUsername("juan")).thenReturn(Optional.of(ciudadano));
        when(passwordEncoder.matches("mal", "hashed")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.login(new LoginRequest("juan", "mal")));
    }

    @Test
    void login_usuarioNoExistente_deberiaLanzarExcepcion() {
        when(ciudadanoRepository.findByUsername("desconocido")).thenReturn(Optional.empty());
        when(asesorRepository.findByUsername("desconocido")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> service.login(new LoginRequest("desconocido", "x")));
    }

    @Test
    void register_cuandoUsuarioYaExiste_deberiaLanzarExcepcion() {
        RegisterRequest request = new RegisterRequest("Juan", "juan", "pass123", "juan@mail.com", "ROLE_CIUDADANO");
        when(ciudadanoRepository.findByUsername("juan")).thenReturn(Optional.of(new Ciudadano()));

        assertThrows(BadRequestException.class, () -> service.register(request));
    }

    @Test
    void register_rolInvalido_deberiaLanzarExcepcion() {
        RegisterRequest request = new RegisterRequest("Juan", "juan", "pass123", "juan@mail.com", "ROL_INEXISTENTE");
        when(ciudadanoRepository.findByUsername("juan")).thenReturn(Optional.empty());
        when(asesorRepository.findByUsername("juan")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> service.register(request));
    }

    @Test
    void register_casoFeliz_deberiaCrearYRetornarToken() {
        RegisterRequest request = new RegisterRequest("Juan", "juan", "pass123", "juan@mail.com", "ROLE_CIUDADANO");
        Ciudadano guardado = new Ciudadano();
        guardado.setId(1);
        guardado.setUsername("juan");
        guardado.setRol(RolUsuario.ROLE_CIUDADANO);

        when(ciudadanoRepository.findByUsername("juan")).thenReturn(Optional.empty());
        when(asesorRepository.findByUsername("juan")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("hashed");
        when(ciudadanoRepository.save(any(Ciudadano.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("token-jwt");

        LoginResponse respuesta = service.register(request);

        assertNotNull(respuesta);
        assertEquals("token-jwt", respuesta.token());
        assertEquals("ROLE_CIUDADANO", respuesta.rol());
        verify(ciudadanoRepository).save(any(Ciudadano.class));
    }
}

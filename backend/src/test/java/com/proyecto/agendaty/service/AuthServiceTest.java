package com.proyecto.agendaty.service;

import com.proyecto.agendaty.dto.LoginRequest;
import com.proyecto.agendaty.dto.LoginResponse;
import com.proyecto.agendaty.dto.RegisterRequest;
import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.model.enums.RolUsuario;
import com.proyecto.agendaty.service.impl.AuthServiceImp;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CiudadanoRepository ciudadanoRepository;

    @Mock
    private AsesorRepository asesorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImp authService;

    /* ==================== RA1: Registro público con rol CIUDADANO forzado ==================== */

    @Test
    void registrar_rolCIUDADANOForzado_aunqueSeEnvieOtroRol() {
        RegisterRequest request = new RegisterRequest(
                "Juan Perez", "juan@email.com", "pass123", "12345678", "3001234567", "ROLE_ADMINISTRADOR"
        );

        when(ciudadanoRepository.existsByCorreo("juan@email.com")).thenReturn(false);
        when(ciudadanoRepository.existsByDocumento("12345678")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed");
        when(ciudadanoRepository.save(any(Ciudadano.class))).thenAnswer(inv -> {
            Ciudadano c = inv.getArgument(0);
            c.setId(1);
            return c;
        });

        LoginResponse response = authService.registrar(request);

        assertEquals(RolUsuario.ROLE_CIUDADANO, RolUsuario.valueOf(response.rol()),
                "RA1: el rol debe ser CIUDADANO aunque se enviara otro");
    }

    /* ==================== RA2: Login correcto e incorrecto ==================== */

    @Test
    void login_credencialesCorrectas() {
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setId(1);
        ciudadano.setNombre("Juan");
        ciudadano.setCorreo("juan@email.com");
        ciudadano.setPassword("hashed");
        ciudadano.setRol(RolUsuario.ROLE_CIUDADANO);

        when(ciudadanoRepository.findByCorreo("juan@email.com")).thenReturn(java.util.Optional.of(ciudadano));
        when(passwordEncoder.matches("pass123", "hashed")).thenReturn(true);

        LoginResponse response = authService.login(new LoginRequest("juan@email.com", "pass123"));

        assertEquals(1, response.id());
        assertEquals("juan@email.com", response.correo());
        assertEquals("ROLE_CIUDADANO", response.rol());
    }

    @Test
    void login_credencialesIncorrectas() {
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setPassword("hashed");
        ciudadano.setRol(RolUsuario.ROLE_CIUDADANO);

        when(ciudadanoRepository.findByCorreo("juan@email.com")).thenReturn(java.util.Optional.of(ciudadano));
        when(passwordEncoder.matches("mal", "hashed")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> authService.login(new LoginRequest("juan@email.com", "mal")),
                "RA2: credenciales incorrectas deben lanzar excepción");
    }

    @Test
    void login_usuarioNoExiste() {
        when(ciudadanoRepository.findByCorreo("noexiste@email.com")).thenReturn(java.util.Optional.empty());
        when(asesorRepository.findByCorreo("noexiste@email.com")).thenReturn(java.util.Optional.empty());

        assertThrows(BadRequestException.class,
                () -> authService.login(new LoginRequest("noexiste@email.com", "pass")),
                "RA2: usuario inexistente debe lanzar excepción");
    }

    /* ==================== RA4: Admin registra asesor ==================== */

    @Test
    void crearUsuarioInterno_adminRegistraAsesor() {
        RegisterRequest request = new RegisterRequest(
                "Maria Asesora", "asesor@email.com", "pass123", "87654321", "3009876543", "ROLE_ASESOR"
        );

        when(ciudadanoRepository.existsByCorreo("asesor@email.com")).thenReturn(false);
        when(ciudadanoRepository.existsByDocumento("87654321")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed");
        when(asesorRepository.save(any(Asesor.class))).thenAnswer(inv -> {
            Asesor a = inv.getArgument(0);
            a.setId(5);
            return a;
        });

        LoginResponse response = authService.crearUsuarioInterno(request);

        assertEquals(RolUsuario.ROLE_ASESOR, RolUsuario.valueOf(response.rol()),
                "RA4: el rol debe ser ASESOR");
        assertEquals("asesor@email.com", response.correo());
    }

    /* ==================== RA4: Admin registra ciudadano ==================== */

    @Test
    void crearUsuarioInterno_adminRegistraCiudadano() {
        RegisterRequest request = new RegisterRequest(
                "Carlos Ciudadano", "ciudadano@email.com", "pass123", "11223344", "3001112233", "ROLE_CIUDADANO"
        );

        when(ciudadanoRepository.existsByCorreo("ciudadano@email.com")).thenReturn(false);
        when(ciudadanoRepository.existsByDocumento("11223344")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed");
        when(ciudadanoRepository.save(any(Ciudadano.class))).thenAnswer(inv -> {
            Ciudadano c = inv.getArgument(0);
            c.setId(7);
            return c;
        });

        LoginResponse response = authService.crearUsuarioInterno(request);

        assertEquals(RolUsuario.ROLE_CIUDADANO, RolUsuario.valueOf(response.rol()),
                "RA4: el rol debe ser CIUDADANO");
        assertEquals("ciudadano@email.com", response.correo());
    }

    /* ==================== RA4: Rol inexistente lanza excepción ==================== */

    @Test
    void crearUsuarioInterno_rolInexistente_lanzaExcepcion() {
        RegisterRequest request = new RegisterRequest(
                "Rol Inventado", "rol@email.com", "pass123", "55667788", "3004445566", "ROLE_SUPERADMIN"
        );

        when(ciudadanoRepository.existsByCorreo("rol@email.com")).thenReturn(false);
        when(ciudadanoRepository.existsByDocumento("55667788")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> authService.crearUsuarioInterno(request),
                "RA4: un rol inexistente debe lanzar excepción");
    }

    /* ==================== Duplicados entre ciudadanos y asesores ==================== */

    @Test
    void crearUsuarioInterno_correoExisteEnAsesor_lanzaExcepcion() {
        RegisterRequest request = new RegisterRequest(
                "Nombre Duplicado", "asesor@email.com", "pass123", "12345678", "3001234567", "ROLE_RECEPCIONISTA"
        );

        when(ciudadanoRepository.existsByCorreo("asesor@email.com")).thenReturn(false);
        when(asesorRepository.existsByCorreo("asesor@email.com")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> authService.crearUsuarioInterno(request),
                "Un correo ya usado por un asesor debe ser rechazado");
    }

    @Test
    void registrar_correoExisteEnAsesor_lanzaExcepcion() {
        RegisterRequest request = new RegisterRequest(
                "Nombre Duplicado", "asesor@email.com", "pass123", "12345678", "3001234567", "ROLE_CIUDADANO"
        );

        when(ciudadanoRepository.existsByCorreo("asesor@email.com")).thenReturn(false);
        when(asesorRepository.existsByCorreo("asesor@email.com")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> authService.registrar(request),
                "RA1: un correo ya usado por un asesor debe ser rechazado");
    }

    /* ==================== RA6: Usuario no admin no puede registrar asesor ==================== */

    @Test
    void crearUsuarioInterno_usuarioNoAdmin_lanzaExcepcion() {
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setId(999);
        ciudadano.setNombre("Juan");
        ciudadano.setRol(RolUsuario.ROLE_CIUDADANO);

        when(ciudadanoRepository.findById(999)).thenReturn(java.util.Optional.of(ciudadano));

        assertThrows(BadRequestException.class,
                () -> authService.validarRol(999, RolUsuario.ROLE_ADMINISTRADOR),
                "RA6: usuario no admin no puede registrar asesor");
    }

    /* ==================== RA5: Contraseña guardada con BCrypt ==================== */

    @Test
    void registrar_contraseñaSeGuardaConBCrypt() {
        RegisterRequest request = new RegisterRequest(
                "Juan Perez", "juan@email.com", "pass123", "12345678", "3001234567", "ROLE_CIUDADANO"
        );

        when(ciudadanoRepository.existsByCorreo("juan@email.com")).thenReturn(false);
        when(ciudadanoRepository.existsByDocumento("12345678")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("$2a$10$hashedvalue");
        when(ciudadanoRepository.save(any(Ciudadano.class))).thenAnswer(inv -> {
            Ciudadano c = inv.getArgument(0);
            c.setId(1);
            return c;
        });

        authService.registrar(request);

        // Verificar que se llamó a passwordEncoder.encode (BCrypt)
        verify(passwordEncoder).encode("pass123");
    }
}

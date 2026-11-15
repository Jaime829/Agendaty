package com.proyecto.agendaty.controller;

import com.proyecto.agendaty.dto.AsesorRequest;
import com.proyecto.agendaty.dto.AsesorResponse;
import com.proyecto.agendaty.dto.AtencionRequest;
import com.proyecto.agendaty.dto.AtencionResponse;
import com.proyecto.agendaty.dto.CiudadanoRequest;
import com.proyecto.agendaty.dto.CiudadanoResponse;
import com.proyecto.agendaty.dto.CiudadanoUpdateRequest;
import com.proyecto.agendaty.dto.LoginRequest;
import com.proyecto.agendaty.dto.LoginResponse;
import com.proyecto.agendaty.dto.RegisterRequest;
import com.proyecto.agendaty.dto.TipoTramiteRequest;
import com.proyecto.agendaty.dto.TipoTramiteResponse;
import com.proyecto.agendaty.dto.TurnoRequest;
import com.proyecto.agendaty.dto.TurnoResponse;
import com.proyecto.agendaty.dto.VentanillaRequest;
import com.proyecto.agendaty.dto.VentanillaResponse;
import com.proyecto.agendaty.model.enums.EstadoTurno;
import com.proyecto.agendaty.model.enums.PrioridadTurno;
import com.proyecto.agendaty.model.enums.RolUsuario;
import com.proyecto.agendaty.service.AsesorService;
import com.proyecto.agendaty.service.AtencionService;
import com.proyecto.agendaty.service.AuthService;
import com.proyecto.agendaty.service.CiudadanoService;
import com.proyecto.agendaty.service.ReporteService;
import com.proyecto.agendaty.service.TipoTramiteService;
import com.proyecto.agendaty.service.TurnoService;
import com.proyecto.agendaty.service.VentanillaService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControladoresTest {

    /* ==================== AdminController ==================== */

    @Test
    void adminController_listarAutorizado() {
        AuthService authService = mock(AuthService.class);
        doNothing().when(authService).validarRol(anyInt(), any(RolUsuario.class));
        when(authService.listarTodosLosUsuarios()).thenReturn(List.of(
                new LoginResponse(1, "Admin Sistema", "admin@turnos.local", "ROLE_ADMINISTRADOR")
        ));

        AdminController controller = new AdminController(authService);

        ResponseEntity<List<LoginResponse>> r = controller.listarUsuarios(1);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
        verify(authService).validarRol(1, RolUsuario.ROLE_ADMINISTRADOR);
    }

    @Test
    void adminController_registrarUsuarioInterno() {
        AuthService authService = mock(AuthService.class);
        doNothing().when(authService).validarRol(anyInt(), any(RolUsuario.class));
        RegisterRequest req = new RegisterRequest("Maria", "maria@turnos.local", "pass123", "11111111", "3001111111", "ROLE_ASESOR");
        when(authService.crearUsuarioInterno(req))
                .thenReturn(new LoginResponse(5, "Maria", "maria@turnos.local", "ROLE_ASESOR"));

        AdminController controller = new AdminController(authService);

        ResponseEntity<LoginResponse> r = controller.registrarUsuarioInterno(1, req);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals("ROLE_ASESOR", r.getBody().rol());
    }

    /* ==================== AsesorController ==================== */

    @Test
    void asesorController_listar() {
        AsesorService service = mock(AsesorService.class);
        when(service.listarAsesores()).thenReturn(List.of(new AsesorResponse(1, "A1", true, 1)));

        AsesorController controller = new AsesorController(service);

        ResponseEntity<List<AsesorResponse>> r = controller.listar();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
    }

    @Test
    void asesorController_obtener() {
        AsesorService service = mock(AsesorService.class);
        when(service.obtenerAsesor(7)).thenReturn(new AsesorResponse(7, "Asesor", true, 3));

        AsesorController controller = new AsesorController(service);

        ResponseEntity<AsesorResponse> r = controller.obtener(7);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("Asesor", r.getBody().getNombre());
    }

    @Test
    void asesorController_cambiarDisponibilidad() {
        AsesorService service = mock(AsesorService.class);
        when(service.cambiarDisponibilidad(7, false)).thenReturn(new AsesorResponse(7, "A", false, 1));

        AsesorController controller = new AsesorController(service);

        ResponseEntity<AsesorResponse> r = controller.cambiarDisponibilidad(7, false);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(false, r.getBody().getDisponible());
    }

    /* ==================== VentanillaController ==================== */

    @Test
    void ventanillaController_listar() {
        VentanillaService service = mock(VentanillaService.class);
        when(service.listarVentanillas()).thenReturn(List.of(new VentanillaResponse(1L, 1, "V1", null)));

        VentanillaController controller = new VentanillaController(service);

        ResponseEntity<List<VentanillaResponse>> r = controller.listar();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
    }

    @Test
    void ventanillaController_crear() {
        VentanillaService service = mock(VentanillaService.class);
        VentanillaRequest req = new VentanillaRequest();
        req.setNumero(9);
        req.setDescripcion("Prioridad");
        when(service.crearVentanilla(req)).thenReturn(new VentanillaResponse(1L, 9, "Prioridad", null));

        VentanillaController controller = new VentanillaController(service);

        ResponseEntity<VentanillaResponse> r = controller.crear(req);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals(9, r.getBody().getNumero());
    }

    @Test
    void ventanillaController_obtener() {
        VentanillaService service = mock(VentanillaService.class);
        when(service.obtenerVentanilla(3L)).thenReturn(new VentanillaResponse(3L, 3, "V3", 5));

        VentanillaController controller = new VentanillaController(service);

        ResponseEntity<VentanillaResponse> r = controller.obtener(3L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(3, r.getBody().getNumero());
    }

    @Test
    void ventanillaController_actualizar() {
        VentanillaService service = mock(VentanillaService.class);
        VentanillaRequest req = new VentanillaRequest();
        req.setNumero(2);
        req.setDescripcion("Actualizada");
        when(service.actualizarVentanilla(3L, req)).thenReturn(new VentanillaResponse(3L, 2, "Actualizada", null));

        VentanillaController controller = new VentanillaController(service);

        ResponseEntity<VentanillaResponse> r = controller.actualizar(3L, req);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("Actualizada", r.getBody().getDescripcion());
    }

    @Test
    void ventanillaController_eliminar() {
        VentanillaService service = mock(VentanillaService.class);

        VentanillaController controller = new VentanillaController(service);

        ResponseEntity<Void> r = controller.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, r.getStatusCode());
        verify(service).eliminarVentanilla(1L);
    }

    /* ==================== TipoTramiteController ==================== */

    @Test
    void tipoTramiteController_listar() {
        TipoTramiteService service = mock(TipoTramiteService.class);
        when(service.listar()).thenReturn(List.of(
                new TipoTramiteResponse(1L, "Cedula", "CED", false, true)
        ));

        TipoTramiteController controller = new TipoTramiteController(service);

        ResponseEntity<List<TipoTramiteResponse>> r = controller.listar();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
    }

    @Test
    void tipoTramiteController_obtener() {
        TipoTramiteService service = mock(TipoTramiteService.class);
        when(service.obtener(1L)).thenReturn(new TipoTramiteResponse(1L, "Cedula", "CED", false, true));

        TipoTramiteController controller = new TipoTramiteController(service);

        ResponseEntity<TipoTramiteResponse> r = controller.obtener(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("CED", r.getBody().codigo());
    }

    @Test
    void tipoTramiteController_crear() {
        TipoTramiteService service = mock(TipoTramiteService.class);
        TipoTramiteRequest req = new TipoTramiteRequest("A", "COD", false, true);
        when(service.crear(req)).thenReturn(new TipoTramiteResponse(1L, "A", "COD", false, true));

        TipoTramiteController controller = new TipoTramiteController(service);

        ResponseEntity<TipoTramiteResponse> r = controller.crear(req);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
    }

    @Test
    void tipoTramiteController_actualizar() {
        TipoTramiteService service = mock(TipoTramiteService.class);
        TipoTramiteRequest req = new TipoTramiteRequest("B", "B", true, false);
        when(service.actualizar(1L, req)).thenReturn(new TipoTramiteResponse(1L, "B", "B", true, false));

        TipoTramiteController controller = new TipoTramiteController(service);

        ResponseEntity<TipoTramiteResponse> r = controller.actualizar(1L, req);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void tipoTramiteController_eliminar() {
        TipoTramiteService service = mock(TipoTramiteService.class);

        TipoTramiteController controller = new TipoTramiteController(service);

        ResponseEntity<Void> r = controller.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, r.getStatusCode());
        verify(service).eliminar(1L);
    }

    /* ==================== ReporteController ==================== */

    @Test
    void reporteController_tiempoEsperaPromedio() {
        ReporteService service = mock(ReporteService.class);
        Map<String, Object> esperado = Map.of("valor", 0.0);
        when(service.tiempoEsperaPromedio(null, null, null, null)).thenReturn(esperado);

        ReporteController controller = new ReporteController(service);

        ResponseEntity<Map<String, Object>> r = controller.tiempoEsperaPromedio(null, null, null, null);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(esperado, r.getBody());
    }

    @Test
    void reporteController_turnosAtendidos() {
        ReporteService service = mock(ReporteService.class);
        Map<String, Object> esperado = Map.of("atendidos", 1L);
        when(service.turnosAtendidos(null, null, null, null)).thenReturn(esperado);

        ReporteController controller = new ReporteController(service);

        ResponseEntity<Map<String, Object>> r = controller.turnosAtendidos(null, null, null, null);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(esperado, r.getBody());
    }

    @Test
    void reporteController_tramitesFrecuentes() {
        ReporteService service = mock(ReporteService.class);
        List<Map<String, Object>> esperado = List.of(Map.of("tipo_tramite_nombre", "Cedula"));
        when(service.tramitesFrecuentes(null, null, null)).thenReturn(esperado);

        ReporteController controller = new ReporteController(service);

        ResponseEntity<List<Map<String, Object>>> r = controller.tramitesFrecuentes(null, null, null);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(esperado, r.getBody());
    }

    /* ==================== CiudadanoController ==================== */

    @Test
    void ciudadanoController_crear() {
        CiudadanoService service = mock(CiudadanoService.class);
        CiudadanoRequest req = new CiudadanoRequest("Juan", "j@e.com", "12345678", "3001234567");
        when(service.crearCiudadano(req)).thenReturn(new CiudadanoResponse(1, "Juan", "j@e.com", "12345678", "3001234567"));

        CiudadanoController controller = new CiudadanoController(service);

        ResponseEntity<CiudadanoResponse> r = controller.crear(req);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
    }

    @Test
    void ciudadanoController_listar() {
        CiudadanoService service = mock(CiudadanoService.class);
        when(service.listarCiudadanos()).thenReturn(List.of(
                new CiudadanoResponse(1, "Juan", "j@e.com", "12345678", "3001234567")
        ));

        CiudadanoController controller = new CiudadanoController(service);

        ResponseEntity<List<CiudadanoResponse>> r = controller.listar();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
    }

    @Test
    void ciudadanoController_obtener() {
        CiudadanoService service = mock(CiudadanoService.class);
        when(service.obtenerCiudadano(1)).thenReturn(
                new CiudadanoResponse(1, "Juan", "j@e.com", "12345678", "3001234567"));

        CiudadanoController controller = new CiudadanoController(service);

        ResponseEntity<CiudadanoResponse> r = controller.obtener(1);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void ciudadanoController_actualizar() {
        CiudadanoService service = mock(CiudadanoService.class);
        CiudadanoUpdateRequest req = new CiudadanoUpdateRequest("Juan", "3001234567");
        when(service.actualizarCiudadano(1, req))
                .thenReturn(new CiudadanoResponse(1, "Juan", "j@e.com", "12345678", "3001234567"));

        CiudadanoController controller = new CiudadanoController(service);

        ResponseEntity<CiudadanoResponse> r = controller.actualizar(1, req);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    /* ==================== TurnoController ==================== */

    @Test
    void turnoController_generar() {
        TurnoService service = mock(TurnoService.class);
        TurnoRequest req = new TurnoRequest(5, 1L, false);
        when(service.generarTurno(req, 1)).thenReturn(
                new TurnoResponse(1L, "CEDN-00001", EstadoTurno.EN_ESPERA, PrioridadTurno.NORMAL,
                        LocalDateTime.now(), null, null, 1, "Juan", 1L, "Ced", null, null));

        TurnoController controller = new TurnoController(service);

        ResponseEntity<TurnoResponse> r = controller.generar(1, req);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals("CEDN-00001", r.getBody().numeroTurno());
    }

    @Test
    void turnoController_llamarSiguiente() {
        TurnoService service = mock(TurnoService.class);
        when(service.llamarSiguiente(7)).thenReturn(
                new TurnoResponse(1L, "CEDN-00002", EstadoTurno.LLAMADO, PrioridadTurno.NORMAL,
                        LocalDateTime.now(), LocalDateTime.now(), null, 1, "Juan", 1L, "Ced", 7, "Asesor"));

        TurnoController controller = new TurnoController(service);

        ResponseEntity<TurnoResponse> r = controller.llamarSiguiente(7);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void turnoController_cancelar() {
        TurnoService service = mock(TurnoService.class);
        when(service.cancelarTurno(1L, 1)).thenReturn(
                new TurnoResponse(1L, "CEDN-00003", EstadoTurno.CANCELADO, PrioridadTurno.NORMAL,
                        LocalDateTime.now(), null, null, 1, "Juan", 1L, "Ced", null, null));

        TurnoController controller = new TurnoController(service);

        ResponseEntity<TurnoResponse> r = controller.cancelar(1L, 1);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void turnoController_marcarNoAtendido() {
        TurnoService service = mock(TurnoService.class);
        when(service.marcarNoAtendido(1L)).thenReturn(
                new TurnoResponse(1L, "CEDN-00004", EstadoTurno.NO_ATENDIDO, PrioridadTurno.NORMAL,
                        LocalDateTime.now(), null, null, 1, "Juan", 1L, "Ced", null, null));

        TurnoController controller = new TurnoController(service);

        ResponseEntity<TurnoResponse> r = controller.marcarNoAtendido(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void turnoController_listar() {
        TurnoService service = mock(TurnoService.class);
        when(service.listarTurnos(null, null, null, null)).thenReturn(List.of(
                new TurnoResponse(1L, "CEDN-00005", EstadoTurno.EN_ESPERA, PrioridadTurno.NORMAL,
                        LocalDateTime.now(), null, null, 5, "Juan", 1L, "Ced", null, null)
        ));

        TurnoController controller = new TurnoController(service);

        ResponseEntity<List<TurnoResponse>> r = controller.listar(null, null, null, null);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
    }

    @Test
    void turnoController_obtener() {
        TurnoService service = mock(TurnoService.class);
        when(service.obtenerTurno(1L)).thenReturn(
                new TurnoResponse(1L, "CEDN-00006", EstadoTurno.EN_ESPERA, PrioridadTurno.NORMAL,
                        LocalDateTime.now(), null, null, 5, "Juan", 1L, "Ced", null, null));

        TurnoController controller = new TurnoController(service);

        ResponseEntity<TurnoResponse> r = controller.obtener(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    /* ==================== AtencionController ==================== */

    @Test
    void atencionController_iniciar() {
        AtencionService service = mock(AtencionService.class);
        when(service.iniciarAtencion(1L, 7)).thenReturn(
                new AtencionResponse(1L, 1L, "CEDN-00001", 7, "Asesor",
                        LocalDateTime.now(), null, null, "Iniciada", null, null, 0));

        AtencionController controller = new AtencionController(service);

        ResponseEntity<AtencionResponse> r = controller.iniciar(1L, 7);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void atencionController_pausar() {
        AtencionService service = mock(AtencionService.class);
        when(service.pausarAtencion(1L, 7)).thenReturn(
                new AtencionResponse(1L, 1L, "CEDN-00001", 7, "Asesor",
                        LocalDateTime.now(), null, null, null, null, null, 0));

        AtencionController controller = new AtencionController(service);

        ResponseEntity<AtencionResponse> r = controller.pausar(1L, 7);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void atencionController_reanudar() {
        AtencionService service = mock(AtencionService.class);
        when(service.reanudarAtencion(1L, 7)).thenReturn(
                new AtencionResponse(1L, 1L, "CEDN-00001", 7, "Asesor",
                        LocalDateTime.now(), null, null, null, null, null, 5));

        AtencionController controller = new AtencionController(service);

        ResponseEntity<AtencionResponse> r = controller.reanudar(1L, 7);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void atencionController_finalizar() {
        AtencionService service = mock(AtencionService.class);
        AtencionRequest req = new AtencionRequest("Cédula expedida", "obs");
        when(service.finalizarAtencion(1L, 7, req)).thenReturn(
                new AtencionResponse(1L, 1L, "CEDN-00001", 7, "Asesor",
                        LocalDateTime.now(), LocalDateTime.now(), "obs", "Cédula expedida", 5, 10, 1));

        AtencionController controller = new AtencionController(service);

        ResponseEntity<AtencionResponse> r = controller.finalizar(1L, 7, req);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void atencionController_obtenerPorTurno() {
        AtencionService service = mock(AtencionService.class);
        when(service.obtenerAtencionPorTurno(1L)).thenReturn(
                new AtencionResponse(1L, 1L, "CEDN-00001", 7, "Asesor",
                        LocalDateTime.now(), null, "obs", "res", 3, 8, 0));

        AtencionController controller = new AtencionController(service);

        ResponseEntity<AtencionResponse> r = controller.obtenerPorTurno(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    /* ==================== AuthController ==================== */

    @Test
    void authController_registro() {
        AuthService service = mock(AuthService.class);
        RegisterRequest req = new RegisterRequest("Juan", "j@e.com", "pass123", "12345678", "3001234567", null);
        when(service.registrar(req)).thenReturn(new LoginResponse(1, "Juan", "j@e.com", "ROLE_CIUDADANO"));

        AuthController controller = new AuthController(service);

        ResponseEntity<LoginResponse> r = controller.registrar(req);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals("ROLE_CIUDADANO", r.getBody().rol());
    }

    @Test
    void authController_login() {
        AuthService service = mock(AuthService.class);
        LoginRequest req = new LoginRequest("j@e.com", "pass123");
        when(service.login(req)).thenReturn(new LoginResponse(1, "Juan", "j@e.com", "ROLE_CIUDADANO"));

        AuthController controller = new AuthController(service);

        ResponseEntity<LoginResponse> r = controller.login(req);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().id());
    }

    /* ==================== UsuarioController ==================== */

    @Test
    void usuarioController_crearAsesor() {
        AsesorService service = mock(AsesorService.class);
        AsesorRequest req = new AsesorRequest();
        req.setNombre("María");
        when(service.crearAsesor(req, 5L)).thenReturn(new AsesorResponse(3, "María", true, 5));

        UsuarioController controller = new UsuarioController(service);

        ResponseEntity<AsesorResponse> r = controller.crearAsesor(req, 5L);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
    }
}

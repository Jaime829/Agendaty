package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.AtencionRequest;
import com.proyecto.agendaty.dto.AtencionResponse;
import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Atencion;
import com.proyecto.agendaty.entity.Auditoria;
import com.proyecto.agendaty.entity.Pausa;
import com.proyecto.agendaty.entity.Turno;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.mapper.AtencionMapper;
import com.proyecto.agendaty.model.enums.EstadoTurno;
import com.proyecto.agendaty.repository.AuditoriaRepository;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.AtencionRepository;
import com.proyecto.agendaty.repository.PausaRepository;
import com.proyecto.agendaty.repository.TurnoRepository;
import com.proyecto.agendaty.service.AtencionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.persistence.EntityManager;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AtencionServiceImpTest {

    @Mock private AtencionRepository atencionRepository;
    @Mock private TurnoRepository turnoRepository;
    @Mock private AsesorRepository asesorRepository;
    @Mock private PausaRepository pausaRepository;
    @Mock private AuditoriaRepository auditoriaRepository;
    @Mock private AtencionMapper atencionMapper;
    @Mock private Clock clock;
    @Mock private EntityManager entityManager;

    @InjectMocks
    private AtencionServiceImp service;

    private Asesor asesor;
    private Turno turno;
    private Instant inicio;
    private Instant fin;

    @BeforeEach
    void setUp() {
        inicio = Instant.parse("2026-11-10T10:00:00Z");
        fin = Instant.parse("2026-11-10T10:30:00Z");
        when(clock.instant()).thenReturn(inicio);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        asesor = new Asesor();
        asesor.setId(7);
        asesor.setNombre("Maria Asesora");
        asesor.setDisponible(true);

        turno = new Turno();
        turno.setId(1L);
        turno.setNumeroTurno("CEDN-00001");
        turno.setEstado(EstadoTurno.LLAMADO);
        turno.setAsesor(asesor);
    }

    /* ==================== RN4: asesor con un solo turno activo ==================== */

    @Test
    void iniciarAtencion_cuandoAsesorYaTieneTurnoActivo_deberiaLanzarExcepcion_RN4() {
        Asesor otroAsesor = new Asesor();
        otroAsesor.setId(8);
        otroAsesor.setNombre("Otro");
        otroAsesor.setDisponible(true);

        Turno turnoLlamado = new Turno();
        turnoLlamado.setId(1L);
        turnoLlamado.setEstado(EstadoTurno.LLAMADO);
        turnoLlamado.setAsesor(asesor);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoLlamado));
        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesor));
        //asesor ya tiene un turno en EN_ATENCION
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_ATENCION, asesor)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.iniciarAtencion(1L, 7),
                "Un asesor no puede tener dos turnos activos");
    }

    @Test
    void iniciarAtencion_cuandoTurnoYaAtendidoPorOtroAsesor_deberiaRechazar_RN1() {
        Asesor otroAsesor = new Asesor();
        otroAsesor.setId(8);
        otroAsesor.setNombre("Otro");

        Turno turnoOcupado = new Turno();
        turnoOcupado.setId(1L);
        turnoOcupado.setEstado(EstadoTurno.LLAMADO);
        turnoOcupado.setAsesor(otroAsesor); // asignado a otro asesor

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoOcupado));
        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesor));
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_ATENCION, asesor)).thenReturn(false);
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_PAUSA, asesor)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.iniciarAtencion(1L, 7),
                "RN1: un turno no puede ser atendido por dos asesores");
    }

    @Test
    void iniciarAtencion_guardaTodosLosCamposYAsignaLock() {
        Turno turnoEnEspera = new Turno();
        turnoEnEspera.setId(1L);
        turnoEnEspera.setEstado(EstadoTurno.EN_ESPERA);

        Asesor asesorSinTurno = new Asesor();
        asesorSinTurno.setId(7);
        asesorSinTurno.setNombre("Maria Asesora");
        asesorSinTurno.setDisponible(true);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoEnEspera));
        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesorSinTurno));
        when(turnoRepository.existsByEstadoAndAsesor(any(EstadoTurno.class), any(Asesor.class))).thenReturn(false);
        when(atencionRepository.save(any(Atencion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(atencionMapper.toResponse(any(Atencion.class))).thenAnswer(inv -> {
            Atencion a = inv.getArgument(0);
            return new AtencionResponse(a.getId(), a.getTurno().getId(), a.getTurno().getNumeroTurno(),
                    a.getAsesor().getId(), a.getAsesor().getNombre(), a.getFechaInicio(), null, null, null,
                    null, null, null);
        });

        AtencionResponse r = service.iniciarAtencion(1L, 7);

        assertNotNull(r);
        ArgumentCaptor<Atencion> atencionCaptor = ArgumentCaptor.forClass(Atencion.class);
        verify(atencionRepository).save(atencionCaptor.capture());
        Atencion guardada = atencionCaptor.getValue();

        assertEquals(asesorSinTurno, guardada.getAsesor());
        assertEquals(turnoEnEspera, guardada.getTurno());
        assertNotNull(guardada.getFechaInicio(), "RN6: fechaInicio seteada");
        assertEquals(0, guardada.getTotalPausasMinutos().intValue());
        assertNull(guardada.getTiempoEsperaMinutos());
        assertNull(guardada.getTiempoAtencionMinutos());

        ArgumentCaptor<Turno> turnoCaptor = ArgumentCaptor.forClass(Turno.class);
        verify(turnoRepository).save(turnoCaptor.capture());
        Turno guardado = turnoCaptor.getValue();

        assertEquals(EstadoTurno.EN_ATENCION, guardado.getEstado());
        assertEquals(asesorSinTurno, guardado.getAsesor());
        assertNotNull(guardado.getFechaLlamado(), "EN_ESPERA=> ahora setFechaLlamado");
    }

    @Test
    void pausarAtencion_guardaTurnoYPausa() {
        turno.setEstado(EstadoTurno.EN_ATENCION);

        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setPausas(new java.util.ArrayList<>());
        atencion.setFechaInicio(java.time.LocalDateTime.of(2026, 11, 10, 10, 0));

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));
        when(pausaRepository.save(any(Pausa.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(atencionMapper.toResponse(any(Atencion.class))).thenAnswer(inv -> {
            Atencion a = inv.getArgument(0);
            return new AtencionResponse(a.getId(), a.getTurno().getId(), a.getTurno().getNumeroTurno(),
                    a.getAsesor().getId(), a.getAsesor().getNombre(), a.getFechaInicio(), a.getFechaFin(),
                    null, null, null, null, null);
        });
        when(atencionRepository.findById(1L)).thenReturn(Optional.of(atencion));

        service.pausarAtencion(1L, 7);

        ArgumentCaptor<Pausa> pausaCaptor = ArgumentCaptor.forClass(Pausa.class);
        verify(pausaRepository).save(pausaCaptor.capture());
        assertNotNull(pausaCaptor.getValue().getFechaInicio());

        ArgumentCaptor<Turno> turnoCaptor = ArgumentCaptor.forClass(Turno.class);
        verify(turnoRepository).save(turnoCaptor.capture());
        assertEquals(EstadoTurno.EN_PAUSA, turnoCaptor.getValue().getEstado());
    }

    @Test
    void reanudarAtencion_imputaPausaConDuracion() {
        turno.setEstado(EstadoTurno.EN_PAUSA);

        Pausa pausa = new Pausa();
        pausa.setFechaInicio(java.time.LocalDateTime.of(2026, 11, 10, 10, 10));

        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setPausas(new java.util.ArrayList<>(List.of(pausa)));

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));
        when(pausaRepository.save(any(Pausa.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(atencionMapper.toResponse(any(Atencion.class))).thenAnswer(inv -> {
            Atencion a = inv.getArgument(0);
            return new AtencionResponse(a.getId(), a.getTurno().getId(), a.getTurno().getNumeroTurno(),
                    a.getAsesor().getId(), a.getAsesor().getNombre(), a.getFechaInicio(), a.getFechaFin(),
                    null, null, null, null, null);
        });
        when(atencionRepository.findById(1L)).thenReturn(Optional.of(atencion));

        service.reanudarAtencion(1L, 7);

        ArgumentCaptor<Pausa> pausaCaptor = ArgumentCaptor.forClass(Pausa.class);
        verify(pausaRepository).save(pausaCaptor.capture());
        assertNotNull(pausaCaptor.getValue().getFechaFin());
        assertNotNull(pausaCaptor.getValue().getDuracionMinutos());
    }

    @Test
    void iniciarAtencion_casoFeliz_deberiaIniciarAtencion() {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesor));
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_ATENCION, asesor)).thenReturn(false);
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_PAUSA, asesor)).thenReturn(false);
        when(atencionRepository.save(any(Atencion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(atencionMapper.toResponse(any(Atencion.class))).thenAnswer(inv -> {
            Atencion a = inv.getArgument(0);
            return new AtencionResponse(a.getId(), a.getTurno().getId(), a.getTurno().getNumeroTurno(),
                    a.getAsesor().getId(), a.getAsesor().getNombre(), a.getFechaInicio(), null, null, null,
                    null, null, null);
        });

        AtencionResponse respuesta = service.iniciarAtencion(1L, 7);

        assertNotNull(respuesta);
        assertEquals(7, respuesta.asesorId());
        assertNotNull(respuesta.fechaInicio());
    }

    /* ==================== RN5: resultado obligatorio ==================== */

    @Test
    void finalizarAtencion_sinResultado_deberiaLanzarExcepcion_RN5() {
        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setPausas(java.util.List.of());

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));

        assertThrows(BadRequestException.class,
                () -> service.finalizarAtencion(1L, 7, new AtencionRequest(null, null)),
                "RN5: no se puede finalizar sin resultado");
    }

    @Test
    void finalizarAtencion_cuandoResultadoVacio_deberiaLanzarExcepcion_RN5() {
        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setPausas(java.util.List.of());

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));

        assertThrows(BadRequestException.class,
                () -> service.finalizarAtencion(1L, 7, new AtencionRequest("   ", null)),
                "RN5: resultado en blanco no es valido");
    }

    /* ==================== RN6: calculo de tiempos ==================== */

    @Test
    void finalizarAtencion_deberiaCalcularTiemposEsperaYAtencion_RN6() {
        // Turno creado a las 10:00, llamado a las 10:05 => espera 5 min
        turno.setEstado(EstadoTurno.EN_ATENCION);
        java.time.LocalDateTime fechaCreacion = java.time.LocalDateTime.of(2026, 11, 10, 10, 0);
        java.time.LocalDateTime fechaLlamado = java.time.LocalDateTime.of(2026, 11, 10, 10, 5);
        turno.setFechaCreacion(fechaCreacion);
        turno.setFechaLlamado(fechaLlamado);

        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setPausas(java.util.List.of());
        atencion.setFechaInicio(java.time.LocalDateTime.of(2026, 11, 10, 10, 5));

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesor));
        when(atencionRepository.save(any(Atencion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(asesorRepository.save(any(Asesor.class))).thenAnswer(inv -> inv.getArgument(0));
        when(atencionMapper.toResponse(any(Atencion.class))).thenAnswer(inv -> {
            Atencion a = inv.getArgument(0);
            return new AtencionResponse(a.getId(), a.getTurno().getId(), a.getTurno().getNumeroTurno(),
                    a.getAsesor().getId(), a.getAsesor().getNombre(), a.getFechaInicio(), a.getFechaFin(),
                    a.getObservaciones(), a.getResultado(), a.getTiempoEsperaMinutos(),
                    a.getTiempoAtencionMinutos(), a.getTotalPausasMinutos());
        });

        // clock a las 10:20 => atencion dura 15 min (10:05 a 10:20)
        when(clock.instant()).thenReturn(Instant.parse("2026-11-10T10:20:00Z"));

        AtencionResponse respuesta = service.finalizarAtencion(1L, 7,
                new AtencionRequest("Atendido correctamente", "Observacion"));

        assertNotNull(respuesta);
        assertEquals("Atendido correctamente", respuesta.resultado(), "RN5: resultado registrado");
        assertEquals(Integer.valueOf(5), respuesta.tiempoEsperaMinutos(), "RN6: espera 5 min");
        assertEquals(Integer.valueOf(15), respuesta.tiempoAtencionMinutos(), "RN6: atencion 15 min");
        assertEquals(EstadoTurno.FINALIZADO, turno.getEstado());
    }

    /* ==================== Pausa / Reanudar ==================== */

    @Test
    void pausarAtencion_cuandoNoEsElAsesor_deberiaRechazar() {
        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);

        Asesor otro = new Asesor();
        otro.setId(99);
        otro.setNombre("Otro");

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));

        assertThrows(BadRequestException.class, () -> service.pausarAtencion(1L, 99));
    }

    @Test
    void pausarAtencion_casoFeliz_creaPausaYActualizaEstado() {
        turno.setEstado(EstadoTurno.EN_ATENCION);

        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setPausas(new java.util.ArrayList<>());
        atencion.setFechaInicio(java.time.LocalDateTime.of(2026, 11, 10, 10, 0));

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));
        when(pausaRepository.save(any(Pausa.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(atencionMapper.toResponse(any(Atencion.class))).thenAnswer(inv -> {
            Atencion a = inv.getArgument(0);
            return new AtencionResponse(a.getId(), a.getTurno().getId(), a.getTurno().getNumeroTurno(),
                    a.getAsesor().getId(), a.getAsesor().getNombre(), a.getFechaInicio(), a.getFechaFin(),
                    null, null, null, null, null);
        });
        when(atencionRepository.findById(1L)).thenReturn(Optional.of(atencion));

        service.pausarAtencion(1L, 7);

        assertEquals(EstadoTurno.EN_PAUSA, turno.getEstado());
        verify(pausaRepository).save(any(Pausa.class));
    }

    @Test
    void pausarAtencion_estadoNoEnAtencion_lanzaExcepcion() {
        turno.setEstado(EstadoTurno.FINALIZADO);

        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));

        assertThrows(BadRequestException.class, () -> service.pausarAtencion(1L, 7));
    }

    @Test
    void reanudarAtencion_sinPausaActiva_lanzaExcepcion() {
        turno.setEstado(EstadoTurno.EN_PAUSA);

        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setPausas(new java.util.ArrayList<>());

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));

        assertThrows(BadRequestException.class, () -> service.reanudarAtencion(1L, 7));
    }

    @Test
    void reanudarAtencion_estadoNoEnPausa_lanzaExcepcion() {
        turno.setEstado(EstadoTurno.EN_ATENCION);

        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setPausas(new java.util.ArrayList<>());

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));

        assertThrows(BadRequestException.class, () -> service.reanudarAtencion(1L, 7));
    }

    @Test
    void reanudarAtencion_casoFeliz_cierraPausaYCalculaDuracion() {
        turno.setEstado(EstadoTurno.EN_PAUSA);

        Pausa pausa = new Pausa();
        pausa.setId(9L);
        pausa.setFechaInicio(java.time.LocalDateTime.of(2026, 11, 10, 10, 10));

        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setPausas(new java.util.ArrayList<>(List.of(pausa)));
        pausa.setAtencion(atencion);

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));
        when(pausaRepository.save(any(Pausa.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(atencionMapper.toResponse(any(Atencion.class))).thenAnswer(inv -> {
            Atencion a = inv.getArgument(0);
            return new AtencionResponse(a.getId(), a.getTurno().getId(), a.getTurno().getNumeroTurno(),
                    a.getAsesor().getId(), a.getAsesor().getNombre(), a.getFechaInicio(), a.getFechaFin(),
                    null, null, null, null, null);
        });
        when(atencionRepository.findById(1L)).thenReturn(Optional.of(atencion));

        service.reanudarAtencion(1L, 7);

        assertEquals(EstadoTurno.EN_ATENCION, turno.getEstado());
        assertNotNull(pausa.getFechaFin());
        assertNotNull(pausa.getDuracionMinutos());
    }

    @Test
    void obtenerAtencionPorTurno_existente_retornaAtencion() {
        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setFechaInicio(java.time.LocalDateTime.of(2026, 11, 10, 10, 0));

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));
        when(atencionMapper.toResponse(any(Atencion.class))).thenAnswer(inv -> {
            Atencion a = inv.getArgument(0);
            return new AtencionResponse(a.getId(), a.getTurno().getId(), a.getTurno().getNumeroTurno(),
                    a.getAsesor().getId(), a.getAsesor().getNombre(), a.getFechaInicio(), a.getFechaFin(),
                    a.getObservaciones(), a.getResultado(), a.getTiempoEsperaMinutos(),
                    a.getTiempoAtencionMinutos(), a.getTotalPausasMinutos());
        });

        AtencionResponse r = service.obtenerAtencionPorTurno(1L);

        assertNotNull(r);
        assertEquals(7, r.asesorId());
    }

    @Test
    void obtenerAtencionPorTurno_inexistente_lanzaResourceNotFound() {
        when(atencionRepository.findByTurnoId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerAtencionPorTurno(99L));
    }

    @Test
    void finalizarAtencion_cuandoAsesorNoEsElQueAtendio_lanzaExcepcion() {
        turno.setEstado(EstadoTurno.EN_ATENCION);

        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        Asesor otro = new Asesor();
        otro.setId(99);
        otro.setNombre("Otro");
        atencion.setAsesor(otro);
        atencion.setPausas(new java.util.ArrayList<>());

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));

        assertThrows(BadRequestException.class,
                () -> service.finalizarAtencion(1L, 7, new AtencionRequest("resultado", null)));
    }

    @Test
    void finalizarAtencion_estadoNoAtencionNiPausa_lanzaExcepcion() {
        turno.setEstado(EstadoTurno.FINALIZADO);

        Atencion atencion = new Atencion();
        atencion.setId(1L);
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setPausas(new java.util.ArrayList<>());

        when(atencionRepository.findByTurnoId(1L)).thenReturn(Optional.of(atencion));

        assertThrows(BadRequestException.class,
                () -> service.finalizarAtencion(1L, 7, new AtencionRequest("resultado", null)));
    }

    @Test
    void iniciarAtencion_asesorNoDisponible_lanzaExcepcion() {
        asesor.setDisponible(false);
        turno.setEstado(EstadoTurno.EN_ESPERA);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesor));
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_ATENCION, asesor)).thenReturn(false);
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_PAUSA, asesor)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.iniciarAtencion(1L, 7));
    }

    @Test
    void iniciarAtencion_estadoFinalizado_lanzaExcepcion() {
        turno.setEstado(EstadoTurno.FINALIZADO);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        assertThrows(BadRequestException.class, () -> service.iniciarAtencion(1L, 7));
    }

    /* ==================== Auditoria ==================== */

    @Test
    void iniciarAtencion_deberiaRegistrarAuditoria() {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesor));
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_ATENCION, asesor)).thenReturn(false);
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_PAUSA, asesor)).thenReturn(false);
        when(atencionRepository.save(any(Atencion.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(atencionMapper.toResponse(any(Atencion.class))).thenReturn(new AtencionResponse(
                1L, 1L, "CEDN-00001", 7, "Maria", null, null, null, null, null, null, null));

        service.iniciarAtencion(1L, 7);

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaRepository).save(captor.capture());
        assertEquals("INICIAR_ATENCION", captor.getValue().getAccion());
    }
}

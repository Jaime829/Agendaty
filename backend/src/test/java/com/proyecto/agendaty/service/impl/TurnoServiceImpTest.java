package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.TurnoRequest;
import com.proyecto.agendaty.dto.TurnoResponse;
import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ciudadano;
import com.proyecto.agendaty.entity.TipoTramite;
import com.proyecto.agendaty.entity.Turno;
import com.proyecto.agendaty.entity.Auditoria;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.mapper.TurnoMapper;
import com.proyecto.agendaty.model.enums.EstadoTurno;
import com.proyecto.agendaty.model.enums.PrioridadTurno;
import com.proyecto.agendaty.repository.AuditoriaRepository;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import com.proyecto.agendaty.repository.TipoTramiteRepository;
import com.proyecto.agendaty.repository.TurnoRepository;
import com.proyecto.agendaty.service.AuthService;
import com.proyecto.agendaty.service.TurnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.ArgumentCaptor;
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
class TurnoServiceImpTest {

    @Mock private TurnoRepository turnoRepository;
    @Mock private CiudadanoRepository ciudadanoRepository;
    @Mock private TipoTramiteRepository tipoTramiteRepository;
    @Mock private AsesorRepository asesorRepository;
    @Mock private AuditoriaRepository auditoriaRepository;
    @Mock private TurnoMapper turnoMapper;
    @Mock private Clock clock;
    @Mock private EntityManager entityManager;
    @Mock private AuthService authService;

    @InjectMocks
    private TurnoServiceImp service;

    private Ciudadano ciudadano;
    private TipoTramite tipoTramite;
    private Asesor asesor;
    private Instant fixedInstant;

    @BeforeEach
    void setUp() {
        fixedInstant = Instant.parse("2026-11-10T10:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        doNothing().when(authService).validarRolCualquiera(any(), any());

        ciudadano = new Ciudadano();
        ciudadano.setId(1);
        ciudadano.setNombre("Juan Perez");
        ciudadano.setDocumento("12345678");
        ciudadano.setTelefono("3001234567");

        tipoTramite = new TipoTramite();
        tipoTramite.setId(10L);
        tipoTramite.setNombre("Cedula");
        tipoTramite.setCodigo("CED");
        tipoTramite.setEsPrioritarioPorDefecto(false);
        tipoTramite.setActivo(true);

        asesor = new Asesor();
        asesor.setId(7);
        asesor.setNombre("Maria Asesora");
        asesor.setDisponible(true);
    }

    /* ==================== CA1-CA4: generar turno ==================== */

    @Test
    void generarTurno_deberiaPermitirSeleccionarTipoTramite_yQuedarEnEspera_CA1_CA3() {
        TurnoRequest request = new TurnoRequest(1, 10L, false);

        when(ciudadanoRepository.findById(1)).thenReturn(Optional.of(ciudadano));
        when(tipoTramiteRepository.findById(10L)).thenReturn(Optional.of(tipoTramite));
        when(turnoRepository.findAll()).thenReturn(List.of());
        Turno turnoGuardado = new Turno();
        turnoGuardado.setId(100L);
        turnoGuardado.setNumeroTurno("CEDN-00001");
        turnoGuardado.setEstado(EstadoTurno.EN_ESPERA);
        turnoGuardado.setPrioridad(PrioridadTurno.NORMAL);
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoMapper.toResponse(any(Turno.class))).thenAnswer(inv -> {
            Turno t = inv.getArgument(0);
            return new TurnoResponse(t.getId(), t.getNumeroTurno(), t.getEstado(), t.getPrioridad(),
                    null, null, null, 1, "Juan Perez", 10L, "Cedula", null, null);
        });

        TurnoResponse respuesta = service.generarTurno(request, 1);

        assertNotNull(respuesta);
        assertEquals(EstadoTurno.EN_ESPERA, respuesta.estado(), "CA3: debe quedar en EN_ESPERA");
        assertEquals("CEDN-00001", respuesta.numeroTurno(), "CA2: numero unico");
        assertEquals(PrioridadTurno.NORMAL, respuesta.prioridad());
        verify(turnoRepository).save(any(Turno.class));
    }

    @Test
    void generarTurno_deberiaDiferenciarPrioritarioYNormal_CA4() {
        // Normal
        TurnoRequest normal = new TurnoRequest(1, 10L, false);
        // Prioritario
        TurnoRequest prioritario = new TurnoRequest(1, 10L, true);

        when(ciudadanoRepository.findById(1)).thenReturn(Optional.of(ciudadano));
        when(tipoTramiteRepository.findById(10L)).thenReturn(Optional.of(tipoTramite));
        when(turnoRepository.findAll()).thenReturn(List.of(), List.of());
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoMapper.toResponse(any(Turno.class))).thenAnswer(inv -> {
            Turno t = inv.getArgument(0);
            return new TurnoResponse(t.getId(), t.getNumeroTurno(), t.getEstado(), t.getPrioridad(),
                    null, null, null, 1, "Juan Perez", 10L, "Cedula", null, null);
        });

        TurnoResponse rNormal = service.generarTurno(normal, 1);
        TurnoResponse rPrioritario = service.generarTurno(prioritario, 1);

        assertEquals(PrioridadTurno.NORMAL, rNormal.prioridad());
        assertEquals(PrioridadTurno.PRIORITARIO, rPrioritario.prioridad());
    }

    @Test
    void generarTurno_cuandoTipoTramiteNoExiste_deberiaLanzarExcepcion() {
        TurnoRequest request = new TurnoRequest(1, 999L, false);
        when(ciudadanoRepository.findById(1)).thenReturn(Optional.of(ciudadano));
        when(tipoTramiteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.generarTurno(request, 1));
    }

    @Test
    void generarTurno_cuandoCiudadanoNoExiste_deberiaLanzarExcepcion() {
        TurnoRequest request = new TurnoRequest(999, 10L, false);
        when(ciudadanoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.generarTurno(request, 1));
    }

    /* ==================== CA5 + RN3: llamar siguiente ==================== */

    @Test
    void llamarSiguiente_deberiaDevolverElSiguienteTurnoDisponible_CA5() {
        Turno turnoEnEspera = new Turno();
        turnoEnEspera.setId(1L);
        turnoEnEspera.setNumeroTurno("CEDN-00001");
        turnoEnEspera.setEstado(EstadoTurno.EN_ESPERA);
        turnoEnEspera.setPrioridad(PrioridadTurno.NORMAL);
        turnoEnEspera.setCiudadano(ciudadano);
        turnoEnEspera.setTipoTramite(tipoTramite);

        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesor));
        when(turnoRepository.findByEstadoAndPrioridadOrderByCreacion(EstadoTurno.EN_ESPERA, PrioridadTurno.PRIORITARIO))
                .thenReturn(List.of());
        when(turnoRepository.findByEstadoAndPrioridadOrderByCreacion(EstadoTurno.EN_ESPERA, PrioridadTurno.NORMAL))
                .thenReturn(List.of(turnoEnEspera));
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoEnEspera));
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_ATENCION, asesor)).thenReturn(false);
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_PAUSA, asesor)).thenReturn(false);
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoMapper.toResponse(any(Turno.class))).thenAnswer(inv -> {
            Turno t = inv.getArgument(0);
            return new TurnoResponse(t.getId(), t.getNumeroTurno(), t.getEstado(), t.getPrioridad(),
                    null, null, null, 1, "Juan Perez", 10L, "Cedula", 7, "Maria Asesora");
        });

        TurnoResponse respuesta = service.llamarSiguiente(7);

        assertNotNull(respuesta);
        assertEquals(EstadoTurno.LLAMADO, respuesta.estado());
        assertEquals(7, respuesta.asesorId());
        verify(turnoRepository).save(turnoEnEspera);
    }

    @Test
    void llamarSiguiente_deberiaPriorizarTurnosPrioritarios_RN3() {
        Turno prioritario = new Turno();
        prioritario.setId(2L);
        prioritario.setNumeroTurno("CEDP-00001");
        prioritario.setEstado(EstadoTurno.EN_ESPERA);
        prioritario.setPrioridad(PrioridadTurno.PRIORITARIO);

        Turno normal = new Turno();
        normal.setId(1L);
        normal.setNumeroTurno("CEDN-00001");
        normal.setEstado(EstadoTurno.EN_ESPERA);
        normal.setPrioridad(PrioridadTurno.NORMAL);

        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesor));
        when(turnoRepository.findByEstadoAndPrioridadOrderByCreacion(EstadoTurno.EN_ESPERA, PrioridadTurno.PRIORITARIO))
                .thenReturn(List.of(prioritario));
        when(turnoRepository.findByEstadoAndPrioridadOrderByCreacion(EstadoTurno.EN_ESPERA, PrioridadTurno.NORMAL))
                .thenReturn(List.of(normal));
        when(turnoRepository.findById(2L)).thenReturn(Optional.of(prioritario));
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_ATENCION, asesor)).thenReturn(false);
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_PAUSA, asesor)).thenReturn(false);
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoMapper.toResponse(any(Turno.class))).thenAnswer(inv -> {
            Turno t = inv.getArgument(0);
            return new TurnoResponse(t.getId(), t.getNumeroTurno(), t.getEstado(), t.getPrioridad(),
                    null, null, null, 1, "Juan Perez", 10L, "Cedula", 7, "Maria Asesora");
        });

        TurnoResponse respuesta = service.llamarSiguiente(7);

        assertEquals(2L, respuesta.id(), "RN3: debe llamar al prioritario antes que al normal");
        assertEquals(PrioridadTurno.PRIORITARIO, respuesta.prioridad());
    }

    @Test
    void llamarSiguiente_cuandoAsesorTieneTurnoActivo_deberiaRechazar_RN4() {
        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesor));
        when(turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_ATENCION, asesor)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> service.llamarSiguiente(7));
    }

    @Test
    void llamarSiguiente_cuandoNoHayTurnosEnEspera_deberiaLanzarExcepcion() {
        when(asesorRepository.findById(7)).thenReturn(Optional.of(asesor));
        when(turnoRepository.findByEstadoAndPrioridadOrderByCreacion(EstadoTurno.EN_ESPERA, PrioridadTurno.PRIORITARIO))
                .thenReturn(List.of());
        when(turnoRepository.findByEstadoAndPrioridadOrderByCreacion(EstadoTurno.EN_ESPERA, PrioridadTurno.NORMAL))
                .thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> service.llamarSiguiente(7));
    }

    /* ==================== RN2: no atendido ==================== */

    @Test
    void marcarNoAtendido_cuandoTurnoEstaLlamado_deberiaPasarANO_ATENDIDO_RN2() {
        Turno turnoLlamado = new Turno();
        turnoLlamado.setId(1L);
        turnoLlamado.setNumeroTurno("CEDN-00001");
        turnoLlamado.setEstado(EstadoTurno.LLAMADO);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoLlamado));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoMapper.toResponse(any(Turno.class))).thenAnswer(inv -> {
            Turno t = inv.getArgument(0);
            return new TurnoResponse(t.getId(), t.getNumeroTurno(), t.getEstado(), t.getPrioridad(),
                    null, null, null, 1, "Juan Perez", 10L, "Cedula", null, null);
        });

        TurnoResponse respuesta = service.marcarNoAtendido(1L);

        assertEquals(EstadoTurno.NO_ATENDIDO, respuesta.estado(), "RN2: pasa a NO_ATENDIDO");
        verify(turnoRepository).save(turnoLlamado);
    }

    @Test
    void marcarNoAtendido_cuandoTurnoNoEstaLlamado_deberiaLanzarExcepcion_RN2() {
        Turno turnoEspera = new Turno();
        turnoEspera.setId(1L);
        turnoEspera.setEstado(EstadoTurno.EN_ESPERA);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turnoEspera));

        assertThrows(BadRequestException.class, () -> service.marcarNoAtendido(1L));
    }

    /* ==================== Auditoría ==================== */

    @Test
    void generarTurno_deberiaRegistrarAuditoria() {
        TurnoRequest request = new TurnoRequest(1, 10L, false);
        when(ciudadanoRepository.findById(1)).thenReturn(Optional.of(ciudadano));
        when(tipoTramiteRepository.findById(10L)).thenReturn(Optional.of(tipoTramite));
        when(turnoRepository.findAll()).thenReturn(List.of());
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoMapper.toResponse(any(Turno.class))).thenReturn(new TurnoResponse(
                1L, "CEDN-00001", EstadoTurno.EN_ESPERA, PrioridadTurno.NORMAL,
                null, null, null, 1, "Juan Perez", 10L, "Cedula", null, null));

        service.generarTurno(request, 1);

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaRepository).save(captor.capture());
        Auditoria a = captor.getValue();
        assertEquals("GENERAR_TURNO", a.getAccion());
        assertEquals("Turno", a.getEntidad());
    }
}

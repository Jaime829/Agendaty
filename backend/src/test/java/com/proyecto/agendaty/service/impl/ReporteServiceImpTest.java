package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Atencion;
import com.proyecto.agendaty.entity.TipoTramite;
import com.proyecto.agendaty.entity.Turno;
import com.proyecto.agendaty.model.enums.EstadoTurno;
import com.proyecto.agendaty.model.enums.PrioridadTurno;
import com.proyecto.agendaty.repository.AtencionRepository;
import com.proyecto.agendaty.repository.TurnoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImpTest {

    @Mock
    private AtencionRepository atencionRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @InjectMocks
    private ReporteServiceImp reporteService;

    private Asesor crearAsesor(Integer id) {
        Asesor a = new Asesor();
        a.setId(id);
        a.setNombre("Asesor " + id);
        a.setDisponible(true);
        a.setCorreo("asesor" + id + "@turnos.local");
        a.setUsername("asesor" + id + "@turnos.local");
        a.setPassword("hash");
        return a;
    }

    private TipoTramite crearTipo(Long id, String codigo, String nombre) {
        TipoTramite t = new TipoTramite();
        t.setId(id);
        t.setCodigo(codigo);
        t.setNombre(nombre);
        t.setActivo(true);
        return t;
    }

    private Atencion crearAtencion(Long idTurno, Asesor asesor, TipoTramite tipo,
                                   Integer tiempoEspera, LocalDateTime inicio) {
        Atencion a = new Atencion();
        a.setId(idTurno * 100);
        Turno turno = new Turno();
        turno.setId(idTurno);
        turno.setNumeroTurno("CEDN-0000" + idTurno);
        turno.setEstado(EstadoTurno.FINALIZADO);
        turno.setPrioridad(PrioridadTurno.NORMAL);
        turno.setFechaCreacion(inicio.minusMinutes(10));
        turno.setFechaLlamado(inicio.minusMinutes(5));
        turno.setCiudadano(new com.proyecto.agendaty.entity.Ciudadano());
        turno.setTipoTramite(tipo);
        a.setTurno(turno);
        a.setAsesor(asesor);
        a.setFechaInicio(inicio);
        a.setTiempoEsperaMinutos(tiempoEspera);
        return a;
    }

    private Turno crearTurno(Long id, Asesor asesor, TipoTramite tipo, EstadoTurno estado, LocalDateTime creacion) {
        Turno t = new Turno();
        t.setId(id);
        t.setNumeroTurno("CEDN-0000" + id);
        t.setEstado(estado);
        t.setPrioridad(PrioridadTurno.NORMAL);
        t.setFechaCreacion(creacion);
        t.setCiudadano(new com.proyecto.agendaty.entity.Ciudadano());
        t.setTipoTramite(tipo);
        t.setAsesor(asesor);
        return t;
    }

    /* ==================== tiempoEsperaPromedio ==================== */

    @Test
    void tiempoEsperaPromedio_sinAtencionesDevuelveCero() {
        when(atencionRepository.findAll()).thenReturn(List.of());

        Map<String, Object> r = reporteService.tiempoEsperaPromedio(null, null, null, null);

        assertEquals("tiempo_espera_promedio_minutos", r.get("metrica"));
        assertEquals(0.0, r.get("valor"));
        assertEquals(0, r.get("cantidad_atenciones"));
    }

    @Test
    void tiempoEsperaPromedio_ignoraAtencionesSinTiempoEspera() {
        Asesor asesor = crearAsesor(1);
        Atencion a1 = crearAtencion(1L, asesor, crearTipo(1L, "CED", "Cédula"), null, LocalDateTime.now().minusHours(1));
        Atencion a2 = crearAtencion(2L, asesor, crearTipo(1L, "CED", "Cédula"), 30, LocalDateTime.now().minusHours(2));

        when(atencionRepository.findAll()).thenReturn(List.of(a1, a2));

        Map<String, Object> r = reporteService.tiempoEsperaPromedio(null, null, null, null);

        assertEquals(30.0, (Double) r.get("valor"));
        assertEquals(1, (Integer) r.get("cantidad_atenciones"));
    }

    @Test
    void tiempoEsperaPromedio_promediaCorrectamente() {
        Asesor asesor = crearAsesor(1);
        Atencion a1 = crearAtencion(1L, asesor, crearTipo(1L, "CED", "Cédula"), 10, LocalDateTime.now().minusHours(3));
        Atencion a2 = crearAtencion(2L, asesor, crearTipo(1L, "CED", "Cédula"), 20, LocalDateTime.now().minusHours(2));

        when(atencionRepository.findAll()).thenReturn(List.of(a1, a2));

        Map<String, Object> r = reporteService.tiempoEsperaPromedio(null, null, null, null);

        assertEquals(15.0, (Double) r.get("valor"));
        assertEquals(2, (Integer) r.get("cantidad_atenciones"));
    }

    @Test
    void tiempoEsperaPromedio_filtraPorAsesor() {
        Asesor asesor1 = crearAsesor(1);
        Asesor asesor2 = crearAsesor(2);
        Atencion a1 = crearAtencion(1L, asesor1, crearTipo(1L, "CED", "Cédula"), 10, LocalDateTime.now().minusHours(2));
        Atencion a2 = crearAtencion(2L, asesor2, crearTipo(1L, "CED", "Cédula"), 50, LocalDateTime.now().minusHours(1));

        when(atencionRepository.findAll()).thenReturn(List.of(a1, a2));

        Map<String, Object> r = reporteService.tiempoEsperaPromedio(null, null, 1, null);

        assertEquals(10.0, (Double) r.get("valor"));
        assertEquals(1, (Integer) r.get("cantidad_atenciones"));
    }

    @Test
    void tiempoEsperaPromedio_filtraPorTipoTramite() {
        Asesor asesor = crearAsesor(1);
        Atencion a1 = crearAtencion(1L, asesor, crearTipo(1L, "CED", "Cédula"), 10, LocalDateTime.now().minusHours(2));
        Atencion a2 = crearAtencion(2L, asesor, crearTipo(2L, "PAS", "Pasaporte"), 50, LocalDateTime.now().minusHours(1));

        when(atencionRepository.findAll()).thenReturn(List.of(a1, a2));

        Map<String, Object> r = reporteService.tiempoEsperaPromedio(null, null, null, 1L);

        assertEquals(10.0, (Double) r.get("valor"));
    }

    @Test
    void tiempoEsperaPromedio_filtraPorFechaDesde() {
        Asesor asesor = crearAsesor(1);
        LocalDateTime ahora = LocalDateTime.now();
        Atencion a1 = crearAtencion(1L, asesor, crearTipo(1L, "CED", "Cédula"), 10, ahora.minusMinutes(30));
        Atencion a2 = crearAtencion(2L, asesor, crearTipo(1L, "CED", "Cédula"), 50, ahora.minusDays(2));

        when(atencionRepository.findAll()).thenReturn(List.of(a1, a2));

        Map<String, Object> r = reporteService.tiempoEsperaPromedio(ahora.minusHours(1), null, null, null);

        assertEquals(10.0, (Double) r.get("valor"));
        assertEquals(1, (Integer) r.get("cantidad_atenciones"));
    }

    @Test
    void tiempoEsperaPromedio_filtraPorFechaHasta() {
        Asesor asesor = crearAsesor(1);
        LocalDateTime ahora = LocalDateTime.now();
        Atencion a1 = crearAtencion(1L, asesor, crearTipo(1L, "CED", "Cédula"), 10, ahora.minusMinutes(30));
        Atencion a2 = crearAtencion(2L, asesor, crearTipo(1L, "CED", "Cédula"), 50, ahora.plusHours(2));

        when(atencionRepository.findAll()).thenReturn(List.of(a1, a2));

        Map<String, Object> r = reporteService.tiempoEsperaPromedio(null, ahora.plusMinutes(1), null, null);

        assertEquals(10.0, (Double) r.get("valor"));
    }

    /* ==================== turnosAtendidos ==================== */

    @Test
    void turnosAtendidos_cuentaFinalizadosYNoAtendidos() {
        TipoTramite tipo = crearTipo(1L, "CED", "Cédula");
        when(turnoRepository.findAll()).thenReturn(List.of(
                crearTurno(1L, null, tipo, EstadoTurno.FINALIZADO, LocalDateTime.now()),
                crearTurno(2L, null, tipo, EstadoTurno.FINALIZADO, LocalDateTime.now()),
                crearTurno(3L, null, tipo, EstadoTurno.NO_ATENDIDO, LocalDateTime.now()),
                crearTurno(4L, null, tipo, EstadoTurno.EN_ESPERA, LocalDateTime.now())
        ));

        Map<String, Object> r = reporteService.turnosAtendidos(null, null, null, null);

        assertEquals(2L, r.get("atendidos"));
        assertEquals(1L, r.get("no_atendidos"));
        assertEquals(3L, r.get("total"));
    }

    @Test
    void turnosAtendidos_sinTurnosEnElPeriodo() {
        when(turnoRepository.findAll()).thenReturn(List.of(
                crearTurno(1L, null, crearTipo(1L, "CED", "Cédula"), EstadoTurno.EN_ESPERA, LocalDateTime.now()),
                crearTurno(2L, null, crearTipo(1L, "CED", "Cédula"), EstadoTurno.CANCELADO, LocalDateTime.now())
        ));

        Map<String, Object> r = reporteService.turnosAtendidos(null, null, null, null);

        assertEquals(0L, r.get("atendidos"));
        assertEquals(0L, r.get("no_atendidos"));
        assertEquals(0L, r.get("total"));
    }

    @Test
    void turnosAtendidos_filtraPorAsesor() {
        Asesor asesor1 = crearAsesor(1);
        TipoTramite tipo = crearTipo(1L, "CED", "Cédula");
        when(turnoRepository.findAll()).thenReturn(List.of(
                crearTurno(1L, asesor1, tipo, EstadoTurno.FINALIZADO, LocalDateTime.now()),
                crearTurno(2L, null, tipo, EstadoTurno.FINALIZADO, LocalDateTime.now())
        ));

        Map<String, Object> r = reporteService.turnosAtendidos(null, null, 1, null);

        assertEquals(1L, r.get("atendidos"));
    }

    @Test
    void turnosAtendidos_filtraPorTipoTramite() {
        TipoTramite cedula = crearTipo(1L, "CED", "Cédula");
        TipoTramite pasaporte = crearTipo(2L, "PAS", "Pasaporte");
        when(turnoRepository.findAll()).thenReturn(List.of(
                crearTurno(1L, null, cedula, EstadoTurno.FINALIZADO, LocalDateTime.now()),
                crearTurno(2L, null, pasaporte, EstadoTurno.NO_ATENDIDO, LocalDateTime.now())
        ));

        Map<String, Object> r = reporteService.turnosAtendidos(null, null, null, 1L);

        assertEquals(1L, r.get("atendidos"));
        assertEquals(0L, r.get("no_atendidos"));
    }

    /* ==================== tramitesFrecuentes ==================== */

    @Test
    void tramitesFrecuentes_cuentaPorTramiteYOrdenaDesc() {
        TipoTramite cedula = crearTipo(1L, "CED", "Cédula");
        TipoTramite pasaporte = crearTipo(2L, "PAS", "Pasaporte");
        when(turnoRepository.findAll()).thenReturn(List.of(
                crearTurno(1L, null, pasaporte, EstadoTurno.FINALIZADO, LocalDateTime.now()),
                crearTurno(2L, null, cedula, EstadoTurno.FINALIZADO, LocalDateTime.now()),
                crearTurno(3L, null, cedula, EstadoTurno.FINALIZADO, LocalDateTime.now()),
                crearTurno(4L, null, cedula, EstadoTurno.FINALIZADO, LocalDateTime.now())
        ));

        List<Map<String, Object>> r = reporteService.tramitesFrecuentes(null, null, null);

        assertEquals(2, r.size());
        assertEquals("Cédula", r.get(0).get("tipo_tramite_nombre"));
        assertEquals(3, r.get(0).get("cantidad"));
        assertEquals("Pasaporte", r.get(1).get("tipo_tramite_nombre"));
        assertEquals(1, r.get(1).get("cantidad"));
    }

    @Test
    void tramitesFrecuentes_filtraPorTipoTramite() {
        TipoTramite cedula = crearTipo(1L, "CED", "Cédula");
        TipoTramite pasaporte = crearTipo(2L, "PAS", "Pasaporte");
        when(turnoRepository.findAll()).thenReturn(List.of(
                crearTurno(1L, null, cedula, EstadoTurno.FINALIZADO, LocalDateTime.now()),
                crearTurno(2L, null, pasaporte, EstadoTurno.FINALIZADO, LocalDateTime.now())
        ));

        List<Map<String, Object>> r = reporteService.tramitesFrecuentes(null, null, 1L);

        assertEquals(1, r.size());
        assertEquals(1L, r.get(0).get("tipo_tramite_id"));
    }

    @Test
    void tramitesFrecuentes_sinTurnos() {
        when(turnoRepository.findAll()).thenReturn(List.of());

        List<Map<String, Object>> r = reporteService.tramitesFrecuentes(null, null, null);

        assertTrue(r.isEmpty());
    }
}

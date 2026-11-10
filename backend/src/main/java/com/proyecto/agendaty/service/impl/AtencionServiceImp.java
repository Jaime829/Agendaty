package com.proyecto.agendaty.service.impl;

import com.proyecto.agendaty.dto.AtencionRequest;
import com.proyecto.agendaty.dto.AtencionResponse;
import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Atencion;
import com.proyecto.agendaty.entity.Auditoria;
import com.proyecto.agendaty.entity.Pausa;
import com.proyecto.agendaty.entity.Turno;
import com.proyecto.agendaty.model.enums.EstadoTurno;
import com.proyecto.agendaty.exceptions.BadRequestException;
import com.proyecto.agendaty.exceptions.ResourceNotFoundException;
import com.proyecto.agendaty.mapper.AtencionMapper;
import com.proyecto.agendaty.repository.AuditoriaRepository;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.AtencionRepository;
import com.proyecto.agendaty.repository.PausaRepository;
import com.proyecto.agendaty.repository.TurnoRepository;
import com.proyecto.agendaty.service.AtencionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AtencionServiceImp implements AtencionService {

    private final AtencionRepository atencionRepository;
    private final TurnoRepository turnoRepository;
    private final AsesorRepository asesorRepository;
    private final PausaRepository pausaRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final AtencionMapper atencionMapper;
    private final Clock clock;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public AtencionResponse iniciarAtencion(Long turnoId, Integer asesorId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Turno " + turnoId + " no encontrado"));
        entityManager.lock(turno, LockModeType.PESSIMISTIC_WRITE);

        if (turno.getEstado() != EstadoTurno.LLAMADO && turno.getEstado() != EstadoTurno.EN_ESPERA) {
            throw new BadRequestException("El turno " + turno.getNumeroTurno() + " no se puede atender (estado: " + turno.getEstado() + ")");
        }

        // RN1: un turno no puede ser atendido por dos asesores al mismo tiempo
        if (turno.getAsesor() != null && !turno.getAsesor().getId().equals(asesorId)) {
            throw new BadRequestException("El turno " + turno.getNumeroTurno() + " ya está siendo atendido por el asesor " + turno.getAsesor().getNombre());
        }

        Asesor asesor = asesorRepository.findById(asesorId)
                .orElseThrow(() -> new ResourceNotFoundException("Asesor " + asesorId + " no encontrado"));

        // RN4: un asesor solo puede tener un turno activo
        boolean asesorTieneActivo = turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_ATENCION, asesor)
                || turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_PAUSA, asesor);
        if (asesorTieneActivo) {
            throw new BadRequestException("El asesor " + asesor.getNombre() + " ya tiene un turno en atención");
        }

        if (Boolean.FALSE.equals(asesor.getDisponible())) {
            throw new BadRequestException("El asesor " + asesor.getNombre() + " no está disponible");
        }

        LocalDateTime ahora = LocalDateTime.now(clock);

        Atencion atencion = new Atencion();
        atencion.setTurno(turno);
        atencion.setAsesor(asesor);
        atencion.setFechaInicio(ahora);
        atencion.setTiempoEsperaMinutos(null);
        atencion.setTiempoAtencionMinutos(null);
        atencion.setTotalPausasMinutos(0);

        Atencion guardada = atencionRepository.save(atencion);

        turno.setAsesor(asesor);
        turno.setEstado(EstadoTurno.EN_ATENCION);
        turno.setFechaLlamado(turno.getFechaLlamado() != null ? turno.getFechaLlamado() : ahora);
        turnoRepository.save(turno);

        auditar(asesorId, "INICIAR_ATENCION", "Atencion", guardada.getId(),
                "Atención iniciada para turno " + turno.getNumeroTurno());

        return atencionMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public AtencionResponse pausarAtencion(Long turnoId, Integer asesorId) {
        Atencion atencion = atencionRepository.findByTurnoId(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró atención para el turno " + turnoId));

        if (!atencion.getAsesor().getId().equals(asesorId)) {
            throw new BadRequestException("Solo el asesor que inició la atención puede pausarla");
        }

        if (atencion.getTurno().getEstado() != EstadoTurno.EN_ATENCION) {
            throw new BadRequestException("El turno " + atencion.getTurno().getNumeroTurno() + " no está en atención");
        }

        LocalDateTime ahora = LocalDateTime.now(clock);
        Pausa pausa = new Pausa();
        pausa.setAtencion(atencion);
        pausa.setFechaInicio(ahora);
        pausaRepository.save(pausa);

        Turno turno = atencion.getTurno();
        turno.setEstado(EstadoTurno.EN_PAUSA);
        turnoRepository.save(turno);

        auditar(asesorId, "PAUSAR_ATENCION", "Atencion", atencion.getId(),
                "Atención pausada para turno " + turno.getNumeroTurno());

        return atencionMapper.toResponse(atencionRepository.findById(atencion.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public AtencionResponse reanudarAtencion(Long turnoId, Integer asesorId) {
        Atencion atencion = atencionRepository.findByTurnoId(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró atención para el turno " + turnoId));

        if (!atencion.getAsesor().getId().equals(asesorId)) {
            throw new BadRequestException("Solo el asesor que inició la atención puede reanudarla");
        }

        if (atencion.getTurno().getEstado() != EstadoTurno.EN_PAUSA) {
            throw new BadRequestException("El turno " + atencion.getTurno().getNumeroTurno() + " no está en pausa");
        }

        List<Pausa> pausas = atencion.getPausas();
        Pausa ultima = pausas.stream()
                .filter(p -> p.getFechaFin() == null)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No hay una pausa activa para reanudar"));

        LocalDateTime ahora = LocalDateTime.now(clock);
        ultima.setFechaFin(ahora);
        ultima.setDuracionMinutos((int) ChronoUnit.MINUTES.between(ultima.getFechaInicio(), ahora));
        pausaRepository.save(ultima);

        Turno turno = atencion.getTurno();
        turno.setEstado(EstadoTurno.EN_ATENCION);
        turnoRepository.save(turno);

        auditar(asesorId, "REANUDAR_ATENCION", "Atencion", atencion.getId(),
                "Atención reanudada para turno " + turno.getNumeroTurno());

        return atencionMapper.toResponse(atencionRepository.findById(atencion.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public AtencionResponse finalizarAtencion(Long turnoId, Integer asesorId, AtencionRequest request) {
        // RN5: toda atención finalizada debe tener resultado registrado
        if (request == null || request.resultado() == null || request.resultado().isBlank()) {
            throw new BadRequestException("El resultado es obligatorio para finalizar la atención");
        }

        Atencion atencion = atencionRepository.findByTurnoId(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró atención para el turno " + turnoId));

        if (!atencion.getAsesor().getId().equals(asesorId)) {
            throw new BadRequestException("Solo el asesor que atendió el turno puede finalizarlo");
        }

        Turno turno = atencion.getTurno();
        if (turno.getEstado() != EstadoTurno.EN_ATENCION && turno.getEstado() != EstadoTurno.EN_PAUSA) {
            throw new BadRequestException("El turno " + turno.getNumeroTurno() + " no se puede finalizar (estado: " + turno.getEstado() + ")");
        }

        LocalDateTime ahora = LocalDateTime.now(clock);
        atencion.setFechaFin(ahora);
        atencion.setResultado(request.resultado());
        atencion.setObservaciones(request.observaciones());

        // RN6: calcular tiempos
        LocalDateTime inicio = atencion.getFechaInicio();
        Long esperaMinutos = null;
        if (turno.getFechaCreacion() != null && turno.getFechaLlamado() != null) {
            esperaMinutos = ChronoUnit.MINUTES.between(turno.getFechaCreacion(), turno.getFechaLlamado());
        }
        atencion.setTiempoEsperaMinutos(esperaMinutos != null ? esperaMinutos.intValue() : null);

        Long totalPausas = atencion.getPausas().stream()
                .filter(p -> p.getDuracionMinutos() != null)
                .mapToLong(p -> p.getDuracionMinutos())
                .sum();
        atencion.setTotalPausasMinutos(totalPausas.intValue());

        Long atencionMinutos = ChronoUnit.MINUTES.between(inicio, ahora) - totalPausas;
        atencion.setTiempoAtencionMinutos(atencionMinutos.intValue());

        Atencion finalizada = atencionRepository.save(atencion);

        turno.setEstado(EstadoTurno.FINALIZADO);
        turno.setFechaFinEspera(ahora);
        turnoRepository.save(turno);

        // Liberar asesor
        Asesor asesor = atencion.getAsesor();
        asesor.setDisponible(true);
        asesorRepository.save(asesor);

        auditar(asesorId, "FINALIZAR_ATENCION", "Atencion", finalizada.getId(),
                "Atención finalizada para turno " + turno.getNumeroTurno() + " con resultado: " + request.resultado());

        return atencionMapper.toResponse(finalizada);
    }

    @Override
    @Transactional(readOnly = true)
    public AtencionResponse obtenerAtencionPorTurno(Long turnoId) {
        Atencion atencion = atencionRepository.findByTurnoId(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró atención para el turno " + turnoId));
        return atencionMapper.toResponse(atencion);
    }

    /* ==================== Auditoría ==================== */

    private void auditar(Integer usuarioId, String accion, String entidad, Long entidadId, String detalles) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuarioId(usuarioId);
        auditoria.setUsuarioNombre(usuarioId != null ? "usuario_" + usuarioId : "sistema");
        auditoria.setAccion(accion);
        auditoria.setEntidad(entidad);
        auditoria.setEntidadId(entidadId);
        auditoria.setFecha(LocalDateTime.now(clock));
        auditoria.setDetalles(detalles);
        auditoriaRepository.save(auditoria);
    }
}

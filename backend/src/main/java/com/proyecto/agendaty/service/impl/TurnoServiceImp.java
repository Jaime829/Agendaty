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
import com.proyecto.agendaty.model.enums.RolUsuario;
import com.proyecto.agendaty.repository.AuditoriaRepository;
import com.proyecto.agendaty.repository.AsesorRepository;
import com.proyecto.agendaty.repository.CiudadanoRepository;
import com.proyecto.agendaty.repository.TipoTramiteRepository;
import com.proyecto.agendaty.repository.TurnoRepository;
import com.proyecto.agendaty.service.AuthService;
import com.proyecto.agendaty.service.TurnoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TurnoServiceImp implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final CiudadanoRepository ciudadanoRepository;
    private final TipoTramiteRepository tipoTramiteRepository;
    private final AsesorRepository asesorRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final TurnoMapper turnoMapper;
    private final Clock clock;
    private final EntityManager entityManager;
    private final AuthService authService;

    @Override
    @Transactional
    public TurnoResponse generarTurno(TurnoRequest request, Integer usuarioId) {
        authService.validarRolCualquiera(usuarioId, new RolUsuario[]{RolUsuario.ROLE_RECEPCIONISTA, RolUsuario.ROLE_ADMINISTRADOR});
        Ciudadano ciudadano = ciudadanoRepository.findById(request.ciudadanoId())
                .orElseThrow(() -> new ResourceNotFoundException("Ciudadano " + request.ciudadanoId() + " no encontrado"));

        TipoTramite tipoTramite = tipoTramiteRepository.findById(request.tipoTramiteId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de trámite " + request.tipoTramiteId() + " no encontrado"));

        if (Boolean.FALSE.equals(tipoTramite.getActivo())) {
            throw new BadRequestException("El tipo de trámite " + tipoTramite.getCodigo() + " no está activo");
        }

        PrioridadTurno prioridad = calcularPrioridad(request, tipoTramite);

        String numeroTurno = generarNumeroTurnoUnico(tipoTramite, prioridad);

        LocalDateTime ahora = LocalDateTime.now(clock);
        Turno turno = new Turno();
        turno.setNumeroTurno(numeroTurno);
        turno.setEstado(EstadoTurno.EN_ESPERA);
        turno.setPrioridad(prioridad);
        turno.setFechaCreacion(ahora);
        turno.setCiudadano(ciudadano);
        turno.setTipoTramite(tipoTramite);

        Turno guardado = turnoRepository.save(turno);

        auditar(usuarioId, "GENERAR_TURNO", "Turno", guardado.getId(),
                "Turno " + numeroTurno + " generado para ciudadano " + ciudadano.getNombre());

        return turnoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public TurnoResponse llamarSiguiente(Integer asesorId) {
        authService.validarRolCualquiera(asesorId, new RolUsuario[]{RolUsuario.ROLE_ASESOR, RolUsuario.ROLE_ADMINISTRADOR});
        Asesor asesor = asesorRepository.findById(asesorId)
                .orElseThrow(() -> new ResourceNotFoundException("Asesor " + asesorId + " no encontrado"));

        if (Boolean.FALSE.equals(asesor.getDisponible())) {
            throw new BadRequestException("El asesor " + asesor.getNombre() + " no está disponible");
        }

        // RN4: el asesor no puede tener un turno activo (EN_ATENCION o EN_PAUSA)
        boolean tieneActivo = turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_ATENCION, asesor)
                || turnoRepository.existsByEstadoAndAsesor(EstadoTurno.EN_PAUSA, asesor);
        if (tieneActivo) {
            throw new BadRequestException("El asesor " + asesor.getNombre() + " ya tiene un turno en atención");
        }

        // RN3: prioritarios primero, luego orden de llegada (fechaCreacion ASC)
        List<Turno> prioritarios = turnoRepository.findByEstadoAndPrioridadOrderByCreacion(EstadoTurno.EN_ESPERA, PrioridadTurno.PRIORITARIO);
        List<Turno> normales = turnoRepository.findByEstadoAndPrioridadOrderByCreacion(EstadoTurno.EN_ESPERA, PrioridadTurno.NORMAL);

        final Turno siguiente;
        if (!prioritarios.isEmpty()) {
            siguiente = prioritarios.get(0);
        } else if (!normales.isEmpty()) {
            siguiente = normales.get(0);
        } else {
            throw new BadRequestException("No hay turnos en espera para llamar");
        }

        // Bloqueo pesimista para evitar doble llamado (RN1)
        Turno turnoBloqueado = turnoRepository.findById(siguiente.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Turno " + siguiente.getNumeroTurno() + " no encontrado"));
        entityManager.lock(turnoBloqueado, LockModeType.PESSIMISTIC_WRITE);

        // Re-verificar estado tras bloqueo
        if (turnoBloqueado.getEstado() != EstadoTurno.EN_ESPERA) {
            throw new BadRequestException("El turno " + turnoBloqueado.getNumeroTurno() + " ya no está en espera");
        }

        LocalDateTime ahora = LocalDateTime.now(clock);
        turnoBloqueado.setEstado(EstadoTurno.LLAMADO);
        turnoBloqueado.setFechaLlamado(ahora);
        turnoBloqueado.setAsesor(asesor);

        Turno llamado = turnoRepository.save(turnoBloqueado);

        auditar(asesorId, "LLAMAR_TURNO", "Turno", llamado.getId(),
                "Turno " + llamado.getNumeroTurno() + " llamado por asesor " + asesor.getNombre());

        return turnoMapper.toResponse(llamado);
    }

    @Override
    @Transactional
    public TurnoResponse cancelarTurno(Long turnoId, Integer usuarioId) {
        authService.validarRolCualquiera(usuarioId, new RolUsuario[]{RolUsuario.ROLE_RECEPCIONISTA, RolUsuario.ROLE_ADMINISTRADOR});
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Turno " + turnoId + " no encontrado"));

        if (turno.getEstado() == EstadoTurno.FINALIZADO || turno.getEstado() == EstadoTurno.NO_ATENDIDO || turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new BadRequestException("El turno " + turno.getNumeroTurno() + " ya se encuentra en estado " + turno.getEstado());
        }

        turno.setEstado(EstadoTurno.CANCELADO);
        Turno cancelado = turnoRepository.save(turno);

        auditar(usuarioId, "CANCELAR_TURNO", "Turno", turnoId,
                "Turno " + cancelado.getNumeroTurno() + " cancelado");

        return turnoMapper.toResponse(cancelado);
    }

    @Override
    @Transactional
    public TurnoResponse marcarNoAtendido(Long turnoId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Turno " + turnoId + " no encontrado"));

        if (turno.getEstado() != EstadoTurno.LLAMADO) {
            throw new BadRequestException("Solo se puede marcar como no atendido un turno en estado LLAMADO");
        }

        // RN2: turno llamado sin respuesta pasa a NO_ATENDIDO
        turno.setEstado(EstadoTurno.NO_ATENDIDO);
        Turno noAtendido = turnoRepository.save(turno);

        auditar(null, "MARCAR_NO_ATENDIDO", "Turno", turnoId,
                "Turno " + noAtendido.getNumeroTurno() + " marcado como no atendido (RN2)");

        return turnoMapper.toResponse(noAtendido);
    }

    @Override
    @Transactional(readOnly = true)
    public TurnoResponse obtenerTurno(Long turnoId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Turno " + turnoId + " no encontrado"));
        return turnoMapper.toResponse(turno);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponse> listarTurnos(String estado, String prioridad, Integer ciudadanoId, Long tipoTramiteId) {
        List<Turno> turnos = turnoRepository.findAll();
        return turnos.stream()
                .filter(t -> estado == null || t.getEstado().name().equalsIgnoreCase(estado))
                .filter(t -> prioridad == null || t.getPrioridad().name().equalsIgnoreCase(prioridad))
                .filter(t -> ciudadanoId == null || t.getCiudadano().getId().equals(ciudadanoId))
                .filter(t -> tipoTramiteId == null || t.getTipoTramite().getId().equals(tipoTramiteId))
                .map(turnoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /* ==================== Métodos privados ==================== */

    private PrioridadTurno calcularPrioridad(TurnoRequest request, TipoTramite tipoTramite) {
        if (Boolean.TRUE.equals(request.prioritario())) {
            return PrioridadTurno.PRIORITARIO;
        }
        if (Boolean.TRUE.equals(tipoTramite.getEsPrioritarioPorDefecto())) {
            return PrioridadTurno.PRIORITARIO;
        }
        return PrioridadTurno.NORMAL;
    }

    private String generarNumeroTurnoUnico(TipoTramite tipoTramite, PrioridadTurno prioridad) {
        LocalDateTime hoy = LocalDateTime.now(clock);
        LocalDateTime inicioDia = hoy.toLocalDate().atStartOfDay();
        LocalDateTime finDia = hoy.toLocalDate().plusDays(1).atStartOfDay();

        List<Turno> turnosDelDia = turnoRepository.findAll().stream()
                .filter(t -> t.getTipoTramite().getId().equals(tipoTramite.getId()))
                .filter(t -> t.getFechaCreacion() != null
                        && !t.getFechaCreacion().isBefore(inicioDia)
                        && t.getFechaCreacion().isBefore(finDia))
                .collect(Collectors.toList());

        int maximoSecuencia = 0;
        for (Turno t : turnosDelDia) {
            String numero = t.getNumeroTurno();
            if (numero != null && numero.contains("-")) {
                try {
                    int seq = Integer.parseInt(numero.substring(numero.lastIndexOf("-") + 1));
                    if (seq > maximoSecuencia) {
                        maximoSecuencia = seq;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        int siguiente = maximoSecuencia + 1;
        String sufijo = String.format("%05d", siguiente);
        String letraPrioridad = prioridad == PrioridadTurno.PRIORITARIO ? "P" : "N";
        return tipoTramite.getCodigo() + letraPrioridad + "-" + sufijo;
    }

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

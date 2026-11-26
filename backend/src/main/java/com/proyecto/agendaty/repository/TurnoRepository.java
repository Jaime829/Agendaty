package com.proyecto.agendaty.repository;

import com.proyecto.agendaty.entity.Turno;
import com.proyecto.agendaty.model.enums.EstadoTurno;
import com.proyecto.agendaty.model.enums.PrioridadTurno;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    Optional<Turno> findByNumeroTurno(String numeroTurno);

    List<Turno> findByEstado(EstadoTurno estado, Pageable pageable);

    List<Turno> findByEstadoAndPrioridad(EstadoTurno estado, PrioridadTurno prioridad, Pageable pageable);

    @Query("SELECT t FROM Turno t WHERE t.estado = :estado AND t.prioridad = :prioridad ORDER BY t.fechaCreacion ASC")
    List<Turno> findByEstadoAndPrioridadOrderByCreacion(
            @Param("estado") EstadoTurno estado,
            @Param("prioridad") PrioridadTurno prioridad);

    boolean existsByEstadoAndAsesor(EstadoTurno estado, com.proyecto.agendaty.entity.Asesor asesor);

    List<Turno> findByCiudadano(com.proyecto.agendaty.entity.Ciudadano ciudadano, Pageable pageable);

    long countByEstado(EstadoTurno estado);
}

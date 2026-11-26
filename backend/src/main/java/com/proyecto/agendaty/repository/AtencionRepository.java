package com.proyecto.agendaty.repository;

import com.proyecto.agendaty.entity.Atencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtencionRepository extends JpaRepository<Atencion, Long> {
    List<Atencion> findByAsesor(com.proyecto.agendaty.entity.Asesor asesor);

    Optional<Atencion> findByTurnoId(Long turnoId);
}

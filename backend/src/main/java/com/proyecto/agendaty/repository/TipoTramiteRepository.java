package com.proyecto.agendaty.repository;

import com.proyecto.agendaty.entity.TipoTramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoTramiteRepository extends JpaRepository<TipoTramite, Long> {
    boolean existsByCodigo(String codigo);
}

package com.proyecto.agendaty.repository;

import com.proyecto.agendaty.entity.Pausa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PausaRepository extends JpaRepository<Pausa, Long> {
}

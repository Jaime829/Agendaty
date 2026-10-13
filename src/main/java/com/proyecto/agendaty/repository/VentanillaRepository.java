package com.proyecto.agendaty.repository;

import com.proyecto.agendaty.entity.Ventanilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentanillaRepository extends JpaRepository<Ventanilla, Integer> {
    Boolean existsByNumero(Integer numero);

}

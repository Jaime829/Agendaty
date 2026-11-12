package com.proyecto.agendaty.repository;

import com.proyecto.agendaty.entity.Asesor;
import com.proyecto.agendaty.entity.Ventanilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AsesorRepository extends JpaRepository<Asesor, Integer> {
    boolean existsByVentanillaActual(Ventanilla ventanilla);
    Optional<Asesor> findByUsername(String username);
    Optional<Asesor> findByCorreo(String correo);
}

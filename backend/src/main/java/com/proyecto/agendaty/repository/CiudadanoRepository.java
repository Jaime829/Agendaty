package com.proyecto.agendaty.repository;

import com.proyecto.agendaty.entity.Ciudadano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CiudadanoRepository extends JpaRepository<Ciudadano, Integer> {
    Boolean existsByDocumento(String documento);
}

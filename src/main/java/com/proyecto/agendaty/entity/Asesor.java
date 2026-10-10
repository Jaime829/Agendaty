package com.proyecto.agendaty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "asesores")
public class Asesor extends Usuario {
    @Column(name = "disponible")
    private Boolean disponible;

    @OneToOne
    @JoinColumn(name = "ventanilla_id", unique = true)
    private Ventanilla ventanillaActual;

}

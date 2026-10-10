package com.proyecto.agendaty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ventanillas")
public class Ventanilla {
    @Id
    private Integer numero;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @OneToOne(mappedBy = "ventanillaActual")
    private Asesor asesorActual;
}

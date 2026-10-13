package com.proyecto.agendaty.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.lang.foreign.StructLayout;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ciudadanos")
public class Ciudadano extends Usuario {

    @Column(name = "documento", nullable = false, length = 10, unique = true)
    private String documento;

    @Column(name = "telefono", nullable = false, length = 10)
    private String telefono;

}

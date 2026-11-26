package com.proyecto.agendaty.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipos_tramite")
public class TipoTramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "codigo", nullable = false, length = 10, unique = true)
    private String codigo;

    @Column(name = "es_prioritario_por_defecto")
    private Boolean esPrioritarioPorDefecto = false;

    @Column(name = "activo")
    private Boolean activo = true;

    @OneToMany(mappedBy = "tipoTramite")
    @ToString.Exclude
    private List<Turno> turnos;
}

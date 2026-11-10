package com.proyecto.agendaty.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "atenciones")
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    @ToString.Exclude
    private Turno turno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asesor_id", nullable = false)
    @ToString.Exclude
    private Asesor asesor;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "resultado", length = 1000)
    private String resultado;

    @Column(name = "tiempo_espera_minutos")
    private Integer tiempoEsperaMinutos;

    @Column(name = "tiempo_atencion_minutos")
    private Integer tiempoAtencionMinutos;

    @Column(name = "total_pausas_minutos")
    private Integer totalPausasMinutos = 0;

    @OneToMany(mappedBy = "atencion", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Pausa> pausas;
}

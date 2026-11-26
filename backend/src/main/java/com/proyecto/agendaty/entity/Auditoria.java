package com.proyecto.agendaty.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "usuario_nombre", length = 150)
    private String usuarioNombre;

    @Column(name = "accion", length = 100)
    private String accion;

    @Column(name = "entidad", length = 100)
    private String entidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "direccion_ip", length = 50)
    private String direccionIp;

    @Column(name = "detalles", length = 1000)
    private String detalles;
}

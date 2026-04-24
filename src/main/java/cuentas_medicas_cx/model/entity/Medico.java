package cuentas_medicas_cx.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "medicos")
@Getter
@Setter
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOMBRE_COMPLETO", nullable = false, length = 255)
    private String nombreCompleto;

    @Column(name = "REGISTRO_MEDICO", unique = true, length = 50)
    private String registroMedico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ESPECIALIDAD_ID")
    private Especialidad especialidad;

    @Column(name = "ESTADO")
    private Boolean estado = true;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}
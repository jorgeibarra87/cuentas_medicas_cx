package cuentas_medicas_cx.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingresos")
@Getter
@Setter
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NUMERO_INGRESO", nullable = false, unique = true, length = 20)
    private String numeroIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PACIENTE_ID")
    private Paciente paciente;

    @Column(name = "ESTADO")
    private Boolean estado = true;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}
package cuentas_medicas_cx.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "entidades_salud")
@Getter
@Setter
public class EntidadesSalud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOMBRE", nullable = false, unique = true, length = 255)
    private String nombre;

    @Column(name = "TIPO", length = 50)
    private String tipo;

    @Column(name = "ESTADO")
    private Boolean estado = true;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}
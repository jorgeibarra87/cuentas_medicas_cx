package cuentas_medicas_cx.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cups_procedimientos")
@Getter
@Setter
public class CupsProcedimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "DESCRIPCION", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "GRUPO_CUPS", length = 10)
    private String grupoCups;

    @Column(name = "COBERTURA_POS", precision = 5, scale = 2)
    private BigDecimal coberturaPos;

    @Column(name = "REQUIERE_AUTORIZACION")
    private Boolean requiereAutorizacion = false;

    @Column(name = "ESTADO")
    private Boolean estado = true;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}
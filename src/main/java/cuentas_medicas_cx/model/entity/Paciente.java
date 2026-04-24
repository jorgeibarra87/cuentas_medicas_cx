package cuentas_medicas_cx.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NUMERO_IDENTIFICACION", nullable = false, unique = true, length = 20)
    private String numeroIdentificacion;

    @Column(name = "TIPO_IDENTIFICACION", length = 5)
    private String tipoIdentificacion = "CC";

    @Column(name = "NOMBRE", length = 255)
    private String nombre;

    @Column(name = "FECHA_NACIMIENTO")
    private LocalDate fechaNacimiento;

    @Column(name = "ESTADO")
    private Boolean estado = true;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}
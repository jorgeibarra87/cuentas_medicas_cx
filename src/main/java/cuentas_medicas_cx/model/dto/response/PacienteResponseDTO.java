package cuentas_medicas_cx.model.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteResponseDTO {
    private Long id;
    private String numeroIdentificacion;
    private String tipoIdentificacion;
    private String nombre;
    private LocalDate fechaNacimiento;
    private Boolean estado;
}
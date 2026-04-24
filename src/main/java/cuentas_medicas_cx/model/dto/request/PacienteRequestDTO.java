package cuentas_medicas_cx.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteRequestDTO {
    @NotBlank(message = "El numero de identificacion es obligatorio")
    private String numeroIdentificacion;
    private String tipoIdentificacion = "CC";
    private String nombre;
    private LocalDate fechaNacimiento;
    private Boolean estado = true;
}
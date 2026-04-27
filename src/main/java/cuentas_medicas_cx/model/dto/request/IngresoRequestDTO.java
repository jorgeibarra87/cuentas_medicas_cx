package cuentas_medicas_cx.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IngresoRequestDTO {

    @NotBlank(message = "El número de ingreso es obligatorio")
    private String numeroIngreso;

    private Long pacienteId;
}
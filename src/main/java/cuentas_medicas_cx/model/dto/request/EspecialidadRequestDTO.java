package cuentas_medicas_cx.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EspecialidadRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private Boolean estado = true;
}
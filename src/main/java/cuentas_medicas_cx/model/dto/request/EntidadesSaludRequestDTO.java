package cuentas_medicas_cx.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EntidadesSaludRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String tipo;
    private Boolean estado = true;
}
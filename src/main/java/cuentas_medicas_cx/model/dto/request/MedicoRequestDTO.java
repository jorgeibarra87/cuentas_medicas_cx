package cuentas_medicas_cx.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MedicoRequestDTO {
    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;
    private String registroMedico;
    private Long especialidadId;
    private Boolean estado = true;
}
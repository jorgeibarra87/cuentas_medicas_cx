package cuentas_medicas_cx.model.dto.response;

import lombok.Data;

@Data
public class MedicoResponseDTO {
    private Long id;
    private String nombreCompleto;
    private String registroMedico;
    private Long especialidadId;
    private String especialidadNombre;
    private Boolean estado;
}
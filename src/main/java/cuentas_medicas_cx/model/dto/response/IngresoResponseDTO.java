package cuentas_medicas_cx.model.dto.response;

import lombok.Data;

@Data
public class IngresoResponseDTO {
    private Long id;
    private String numeroIngreso;
    private Long pacienteId;
    private String pacienteNumeroIdentificacion;
    private Boolean estado;
}
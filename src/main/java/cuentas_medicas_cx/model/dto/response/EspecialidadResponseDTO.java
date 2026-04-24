package cuentas_medicas_cx.model.dto.response;

import lombok.Data;

@Data
public class EspecialidadResponseDTO {
    private Long id;
    private String nombre;
    private Boolean estado;
}
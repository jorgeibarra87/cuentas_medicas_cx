package cuentas_medicas_cx.model.dto.response;

import lombok.Data;

@Data
public class EntidadesSaludResponseDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private Boolean estado;
}
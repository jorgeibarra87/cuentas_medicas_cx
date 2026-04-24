package cuentas_medicas_cx.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CupsProcedimientoResponseDTO {
    private Long id;
    private String codigo;
    private String descripcion;
    private String grupoCups;
    private BigDecimal coberturaPos;
    private Boolean requiereAutorizacion;
    private Boolean estado;
}
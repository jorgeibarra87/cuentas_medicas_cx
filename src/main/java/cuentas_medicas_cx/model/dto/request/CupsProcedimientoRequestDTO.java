package cuentas_medicas_cx.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CupsProcedimientoRequestDTO {
    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;
    private String descripcion;
    private String grupoCups;
    private BigDecimal coberturaPos;
    private Boolean requiereAutorizacion = false;
    private Boolean estado = true;
}
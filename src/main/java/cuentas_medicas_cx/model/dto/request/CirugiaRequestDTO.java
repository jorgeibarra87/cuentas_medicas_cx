package cuentas_medicas_cx.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CirugiaRequestDTO {

    @NotBlank(message = "El tipo de procedimiento es obligatorio")
    private String tipoProcedimiento;

    @NotBlank(message = "El paciente es obligatorio")
    private Long pacienteId;

    private Long ingresoId;
    private Long cupsId;
    private String procedCod;
    private String gqx;
    private String intervencion;
    private Long especialidadId;
    private Long medicoId;
    private Long anestesiologoId;
    private Long entidadSaludId;
    private String ayudante1;
    private String ayudante2;
    private String liquidacion;
    private String auditoriaPorcentaje;
    private String novedadDesc;
    private String autorizacion;
    private String imagenesDx;
    private String causaObjecion;
    private String revSupervision;
    private String observacionAuditoria;
    private String estadoAuditoria;
}
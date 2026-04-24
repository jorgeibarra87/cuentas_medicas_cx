package cuentas_medicas_cx.model.dto.response;

import lombok.Data;

@Data
public class CirugiaResponseDTO {
    private Long id;
    private String tipoProcedimiento;
    private Long pacienteId;
    private String pacienteNumeroIdentificacion;
    private String ingreso;
    private Long cupsId;
    private String cupsCodigo;
    private String procedCod;
    private String gqx;
    private String intervencion;
    private Long especialidadId;
    private String especialidadNombre;
    private Long medicoId;
    private String medicoNombre;
    private Long anestesiologoId;
    private String anestesiologoNombre;
    private Long entidadSaludId;
    private String entidadSaludNombre;
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
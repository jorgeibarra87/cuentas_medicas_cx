package cuentas_medicas_cx.model.dto.request;

import lombok.Data;

@Data
public class CirugiaUpdateRequestDTO {
    private String tipoProcedimiento;
    private String pacienteNumeroIdentificacion;
    private String ingresoNumero;
    private String cupsCodigo;
    private String procedCod;
    private String gqx;
    private String intervencion;
    private String especialidadNombre;
    private String medicoNombre;
    private String anestesiologoNombre;
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
    private String entidadSaludNombre;
    private String regimen;
    private String fechaSolicitud;
    private String fechaCargue;
    private String horaCargue;
    private String fechaResultado;
}

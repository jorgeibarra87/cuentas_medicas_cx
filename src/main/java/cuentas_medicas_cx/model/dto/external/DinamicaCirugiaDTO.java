package cuentas_medicas_cx.model.dto.external;

import lombok.Data;

@Data
public class DinamicaCirugiaDTO {
    private String tipo;
    private String paciente;
    private String nombres;
    private String ingreso;
    private String cups;
    private String procedCod;
    private String grupoqxCod;
    private String intervencion;
    private String especialidad;
    private String medico;
    private String fechaSolicitud;
    private String fechaCargue;
    private String horaCargue;
    private String fechaResultado;
    private String regimen;
    private String entidad;
}
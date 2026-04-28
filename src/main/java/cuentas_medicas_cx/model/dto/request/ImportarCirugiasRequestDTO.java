package cuentas_medicas_cx.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ImportarCirugiasRequestDTO {

    @NotBlank(message = "La fecha inicio es obligatoria")
    private String fechaInicio;

    @NotBlank(message = "La fecha fin es obligatoria")
    private String fechaFin;

    private List<DatosDinamicaDTO> datos;

    @Data
    public static class DatosDinamicaDTO {
        private String tipo;
        private String paciente;
        private String nombres;
        private String ingreso;
        private String cups;
        private String procedCod;
        private String gqx;
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
}
package cuentas_medicas_cx.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ImportarCirugiasResponseDTO {
    private String rangoFechas;
    private int totalRegistros;
    private int exitosos;
    private int errores;
    private List<String> mensajes;
}
package cuentas_medicas_cx.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCirugiasResponseDTO {
    private List<Map<String, Object>> totalPorMes;
    private List<Map<String, Object>> estadoPorMes;
    private List<Map<String, Object>> porEspecialidad;
    private List<Map<String, Object>> porProcedimiento;
    private List<Map<String, Object>> porAuditor;
    private long totalGeneral;
}

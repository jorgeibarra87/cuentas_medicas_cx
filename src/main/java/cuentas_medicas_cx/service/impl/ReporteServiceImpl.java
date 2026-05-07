package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.response.ReporteCirugiasResponseDTO;
import cuentas_medicas_cx.repository.CirugiaRepository;
import cuentas_medicas_cx.service.ReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final CirugiaRepository cirugiaRepository;

    @Override
    @Transactional(readOnly = true)
    public ReporteCirugiasResponseDTO obtenerReporteAnual(String anio) {
        log.info("📊 Generando reporte anual para: {}", anio);

        String anioPattern = "%" + anio;

        List<Map<String, Object>> totalPorMes = cirugiaRepository.contarPorMes(anioPattern);
        List<Map<String, Object>> estadoPorMes = cirugiaRepository.contarPorMesYEstado(anioPattern,
                Arrays.asList("No facturable", "Adición", "Hecho", "Nulo", "Ok", "Facturable", "Revisión", "Pendiente", "Hecho pendiente", "Adición pendiente"));
        List<Map<String, Object>> porEspecialidad = cirugiaRepository.contarPorEspecialidad(anioPattern);
        List<Map<String, Object>> porProcedimiento = cirugiaRepository.contarPorProcedimiento(anioPattern);
        List<Map<String, Object>> porAuditor = cirugiaRepository.contarPorAuditor(anioPattern);

        long totalGeneral = cirugiaRepository.count();

        ReporteCirugiasResponseDTO response = new ReporteCirugiasResponseDTO();
        response.setTotalPorMes(totalPorMes);
        response.setEstadoPorMes(estadoPorMes);
        response.setPorEspecialidad(porEspecialidad);
        response.setPorProcedimiento(porProcedimiento);
        response.setPorAuditor(porAuditor);
        response.setTotalGeneral(totalGeneral);

        log.info("✅ Reporte generado - Total general: {}", totalGeneral);
        return response;
    }
}

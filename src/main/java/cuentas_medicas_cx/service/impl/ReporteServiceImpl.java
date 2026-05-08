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

        String anioPattern = anio + "-%";

        long totalGeneral;
        List<Map<String, Object>> totalPorMes;
        List<Map<String, Object>> estadoPorMes;
        List<Map<String, Object>> porEspecialidad;
        List<Map<String, Object>> porProcedimiento;
        List<Map<String, Object>> porAuditor;

        try {
            totalPorMes = cirugiaRepository.contarPorMes(anioPattern);
        } catch (Exception e) {
            log.error("Error en contarPorMes: {}", e.getMessage());
            totalPorMes = List.of();
        }

        try {
            estadoPorMes = cirugiaRepository.contarPorMesYEstado(anioPattern,
                    Arrays.asList("No facturable", "Adición", "Hecho", "Nulo", "Ok", "Facturable", "Revisión", "Pendiente", "Hecho pendiente", "Adición pendiente"));
        } catch (Exception e) {
            log.error("Error en contarPorMesYEstado: {}", e.getMessage());
            estadoPorMes = List.of();
        }

        try {
            porEspecialidad = cirugiaRepository.contarPorEspecialidad(anioPattern);
        } catch (Exception e) {
            log.error("Error en contarPorEspecialidad: {}", e.getMessage());
            porEspecialidad = List.of();
        }

        try {
            porProcedimiento = cirugiaRepository.contarPorProcedimiento(anioPattern);
        } catch (Exception e) {
            log.error("Error en contarPorProcedimiento: {}", e.getMessage());
            porProcedimiento = List.of();
        }

        try {
            porAuditor = cirugiaRepository.contarPorAuditor(anioPattern);
        } catch (Exception e) {
            log.error("Error en contarPorAuditor: {}", e.getMessage());
            porAuditor = List.of();
        }

        totalGeneral = cirugiaRepository.contarTotalPorAnio(anioPattern);

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

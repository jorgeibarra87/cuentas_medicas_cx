package cuentas_medicas_cx.controller;

import cuentas_medicas_cx.model.dto.response.ReporteCirugiasResponseDTO;
import cuentas_medicas_cx.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cirugias/reporte")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Endpoints para estadísticas y reportes de cirugías")
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Obtener reporte anual",
            description = "Retorna estadísticas de cirugías por mes, estado, especialidad, procedimiento y auditor para un año dado.",
            tags = {"Reportes"})
    @GetMapping
    public ResponseEntity<ReporteCirugiasResponseDTO> obtenerReporte(
            @RequestParam(defaultValue = "2026") String anio) {
        log.info("📥 GET /cirugias/reporte - anio: {}", anio);
        try {
            ReporteCirugiasResponseDTO response = reporteService.obtenerReporteAnual(anio);
            log.info("✅ Reporte generado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error al generar reporte: {}", e.getMessage(), e);
            throw e;
        }
    }
}

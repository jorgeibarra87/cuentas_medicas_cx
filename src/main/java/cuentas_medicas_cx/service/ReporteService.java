package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.response.ReporteCirugiasResponseDTO;

public interface ReporteService {
    ReporteCirugiasResponseDTO obtenerReporteAnual(String anio);
}

package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.external.DinamicaCirugiaDTO;

import java.util.List;

public interface DinamicaService {

    List<DinamicaCirugiaDTO> obtenerCirugiasPorFechas(String fechaInicio, String fechaFin);

    List<DinamicaCirugiaDTO> obtenerCirugiasPorFechas(String fechaInicio);
}
package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.request.IngresoRequestDTO;
import cuentas_medicas_cx.model.dto.response.IngresoResponseDTO;

import java.util.List;

public interface IngresoService {

    IngresoResponseDTO crear(IngresoRequestDTO request);

    IngresoResponseDTO obtenerPorId(Long id);

    List<IngresoResponseDTO> listarTodos();

    List<IngresoResponseDTO> listarPorPaciente(Long pacienteId);

    IngresoResponseDTO actualizar(Long id, IngresoRequestDTO request);

    void eliminar(Long id);
}
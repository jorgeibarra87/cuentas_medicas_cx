package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.request.PacienteRequestDTO;
import cuentas_medicas_cx.model.dto.response.PacienteResponseDTO;

import java.util.List;

public interface PacienteService {
    PacienteResponseDTO crear(PacienteRequestDTO request);
    PacienteResponseDTO obtenerPorId(Long id);
    List<PacienteResponseDTO> listarTodos();
    PacienteResponseDTO actualizar(Long id, PacienteRequestDTO request);
    PacienteResponseDTO cambiarEstado(Long id, Boolean estado);
    void eliminar(Long id);
    PacienteResponseDTO buscarPorIdentificacion(String numeroIdentificacion);
}
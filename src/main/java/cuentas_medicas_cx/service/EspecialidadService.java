package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.request.EspecialidadRequestDTO;
import cuentas_medicas_cx.model.dto.response.EspecialidadResponseDTO;

import java.util.List;

public interface EspecialidadService {
    EspecialidadResponseDTO crear(EspecialidadRequestDTO request);
    EspecialidadResponseDTO obtenerPorId(Long id);
    List<EspecialidadResponseDTO> listarTodos();
    EspecialidadResponseDTO actualizar(Long id, EspecialidadRequestDTO request);
    EspecialidadResponseDTO cambiarEstado(Long id, Boolean estado);
    void eliminar(Long id);
}
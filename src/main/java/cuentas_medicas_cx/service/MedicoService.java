package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.request.MedicoRequestDTO;
import cuentas_medicas_cx.model.dto.response.MedicoResponseDTO;

import java.util.List;

public interface MedicoService {
    MedicoResponseDTO crear(MedicoRequestDTO request);
    MedicoResponseDTO obtenerPorId(Long id);
    List<MedicoResponseDTO> listarTodos();
    MedicoResponseDTO actualizar(Long id, MedicoRequestDTO request);
    MedicoResponseDTO cambiarEstado(Long id, Boolean estado);
    void eliminar(Long id);
    List<MedicoResponseDTO> listarPorEspecialidad(Long especialidadId);
}
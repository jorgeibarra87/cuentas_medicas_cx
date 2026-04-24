package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.request.EntidadesSaludRequestDTO;
import cuentas_medicas_cx.model.dto.response.EntidadesSaludResponseDTO;

import java.util.List;

public interface EntidadesSaludService {
    EntidadesSaludResponseDTO crear(EntidadesSaludRequestDTO request);
    EntidadesSaludResponseDTO obtenerPorId(Long id);
    List<EntidadesSaludResponseDTO> listarTodos();
    EntidadesSaludResponseDTO actualizar(Long id, EntidadesSaludRequestDTO request);
    EntidadesSaludResponseDTO cambiarEstado(Long id, Boolean estado);
    void eliminar(Long id);
}
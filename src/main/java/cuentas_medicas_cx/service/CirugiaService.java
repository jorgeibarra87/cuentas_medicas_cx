package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.request.CirugiaRequestDTO;
import cuentas_medicas_cx.model.dto.response.CirugiaResponseDTO;

import java.util.List;

public interface CirugiaService {

    CirugiaResponseDTO crear(CirugiaRequestDTO request);

    CirugiaResponseDTO obtenerPorId(Long id);

    List<CirugiaResponseDTO> listarTodos();

    CirugiaResponseDTO actualizar(Long id, CirugiaRequestDTO request);

    CirugiaResponseDTO cambiarEstado(Long id, String estado);

    void eliminar(Long id);
}
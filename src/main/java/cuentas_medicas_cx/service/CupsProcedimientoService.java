package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.request.CupsProcedimientoRequestDTO;
import cuentas_medicas_cx.model.dto.response.CupsProcedimientoResponseDTO;

import java.util.List;

public interface CupsProcedimientoService {
    CupsProcedimientoResponseDTO crear(CupsProcedimientoRequestDTO request);
    CupsProcedimientoResponseDTO obtenerPorId(Long id);
    List<CupsProcedimientoResponseDTO> listarTodos();
    CupsProcedimientoResponseDTO actualizar(Long id, CupsProcedimientoRequestDTO request);
    CupsProcedimientoResponseDTO cambiarEstado(Long id, Boolean estado);
    void eliminar(Long id);
    CupsProcedimientoResponseDTO buscarPorCodigo(String codigo);
}
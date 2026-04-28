package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.request.CirugiaRequestDTO;
import cuentas_medicas_cx.model.dto.request.ImportarCirugiasRequestDTO;
import cuentas_medicas_cx.model.dto.response.CirugiaResponseDTO;
import cuentas_medicas_cx.model.dto.response.ImportarCirugiasResponseDTO;

import java.util.List;

public interface CirugiaService {

    ImportarCirugiasResponseDTO importarDesdeDinamica(ImportarCirugiasRequestDTO request);

    CirugiaResponseDTO crear(CirugiaRequestDTO request);

    CirugiaResponseDTO obtenerPorId(Long id);

    List<CirugiaResponseDTO> listarTodos();

    List<CirugiaResponseDTO> listarPorIngreso(Long ingresoId);

    CirugiaResponseDTO actualizar(Long id, CirugiaRequestDTO request);

    CirugiaResponseDTO cambiarEstado(Long id, String estado);

    void eliminar(Long id);
}
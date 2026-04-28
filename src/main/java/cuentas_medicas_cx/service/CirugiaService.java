package cuentas_medicas_cx.service;

import cuentas_medicas_cx.model.dto.request.CirugiaRequestDTO;
import cuentas_medicas_cx.model.dto.request.ImportarCirugiasRequestDTO;
import cuentas_medicas_cx.model.dto.response.CirugiaResponseDTO;
import cuentas_medicas_cx.model.dto.response.ImportarCirugiasResponseDTO;
import cuentas_medicas_cx.model.dto.external.DinamicaCirugiaDTO;

import java.util.List;

public interface CirugiaService {

    ImportarCirugiasResponseDTO importarDesdeDinamica(ImportarCirugiasRequestDTO request);

    ImportarCirugiasResponseDTO importarDesdeDinamicaBD(String fechaInicio, String fechaFin);

    List<DinamicaCirugiaDTO> obtenerDeDinamica(String fechaInicio, String fechaFin);

    CirugiaResponseDTO crear(CirugiaRequestDTO request);

    CirugiaResponseDTO obtenerPorId(Long id);

    List<CirugiaResponseDTO> listarTodos();

    List<CirugiaResponseDTO> listarPorIngreso(Long ingresoId);

    CirugiaResponseDTO actualizar(Long id, CirugiaRequestDTO request);

    CirugiaResponseDTO cambiarEstado(Long id, String estado);

    void eliminar(Long id);
}
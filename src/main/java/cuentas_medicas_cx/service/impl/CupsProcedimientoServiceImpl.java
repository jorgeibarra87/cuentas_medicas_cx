package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.request.CupsProcedimientoRequestDTO;
import cuentas_medicas_cx.model.dto.response.CupsProcedimientoResponseDTO;
import cuentas_medicas_cx.model.entity.CupsProcedimiento;
import cuentas_medicas_cx.repository.CupsProcedimientoRepository;
import cuentas_medicas_cx.service.CupsProcedimientoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CupsProcedimientoServiceImpl implements CupsProcedimientoService {

    private final CupsProcedimientoRepository cupsProcedimientoRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CupsProcedimientoResponseDTO crear(CupsProcedimientoRequestDTO request) {
        CupsProcedimiento entity = new CupsProcedimiento();
        entity.setCodigo(request.getCodigo());
        entity.setDescripcion(request.getDescripcion());
        entity.setGrupoCups(request.getGrupoCups());
        entity.setCoberturaPos(request.getCoberturaPos());
        entity.setRequiereAutorizacion(request.getRequiereAutorizacion());
        if (request.getEstado() != null) {
            entity.setEstado(request.getEstado());
        }
        CupsProcedimiento guardado = cupsProcedimientoRepository.save(entity);
        return modelMapper.map(guardado, CupsProcedimientoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public CupsProcedimientoResponseDTO obtenerPorId(Long id) {
        CupsProcedimiento entity = cupsProcedimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Procedimiento CUPS no encontrado con id: " + id));
        return modelMapper.map(entity, CupsProcedimientoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CupsProcedimientoResponseDTO> listarTodos() {
        return cupsProcedimientoRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, CupsProcedimientoResponseDTO.class))
                .toList();
    }

    @Override
    public CupsProcedimientoResponseDTO actualizar(Long id, CupsProcedimientoRequestDTO request) {
        CupsProcedimiento entity = cupsProcedimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Procedimiento CUPS no encontrado con id: " + id));
        entity.setCodigo(request.getCodigo());
        entity.setDescripcion(request.getDescripcion());
        entity.setGrupoCups(request.getGrupoCups());
        entity.setCoberturaPos(request.getCoberturaPos());
        entity.setRequiereAutorizacion(request.getRequiereAutorizacion());
        CupsProcedimiento actualizado = cupsProcedimientoRepository.save(entity);
        return modelMapper.map(actualizado, CupsProcedimientoResponseDTO.class);
    }

    @Override
    public CupsProcedimientoResponseDTO cambiarEstado(Long id, Boolean estado) {
        CupsProcedimiento entity = cupsProcedimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Procedimiento CUPS no encontrado con id: " + id));
        entity.setEstado(estado);
        return modelMapper.map(cupsProcedimientoRepository.save(entity), CupsProcedimientoResponseDTO.class);
    }

    @Override
    public void eliminar(Long id) {
        if (!cupsProcedimientoRepository.existsById(id)) {
            throw new EntityNotFoundException("Procedimiento CUPS no encontrado con id: " + id);
        }
        cupsProcedimientoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CupsProcedimientoResponseDTO buscarPorCodigo(String codigo) {
        CupsProcedimiento entity = cupsProcedimientoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Procedimiento CUPS no encontrado con codigo: " + codigo));
        return modelMapper.map(entity, CupsProcedimientoResponseDTO.class);
    }
}
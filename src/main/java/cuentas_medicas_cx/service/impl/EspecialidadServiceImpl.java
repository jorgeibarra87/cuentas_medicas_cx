package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.request.EspecialidadRequestDTO;
import cuentas_medicas_cx.model.dto.response.EspecialidadResponseDTO;
import cuentas_medicas_cx.model.entity.Especialidad;
import cuentas_medicas_cx.repository.EspecialidadRepository;
import cuentas_medicas_cx.service.EspecialidadService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public EspecialidadResponseDTO crear(EspecialidadRequestDTO request) {
        Especialidad entity = new Especialidad();
        entity.setNombre(request.getNombre());
        if (request.getEstado() != null) {
            entity.setEstado(request.getEstado());
        }
        Especialidad guardado = especialidadRepository.save(entity);
        return modelMapper.map(guardado, EspecialidadResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public EspecialidadResponseDTO obtenerPorId(Long id) {
        Especialidad entity = especialidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con id: " + id));
        return modelMapper.map(entity, EspecialidadResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecialidadResponseDTO> listarTodos() {
        return especialidadRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, EspecialidadResponseDTO.class))
                .toList();
    }

    @Override
    public EspecialidadResponseDTO actualizar(Long id, EspecialidadRequestDTO request) {
        Especialidad entity = especialidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con id: " + id));
        entity.setNombre(request.getNombre());
        Especialidad actualizado = especialidadRepository.save(entity);
        return modelMapper.map(actualizado, EspecialidadResponseDTO.class);
    }

    @Override
    public EspecialidadResponseDTO cambiarEstado(Long id, Boolean estado) {
        Especialidad entity = especialidadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con id: " + id));
        entity.setEstado(estado);
        return modelMapper.map(especialidadRepository.save(entity), EspecialidadResponseDTO.class);
    }

    @Override
    public void eliminar(Long id) {
        if (!especialidadRepository.existsById(id)) {
            throw new EntityNotFoundException("Especialidad no encontrada con id: " + id);
        }
        especialidadRepository.deleteById(id);
    }
}
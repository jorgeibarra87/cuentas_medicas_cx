package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.request.EntidadesSaludRequestDTO;
import cuentas_medicas_cx.model.dto.response.EntidadesSaludResponseDTO;
import cuentas_medicas_cx.model.entity.EntidadesSalud;
import cuentas_medicas_cx.repository.EntidadesSaludRepository;
import cuentas_medicas_cx.service.EntidadesSaludService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EntidadesSaludServiceImpl implements EntidadesSaludService {

    private final EntidadesSaludRepository entidadesSaludRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public EntidadesSaludResponseDTO crear(EntidadesSaludRequestDTO request) {
        EntidadesSalud entity = new EntidadesSalud();
        entity.setNombre(request.getNombre());
        entity.setTipo(request.getTipo());
        if (request.getEstado() != null) {
            entity.setEstado(request.getEstado());
        }
        EntidadesSalud guardado = entidadesSaludRepository.save(entity);
        return modelMapper.map(guardado, EntidadesSaludResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public EntidadesSaludResponseDTO obtenerPorId(Long id) {
        EntidadesSalud entity = entidadesSaludRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entidad de salud no encontrada con id: " + id));
        return modelMapper.map(entity, EntidadesSaludResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntidadesSaludResponseDTO> listarTodos() {
        return entidadesSaludRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, EntidadesSaludResponseDTO.class))
                .toList();
    }

    @Override
    public EntidadesSaludResponseDTO actualizar(Long id, EntidadesSaludRequestDTO request) {
        EntidadesSalud entity = entidadesSaludRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entidad de salud no encontrada con id: " + id));
        entity.setNombre(request.getNombre());
        entity.setTipo(request.getTipo());
        EntidadesSalud actualizado = entidadesSaludRepository.save(entity);
        return modelMapper.map(actualizado, EntidadesSaludResponseDTO.class);
    }

    @Override
    public EntidadesSaludResponseDTO cambiarEstado(Long id, Boolean estado) {
        EntidadesSalud entity = entidadesSaludRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entidad de salud no encontrada con id: " + id));
        entity.setEstado(estado);
        return modelMapper.map(entidadesSaludRepository.save(entity), EntidadesSaludResponseDTO.class);
    }

    @Override
    public void eliminar(Long id) {
        if (!entidadesSaludRepository.existsById(id)) {
            throw new EntityNotFoundException("Entidad de salud no encontrada con id: " + id);
        }
        entidadesSaludRepository.deleteById(id);
    }
}
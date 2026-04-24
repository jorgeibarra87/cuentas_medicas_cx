package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.request.MedicoRequestDTO;
import cuentas_medicas_cx.model.dto.response.MedicoResponseDTO;
import cuentas_medicas_cx.model.entity.Especialidad;
import cuentas_medicas_cx.model.entity.Medico;
import cuentas_medicas_cx.repository.EspecialidadRepository;
import cuentas_medicas_cx.repository.MedicoRepository;
import cuentas_medicas_cx.service.MedicoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public MedicoResponseDTO crear(MedicoRequestDTO request) {
        Medico entity = new Medico();
        entity.setNombreCompleto(request.getNombreCompleto());
        entity.setRegistroMedico(request.getRegistroMedico());
        if (request.getEspecialidadId() != null) {
            Especialidad especialidad = especialidadRepository.findById(request.getEspecialidadId())
                    .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con id: " + request.getEspecialidadId()));
            entity.setEspecialidad(especialidad);
        }
        if (request.getEstado() != null) {
            entity.setEstado(request.getEstado());
        }
        Medico guardado = medicoRepository.save(entity);
        return modelMapper.map(guardado, MedicoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponseDTO obtenerPorId(Long id) {
        Medico entity = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado con id: " + id));
        return modelMapper.map(entity, MedicoResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponseDTO> listarTodos() {
        return medicoRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, MedicoResponseDTO.class))
                .toList();
    }

    @Override
    public MedicoResponseDTO actualizar(Long id, MedicoRequestDTO request) {
        Medico entity = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado con id: " + id));
        entity.setNombreCompleto(request.getNombreCompleto());
        entity.setRegistroMedico(request.getRegistroMedico());
        if (request.getEspecialidadId() != null) {
            Especialidad especialidad = especialidadRepository.findById(request.getEspecialidadId())
                    .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con id: " + request.getEspecialidadId()));
            entity.setEspecialidad(especialidad);
        }
        Medico actualizado = medicoRepository.save(entity);
        return modelMapper.map(actualizado, MedicoResponseDTO.class);
    }

    @Override
    public MedicoResponseDTO cambiarEstado(Long id, Boolean estado) {
        Medico entity = medicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado con id: " + id));
        entity.setEstado(estado);
        return modelMapper.map(medicoRepository.save(entity), MedicoResponseDTO.class);
    }

    @Override
    public void eliminar(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new EntityNotFoundException("Medico no encontrado con id: " + id);
        }
        medicoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponseDTO> listarPorEspecialidad(Long especialidadId) {
        return medicoRepository.findByEspecialidadId(especialidadId).stream()
                .map(entity -> modelMapper.map(entity, MedicoResponseDTO.class))
                .toList();
    }
}
package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.request.PacienteRequestDTO;
import cuentas_medicas_cx.model.dto.response.PacienteResponseDTO;
import cuentas_medicas_cx.model.entity.Paciente;
import cuentas_medicas_cx.repository.PacienteRepository;
import cuentas_medicas_cx.service.PacienteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PacienteResponseDTO crear(PacienteRequestDTO request) {
        Paciente entity = new Paciente();
        entity.setNumeroIdentificacion(request.getNumeroIdentificacion());
        entity.setTipoIdentificacion(request.getTipoIdentificacion());
        entity.setNombre(request.getNombre());
        entity.setFechaNacimiento(request.getFechaNacimiento());
        if (request.getEstado() != null) {
            entity.setEstado(request.getEstado());
        }
        Paciente guardado = pacienteRepository.save(entity);
        return modelMapper.map(guardado, PacienteResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponseDTO obtenerPorId(Long id) {
        Paciente entity = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + id));
        return modelMapper.map(entity, PacienteResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> listarTodos() {
        return pacienteRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, PacienteResponseDTO.class))
                .toList();
    }

    @Override
    public PacienteResponseDTO actualizar(Long id, PacienteRequestDTO request) {
        Paciente entity = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + id));
        entity.setNumeroIdentificacion(request.getNumeroIdentificacion());
        entity.setTipoIdentificacion(request.getTipoIdentificacion());
        entity.setNombre(request.getNombre());
        entity.setFechaNacimiento(request.getFechaNacimiento());
        Paciente actualizado = pacienteRepository.save(entity);
        return modelMapper.map(actualizado, PacienteResponseDTO.class);
    }

    @Override
    public PacienteResponseDTO cambiarEstado(Long id, Boolean estado) {
        Paciente entity = pacienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + id));
        entity.setEstado(estado);
        return modelMapper.map(pacienteRepository.save(entity), PacienteResponseDTO.class);
    }

    @Override
    public void eliminar(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Paciente no encontrado con id: " + id);
        }
        pacienteRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponseDTO buscarPorIdentificacion(String numeroIdentificacion) {
        Paciente entity = pacienteRepository.findByNumeroIdentificacion(numeroIdentificacion)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con identificacion: " + numeroIdentificacion));
        return modelMapper.map(entity, PacienteResponseDTO.class);
    }
}
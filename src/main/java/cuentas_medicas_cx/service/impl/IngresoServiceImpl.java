package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.request.IngresoRequestDTO;
import cuentas_medicas_cx.model.dto.response.IngresoResponseDTO;
import cuentas_medicas_cx.model.entity.Ingreso;
import cuentas_medicas_cx.model.entity.Paciente;
import cuentas_medicas_cx.repository.IngresoRepository;
import cuentas_medicas_cx.repository.PacienteRepository;
import cuentas_medicas_cx.service.IngresoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IngresoServiceImpl implements IngresoService {

    private final IngresoRepository ingresoRepository;
    private final PacienteRepository pacienteRepository;

    @Override
    @Transactional
    public IngresoResponseDTO crear(IngresoRequestDTO request) {
        Ingreso entity = new Ingreso();
        entity.setNumeroIngreso(request.getNumeroIngreso());

        if (request.getPacienteId() != null) {
            Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + request.getPacienteId()));
            entity.setPaciente(paciente);
        }

        Ingreso guardado = ingresoRepository.save(entity);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public IngresoResponseDTO obtenerPorId(Long id) {
        Ingreso entity = ingresoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingreso no encontrado con id: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngresoResponseDTO> listarTodos() {
        return ingresoRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngresoResponseDTO> listarPorPaciente(Long pacienteId) {
        return ingresoRepository.findByPacienteId(pacienteId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public IngresoResponseDTO actualizar(Long id, IngresoRequestDTO request) {
        Ingreso entity = ingresoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingreso no encontrado con id: " + id));

        entity.setNumeroIngreso(request.getNumeroIngreso());

        if (request.getPacienteId() != null) {
            Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + request.getPacienteId()));
            entity.setPaciente(paciente);
        }

        Ingreso actualizado = ingresoRepository.save(entity);
        return mapToResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!ingresoRepository.existsById(id)) {
            throw new EntityNotFoundException("Ingreso no encontrado con id: " + id);
        }
        ingresoRepository.deleteById(id);
    }

    private IngresoResponseDTO mapToResponse(Ingreso entity) {
        IngresoResponseDTO dto = new IngresoResponseDTO();
        dto.setId(entity.getId());
        dto.setNumeroIngreso(entity.getNumeroIngreso());
        dto.setEstado(entity.getEstado());

        if (entity.getPaciente() != null) {
            dto.setPacienteId(entity.getPaciente().getId());
            dto.setPacienteNumeroIdentificacion(entity.getPaciente().getNumeroIdentificacion());
        }

        return dto;
    }
}
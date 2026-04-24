package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.request.CirugiaRequestDTO;
import cuentas_medicas_cx.model.dto.response.CirugiaResponseDTO;
import cuentas_medicas_cx.model.entity.*;
import cuentas_medicas_cx.repository.*;
import cuentas_medicas_cx.service.CirugiaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CirugiaServiceImpl implements CirugiaService {

    private final CirugiaRepository cirugiaRepository;
    private final PacienteRepository pacienteRepository;
    private final CupsProcedimientoRepository cupsProcedimientoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final MedicoRepository medicoRepository;
    private final EntidadesSaludRepository entidadesSaludRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CirugiaResponseDTO crear(CirugiaRequestDTO request) {
        Cirugia entity = new Cirugia();
        entity.setTipoProcedimiento(request.getTipoProcedimiento());

        if (request.getPacienteId() != null) {
            Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + request.getPacienteId()));
            entity.setPaciente(paciente);
        }

        entity.setIngreso(request.getIngreso());

        if (request.getCupsId() != null) {
            CupsProcedimiento cups = cupsProcedimientoRepository.findById(request.getCupsId())
                    .orElseThrow(() -> new EntityNotFoundException("Procedimiento CUPS no encontrado con id: " + request.getCupsId()));
            entity.setCups(cups);
        }

        entity.setProcedCod(request.getProcedCod());
        entity.setGqx(request.getGqx());
        entity.setIntervencion(request.getIntervencion());

        if (request.getEspecialidadId() != null) {
            Especialidad especialidad = especialidadRepository.findById(request.getEspecialidadId())
                    .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con id: " + request.getEspecialidadId()));
            entity.setEspecialidad(especialidad);
        }

        if (request.getMedicoId() != null) {
            Medico medico = medicoRepository.findById(request.getMedicoId())
                    .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado con id: " + request.getMedicoId()));
            entity.setMedico(medico);
        }

        if (request.getAnestesiologoId() != null) {
            Medico anestesiologo = medicoRepository.findById(request.getAnestesiologoId())
                    .orElseThrow(() -> new EntityNotFoundException("Anestesiologo no encontrado con id: " + request.getAnestesiologoId()));
            entity.setAnestesiologo(anestesiologo);
        }

        if (request.getEntidadSaludId() != null) {
            EntidadesSalud entidadSalud = entidadesSaludRepository.findById(request.getEntidadSaludId())
                    .orElseThrow(() -> new EntityNotFoundException("Entidad de salud no encontrada con id: " + request.getEntidadSaludId()));
            entity.setEntidadSalud(entidadSalud);
        }

        entity.setAyudante1(request.getAyudante1());
        entity.setAyudante2(request.getAyudante2());
        entity.setLiquidacion(request.getLiquidacion());
        entity.setAuditoriaPorcentaje(request.getAuditoriaPorcentaje());
        entity.setNovedad(request.getNovedadDesc());
        entity.setAutorizacion(request.getAutorizacion());
        entity.setImagenesDx(request.getImagenesDx());
        entity.setCausaObjecion(request.getCausaObjecion());
        entity.setRevSupervision(request.getRevSupervision());
        entity.setObservacionAuditoria(request.getObservacionAuditoria());

        if (request.getEstadoAuditoria() == null || request.getEstadoAuditoria().isEmpty()) {
            entity.setEstadoAuditoria("PENDIENTE");
        } else {
            entity.setEstadoAuditoria(request.getEstadoAuditoria());
        }

        Cirugia guardado = cirugiaRepository.save(entity);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public CirugiaResponseDTO obtenerPorId(Long id) {
        Cirugia entity = cirugiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cirugia no encontrada con id: " + id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CirugiaResponseDTO> listarTodos() {
        return cirugiaRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CirugiaResponseDTO actualizar(Long id, CirugiaRequestDTO request) {
        Cirugia entity = cirugiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cirugia no encontrada con id: " + id));

        entity.setTipoProcedimiento(request.getTipoProcedimiento());

        if (request.getPacienteId() != null) {
            Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + request.getPacienteId()));
            entity.setPaciente(paciente);
        }

        entity.setIngreso(request.getIngreso());

        if (request.getCupsId() != null) {
            CupsProcedimiento cups = cupsProcedimientoRepository.findById(request.getCupsId())
                    .orElseThrow(() -> new EntityNotFoundException("Procedimiento CUPS no encontrado con id: " + request.getCupsId()));
            entity.setCups(cups);
        }

        entity.setProcedCod(request.getProcedCod());
        entity.setGqx(request.getGqx());
        entity.setIntervencion(request.getIntervencion());

        if (request.getEspecialidadId() != null) {
            Especialidad especialidad = especialidadRepository.findById(request.getEspecialidadId())
                    .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con id: " + request.getEspecialidadId()));
            entity.setEspecialidad(especialidad);
        }

        if (request.getMedicoId() != null) {
            Medico medico = medicoRepository.findById(request.getMedicoId())
                    .orElseThrow(() -> new EntityNotFoundException("Medico no encontrado con id: " + request.getMedicoId()));
            entity.setMedico(medico);
        }

        if (request.getAnestesiologoId() != null) {
            Medico anestesiologo = medicoRepository.findById(request.getAnestesiologoId())
                    .orElseThrow(() -> new EntityNotFoundException("Anestesiologo no encontrado con id: " + request.getAnestesiologoId()));
            entity.setAnestesiologo(anestesiologo);
        }

        if (request.getEntidadSaludId() != null) {
            EntidadesSalud entidadSalud = entidadesSaludRepository.findById(request.getEntidadSaludId())
                    .orElseThrow(() -> new EntityNotFoundException("Entidad de salud no encontrada con id: " + request.getEntidadSaludId()));
            entity.setEntidadSalud(entidadSalud);
        }

        entity.setAyudante1(request.getAyudante1());
        entity.setAyudante2(request.getAyudante2());
        entity.setLiquidacion(request.getLiquidacion());
        entity.setAuditoriaPorcentaje(request.getAuditoriaPorcentaje());
        entity.setNovedad(request.getNovedadDesc());
        entity.setAutorizacion(request.getAutorizacion());
        entity.setImagenesDx(request.getImagenesDx());
        entity.setCausaObjecion(request.getCausaObjecion());
        entity.setRevSupervision(request.getRevSupervision());
        entity.setObservacionAuditoria(request.getObservacionAuditoria());

        Cirugia actualizado = cirugiaRepository.save(entity);
        return mapToResponse(actualizado);
    }

    @Override
    public CirugiaResponseDTO cambiarEstado(Long id, String estado) {
        Cirugia entity = cirugiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cirugia no encontrada con id: " + id));
        entity.setEstadoAuditoria(estado);
        return mapToResponse(cirugiaRepository.save(entity));
    }

    @Override
    public void eliminar(Long id) {
        if (!cirugiaRepository.existsById(id)) {
            throw new EntityNotFoundException("Cirugia no encontrada con id: " + id);
        }
        cirugiaRepository.deleteById(id);
    }

    private CirugiaResponseDTO mapToResponse(Cirugia entity) {
        CirugiaResponseDTO dto = new CirugiaResponseDTO();
        dto.setId(entity.getId());
        dto.setTipoProcedimiento(entity.getTipoProcedimiento());
        dto.setIngreso(entity.getIngreso());
        dto.setProcedCod(entity.getProcedCod());
        dto.setGqx(entity.getGqx());
        dto.setIntervencion(entity.getIntervencion());
        dto.setAyudante1(entity.getAyudante1());
        dto.setAyudante2(entity.getAyudante2());
        dto.setLiquidacion(entity.getLiquidacion());
        dto.setAuditoriaPorcentaje(entity.getAuditoriaPorcentaje());
        dto.setNovedadDesc(entity.getNovedad());
        dto.setAutorizacion(entity.getAutorizacion());
        dto.setImagenesDx(entity.getImagenesDx());
        dto.setCausaObjecion(entity.getCausaObjecion());
        dto.setRevSupervision(entity.getRevSupervision());
        dto.setObservacionAuditoria(entity.getObservacionAuditoria());
        dto.setEstadoAuditoria(entity.getEstadoAuditoria());

        if (entity.getPaciente() != null) {
            dto.setPacienteId(entity.getPaciente().getId());
            dto.setPacienteNumeroIdentificacion(entity.getPaciente().getNumeroIdentificacion());
        }

        if (entity.getCups() != null) {
            dto.setCupsId(entity.getCups().getId());
            dto.setCupsCodigo(entity.getCups().getCodigo());
        }

        if (entity.getEspecialidad() != null) {
            dto.setEspecialidadId(entity.getEspecialidad().getId());
            dto.setEspecialidadNombre(entity.getEspecialidad().getNombre());
        }

        if (entity.getMedico() != null) {
            dto.setMedicoId(entity.getMedico().getId());
            dto.setMedicoNombre(entity.getMedico().getNombreCompleto());
        }

        if (entity.getAnestesiologo() != null) {
            dto.setAnestesiologoId(entity.getAnestesiologo().getId());
            dto.setAnestesiologoNombre(entity.getAnestesiologo().getNombreCompleto());
        }

        if (entity.getEntidadSalud() != null) {
            dto.setEntidadSaludId(entity.getEntidadSalud().getId());
            dto.setEntidadSaludNombre(entity.getEntidadSalud().getNombre());
        }

        return dto;
    }
}
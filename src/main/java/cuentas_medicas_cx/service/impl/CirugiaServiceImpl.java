package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.request.CirugiaRequestDTO;
import cuentas_medicas_cx.model.dto.request.ImportarCirugiasRequestDTO;
import cuentas_medicas_cx.model.dto.response.CirugiaResponseDTO;
import cuentas_medicas_cx.model.dto.response.ImportarCirugiasResponseDTO;
import cuentas_medicas_cx.model.entity.*;
import cuentas_medicas_cx.repository.*;
import cuentas_medicas_cx.service.CirugiaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CirugiaServiceImpl implements CirugiaService {

    private final CirugiaRepository cirugiaRepository;
    private final PacienteRepository pacienteRepository;
    private final IngresoRepository ingresoRepository;
    private final CupsProcedimientoRepository cupsProcedimientoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final MedicoRepository medicoRepository;
    private final EntidadesSaludRepository entidadesSaludRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ImportarCirugiasResponseDTO importarDesdeDinamica(ImportarCirugiasRequestDTO request) {
        ImportarCirugiasResponseDTO response = new ImportarCirugiasResponseDTO();
        response.setRangoFechas(request.getFechaInicio() + " - " + request.getFechaFin());
        List<String> mensajes = new ArrayList<>();
        int exitosos = 0;
        int errores = 0;

        if (request.getDatos() == null || request.getDatos().isEmpty()) {
            response.setTotalRegistros(0);
            response.setErrores(0);
            response.setExitosos(0);
            mensajes.add("No hay datos para importar");
            response.setMensajes(mensajes);
            return response;
        }

        for (ImportarCirugiasRequestDTO.DatosDinamicaDTO dato : request.getDatos()) {
            try {
                Cirugia cirugia = new Cirugia();
                cirugia.setTipoProcedimiento(dato.getTipo());
                cirugia.setProcedCod(dato.getProcedCod());
                cirugia.setGqx(dato.getGqx());
                cirugia.setIntervencion(dato.getIntervencion());
                cirugia.setRegimen(dato.getRegimen());
                cirugia.setFechaSolicitud(dato.getFechaSolicitud());
                cirugia.setFechaCargue(dato.getFechaCargue());
                cirugia.setHoraCargue(dato.getHoraCargue());
                cirugia.setFechaResultado(dato.getFechaResultado());
                cirugia.setEstadoAuditoria("PENDIENTE");

                if (dato.getPaciente() != null && !dato.getPaciente().isEmpty()) {
                    Optional<Paciente> pacienteOpt = pacienteRepository.findByNumeroIdentificacion(dato.getPaciente());
                    if (pacienteOpt.isPresent()) {
                        cirugia.setPaciente(pacienteOpt.get());
                    }
                }

                if (dato.getIngreso() != null && !dato.getIngreso().isEmpty()) {
                    Optional<Ingreso> ingresoOpt = ingresoRepository.findByNumeroIngreso(dato.getIngreso());
                    if (ingresoOpt.isPresent()) {
                        cirugia.setIngreso(ingresoOpt.get());
                    }
                }

                if (dato.getCups() != null && !dato.getCups().isEmpty()) {
                    Optional<CupsProcedimiento> cupsOpt = cupsProcedimientoRepository.findByCodigo(dato.getCups());
                    if (cupsOpt.isPresent()) {
                        cirugia.setCups(cupsOpt.get());
                    }
                }

                if (dato.getEspecialidad() != null && !dato.getEspecialidad().isEmpty()) {
                    Optional<Especialidad> espOpt = especialidadRepository.findByNombreContainingIgnoreCase(dato.getEspecialidad()).stream().findFirst();
                    if (espOpt.isPresent()) {
                        cirugia.setEspecialidad(espOpt.get());
                    }
                }

                if (dato.getMedico() != null && !dato.getMedico().isEmpty()) {
                    Optional<Medico> medicoOpt = medicoRepository.findFirstByNombreCompletoContainingIgnoreCase(dato.getMedico());
                    if (medicoOpt.isPresent()) {
                        cirugia.setMedico(medicoOpt.get());
                    }
                }

                if (dato.getEntidad() != null && !dato.getEntidad().isEmpty()) {
                    Optional<EntidadesSalud> entOpt = entidadesSaludRepository.findByNombreContainingIgnoreCase(dato.getEntidad()).stream().findFirst();
                    if (entOpt.isPresent()) {
                        cirugia.setEntidadSalud(entOpt.get());
                    }
                }

                cirugiaRepository.save(cirugia);
                exitosos++;
                mensajes.add("Importado: " + dato.getIngreso() + " - " + dato.getIntervencion());

            } catch (Exception e) {
                errores++;
                mensajes.add("Error al importar: " + dato.getIngreso() + " - " + e.getMessage());
            }
        }

        response.setTotalRegistros(request.getDatos().size());
        response.setExitosos(exitosos);
        response.setErrores(errores);
        response.setMensajes(mensajes);
        return response;
    }

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

        if (request.getIngresoId() != null) {
            Ingreso ingreso = ingresoRepository.findById(request.getIngresoId())
                    .orElseThrow(() -> new EntityNotFoundException("Ingreso no encontrado con id: " + request.getIngresoId()));
            entity.setIngreso(ingreso);
        }

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
    @Transactional(readOnly = true)
    public List<CirugiaResponseDTO> listarPorIngreso(Long ingresoId) {
        return cirugiaRepository.findByIngresoId(ingresoId).stream()
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

        if (request.getIngresoId() != null) {
            Ingreso ingreso = ingresoRepository.findById(request.getIngresoId())
                    .orElseThrow(() -> new EntityNotFoundException("Ingreso no encontrado con id: " + request.getIngresoId()));
            entity.setIngreso(ingreso);
        }

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

        if (entity.getIngreso() != null) {
            dto.setIngresoId(entity.getIngreso().getId());
            dto.setIngresoNumero(entity.getIngreso().getNumeroIngreso());
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
package cuentas_medicas_cx.service.impl;

import cuentas_medicas_cx.model.dto.request.CirugiaRequestDTO;
import cuentas_medicas_cx.model.dto.request.CirugiaUpdateRequestDTO;
import cuentas_medicas_cx.model.dto.request.ImportarCirugiasRequestDTO;
import cuentas_medicas_cx.model.dto.response.CirugiaResponseDTO;
import cuentas_medicas_cx.model.dto.response.ImportarCirugiasResponseDTO;
import cuentas_medicas_cx.model.dto.response.PaginadoDTO;
import cuentas_medicas_cx.model.dto.external.DinamicaCirugiaDTO;
import cuentas_medicas_cx.model.entity.*;
import cuentas_medicas_cx.repository.*;
import cuentas_medicas_cx.service.CirugiaService;
import cuentas_medicas_cx.service.DinamicaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
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
    private final DinamicaService dinamicaService;
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
                cirugia.setFechaSolicitud(normalizarFecha(dato.getFechaSolicitud()));
                cirugia.setFechaCargue(normalizarFecha(dato.getFechaCargue()));
                cirugia.setHoraCargue(dato.getHoraCargue());
                cirugia.setFechaResultado(normalizarFecha(dato.getFechaResultado()));
                cirugia.setEstadoAuditoria("PENDIENTE");

                if (dato.getPaciente() != null && !dato.getPaciente().isEmpty()) {
                    try {
                        List<Paciente> lista = pacienteRepository.findAllByNumeroIdentificacion(dato.getPaciente());
                        if (lista.isEmpty()) {
                            try {
                                Paciente p = new Paciente();
                                p.setNumeroIdentificacion(dato.getPaciente());
                                p.setNombre(dato.getNombres() != null ? dato.getNombres() : "");
                                p = pacienteRepository.saveAndFlush(p);
                                cirugia.setPaciente(p);
                            } catch (Exception e) {
                                lista = pacienteRepository.findAllByNumeroIdentificacion(dato.getPaciente());
                                if (!lista.isEmpty()) cirugia.setPaciente(lista.get(0));
                            }
                        } else {
                            cirugia.setPaciente(lista.get(0));
                        }
                    } catch (Exception ex) { /* ignore */ }
                }

                if (dato.getIngreso() != null && !dato.getIngreso().isEmpty()) {
                    try {
                        List<Ingreso> lista = ingresoRepository.findAllByNumeroIngreso(dato.getIngreso());
                        if (lista.isEmpty() && cirugia.getPaciente() != null) {
                            try {
                                Ingreso i = new Ingreso();
                                i.setNumeroIngreso(dato.getIngreso());
                                i.setPaciente(cirugia.getPaciente());
                                i = ingresoRepository.saveAndFlush(i);
                                cirugia.setIngreso(i);
                            } catch (Exception e) {
                                lista = ingresoRepository.findAllByNumeroIngreso(dato.getIngreso());
                                if (!lista.isEmpty()) cirugia.setIngreso(lista.get(0));
                            }
                        } else if (!lista.isEmpty()) {
                            cirugia.setIngreso(lista.get(0));
                        }
                    } catch (Exception ex) { /* ignore */ }
                }

                if (dato.getCups() != null && !dato.getCups().isEmpty()) {
                    try {
                        List<CupsProcedimiento> lista = cupsProcedimientoRepository.findAllByCodigo(dato.getCups());
                        if (lista.isEmpty()) {
                            try {
                                CupsProcedimiento c = new CupsProcedimiento();
                                c.setCodigo(dato.getCups());
                                c.setDescripcion(dato.getIntervencion() != null ? dato.getIntervencion() : "");
                                c = cupsProcedimientoRepository.saveAndFlush(c);
                                cirugia.setCups(c);
                            } catch (Exception e) {
                                lista = cupsProcedimientoRepository.findAllByCodigo(dato.getCups());
                                if (!lista.isEmpty()) cirugia.setCups(lista.get(0));
                            }
                        } else {
                            cirugia.setCups(lista.get(0));
                        }
                    } catch (Exception ex) { /* ignore */ }
                }

                if (dato.getEspecialidad() != null && !dato.getEspecialidad().isEmpty()) {
                    try {
                        List<Especialidad> lista = especialidadRepository.findAllByNombreContainingIgnoreCase(dato.getEspecialidad());
                        if (lista.isEmpty()) {
                            try {
                                Especialidad e = new Especialidad();
                                e.setNombre(dato.getEspecialidad());
                                e = especialidadRepository.saveAndFlush(e);
                                cirugia.setEspecialidad(e);
                            } catch (Exception ex) {
                                lista = especialidadRepository.findAllByNombreContainingIgnoreCase(dato.getEspecialidad());
                                if (!lista.isEmpty()) cirugia.setEspecialidad(lista.get(0));
                            }
                        } else {
                            cirugia.setEspecialidad(lista.get(0));
                        }
                    } catch (Exception ex) { /* ignore */ }
                }

                if (dato.getMedico() != null && !dato.getMedico().isEmpty()) {
                    try {
                        List<Medico> lista = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(dato.getMedico());
                        if (lista.isEmpty()) {
                            try {
                                Medico m = new Medico();
                                m.setNombreCompleto(dato.getMedico());
                                m = medicoRepository.saveAndFlush(m);
                                cirugia.setMedico(m);
                            } catch (Exception ex) {
                                lista = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(dato.getMedico());
                                if (!lista.isEmpty()) cirugia.setMedico(lista.get(0));
                            }
                        } else {
                            cirugia.setMedico(lista.get(0));
                        }
                    } catch (Exception ex) { /* ignore */ }
                }

                if (dato.getEntidad() != null && !dato.getEntidad().isEmpty()) {
                    try {
                        List<EntidadesSalud> lista = entidadesSaludRepository.findAllByNombreContainingIgnoreCase(dato.getEntidad());
                        if (lista.isEmpty()) {
                            try {
                                EntidadesSalud e = new EntidadesSalud();
                                e.setNombre(dato.getEntidad().trim());
                                e = entidadesSaludRepository.saveAndFlush(e);
                                cirugia.setEntidadSalud(e);
                            } catch (Exception ex) {
                                lista = entidadesSaludRepository.findAllByNombreContainingIgnoreCase(dato.getEntidad());
                                if (!lista.isEmpty()) cirugia.setEntidadSalud(lista.get(0));
                            }
                        } else {
                            cirugia.setEntidadSalud(lista.get(0));
                        }
                    } catch (Exception ex) { /* ignore */ }
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
    @Transactional(readOnly = true)
    public List<DinamicaCirugiaDTO> obtenerDeDinamica(String fechaInicio, String fechaFin) {
        return dinamicaService.obtenerCirugiasPorFechas(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    public ImportarCirugiasResponseDTO importarDesdeDinamicaBD(String fechaInicio, String fechaFin) {
        ImportarCirugiasResponseDTO response = new ImportarCirugiasResponseDTO();
        response.setRangoFechas(fechaInicio + " - " + fechaFin);
        List<String> mensajes = new ArrayList<>();
        int exitosos = 0;
        int errores = 0;
        int omitidos = 0;

        List<DinamicaCirugiaDTO> datosDinamica = dinamicaService.obtenerCirugiasPorFechas(fechaInicio, fechaFin);
        
        if (datosDinamica.isEmpty()) {
            response.setTotalRegistros(0);
            response.setExitosos(0);
            response.setErrores(0);
            mensajes.add("No se encontraron datos en el rango especificado");
            response.setMensajes(mensajes);
            return response;
        }

        for (DinamicaCirugiaDTO dato : datosDinamica) {
            try {
                String fechaCargue = normalizarFecha(dato.getFechaCargue());
                String horaCargue = nvl(dato.getHoraCargue());
                String tipo = nvl(dato.getTipo());
                String procedCod = nvl(dato.getProcedCod());
                String cups = nvl(dato.getCups());
                
                boolean existe = cirugiaRepository.existsByClaveUnica(tipo, procedCod, fechaCargue, horaCargue, cups);
                
                if (existe) {
                    omitidos++;
                    log.warn("DUPLICADO: tipo={} procedCod={} fechaCargue={} horaCargue={}", tipo, procedCod, fechaCargue, horaCargue);
                    continue;
                }

                Cirugia cirugia = new Cirugia();
                cirugia.setTipoProcedimiento(dato.getTipo());
                cirugia.setProcedCod(dato.getProcedCod());
                cirugia.setGqx(dato.getGrupoqxCod());
                cirugia.setIntervencion(dato.getIntervencion());
                cirugia.setRegimen(dato.getRegimen());
                cirugia.setFechaSolicitud(normalizarFecha(dato.getFechaSolicitud()));
                cirugia.setFechaCargue(fechaCargue);
                cirugia.setHoraCargue(horaCargue);
                cirugia.setFechaResultado(normalizarFecha(dato.getFechaResultado()));
                cirugia.setEstadoAuditoria("Pendiente");

                if (dato.getPaciente() != null && !dato.getPaciente().isEmpty()) {
                    List<Paciente> listaPac = pacienteRepository.findAllByNumeroIdentificacion(dato.getPaciente());
                    if (listaPac.isEmpty()) {
                        try {
                            Paciente p = new Paciente();
                            p.setNumeroIdentificacion(dato.getPaciente());
                            p.setNombre(dato.getNombres() != null ? dato.getNombres() : "");
                            p = pacienteRepository.save(p);
                            cirugia.setPaciente(p);
                        } catch (Exception ex) {
                            listaPac = pacienteRepository.findAllByNumeroIdentificacion(dato.getPaciente());
                            if (!listaPac.isEmpty()) cirugia.setPaciente(listaPac.get(0));
                        }
                    } else {
                        cirugia.setPaciente(listaPac.get(0));
                    }
                }

                if (dato.getIngreso() != null && !dato.getIngreso().isEmpty() && cirugia.getPaciente() != null) {
                    List<Ingreso> listaIng = ingresoRepository.findAllByNumeroIngreso(dato.getIngreso());
                    if (listaIng.isEmpty()) {
                        try {
                            Ingreso i = new Ingreso();
                            i.setNumeroIngreso(dato.getIngreso());
                            i.setPaciente(cirugia.getPaciente());
                            i = ingresoRepository.save(i);
                            cirugia.setIngreso(i);
                        } catch (Exception ex) {
                            listaIng = ingresoRepository.findAllByNumeroIngreso(dato.getIngreso());
                            if (!listaIng.isEmpty()) cirugia.setIngreso(listaIng.get(0));
                        }
                    } else {
                        cirugia.setIngreso(listaIng.get(0));
                    }
                }

                if (dato.getCups() != null && !dato.getCups().isEmpty()) {
                    List<CupsProcedimiento> listaCups = cupsProcedimientoRepository.findAllByCodigo(dato.getCups());
                    if (listaCups.isEmpty()) {
                        try {
                            CupsProcedimiento c = new CupsProcedimiento();
                            c.setCodigo(dato.getCups());
                            c.setDescripcion(dato.getIntervencion() != null ? dato.getIntervencion() : "");
                            c = cupsProcedimientoRepository.save(c);
                            cirugia.setCups(c);
                        } catch (Exception ex) {
                            listaCups = cupsProcedimientoRepository.findAllByCodigo(dato.getCups());
                            if (!listaCups.isEmpty()) cirugia.setCups(listaCups.get(0));
                        }
                    } else {
                        cirugia.setCups(listaCups.get(0));
                    }
                }

                if (dato.getEspecialidad() != null && !dato.getEspecialidad().isEmpty()) {
                    List<Especialidad> listaEsp = especialidadRepository.findAllByNombreContainingIgnoreCase(dato.getEspecialidad());
                    if (listaEsp.isEmpty()) {
                        try {
                            Especialidad e = new Especialidad();
                            e.setNombre(dato.getEspecialidad());
                            e = especialidadRepository.save(e);
                            cirugia.setEspecialidad(e);
                        } catch (Exception ex) {
                            listaEsp = especialidadRepository.findAllByNombreContainingIgnoreCase(dato.getEspecialidad());
                            if (!listaEsp.isEmpty()) cirugia.setEspecialidad(listaEsp.get(0));
                        }
                    } else {
                        cirugia.setEspecialidad(listaEsp.get(0));
                    }
                }

                if (dato.getMedico() != null && !dato.getMedico().isEmpty()) {
                    List<Medico> listaMed = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(dato.getMedico());
                    if (listaMed.isEmpty()) {
                        try {
                            Medico m = new Medico();
                            m.setNombreCompleto(dato.getMedico());
                            m = medicoRepository.save(m);
                            cirugia.setMedico(m);
                        } catch (Exception ex) {
                            listaMed = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(dato.getMedico());
                            if (!listaMed.isEmpty()) cirugia.setMedico(listaMed.get(0));
                        }
                    } else {
                        cirugia.setMedico(listaMed.get(0));
                    }
                }

                if (dato.getAnestesiologo() != null && !dato.getAnestesiologo().isEmpty()) {
                    List<Medico> listaAnest = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(dato.getAnestesiologo());
                    if (listaAnest.isEmpty()) {
                        try {
                            Medico m = new Medico();
                            m.setNombreCompleto(dato.getAnestesiologo());
                            m = medicoRepository.save(m);
                            cirugia.setAnestesiologo(m);
                        } catch (Exception ex) {
                            listaAnest = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(dato.getAnestesiologo());
                            if (!listaAnest.isEmpty()) cirugia.setAnestesiologo(listaAnest.get(0));
                        }
                    } else {
                        cirugia.setAnestesiologo(listaAnest.get(0));
                    }
                }

                cirugia.setAyudante1(dato.getAyudante1());
                cirugia.setAyudante2(dato.getAyudante2());

                if (dato.getEntidad() != null && !dato.getEntidad().isEmpty()) {
                    List<EntidadesSalud> listaEnt = entidadesSaludRepository.findAllByNombreContainingIgnoreCase(dato.getEntidad());
                    if (listaEnt.isEmpty()) {
                        try {
                            EntidadesSalud e = new EntidadesSalud();
                            e.setNombre(dato.getEntidad().trim());
                            e = entidadesSaludRepository.save(e);
                            cirugia.setEntidadSalud(e);
                        } catch (Exception ex) {
                            listaEnt = entidadesSaludRepository.findAllByNombreContainingIgnoreCase(dato.getEntidad());
                            if (!listaEnt.isEmpty()) cirugia.setEntidadSalud(listaEnt.get(0));
                        }
                    } else {
                        cirugia.setEntidadSalud(listaEnt.get(0));
                    }
                }

                cirugiaRepository.save(cirugia);
                exitosos++;

            } catch (Exception e) {
                errores++;
                mensajes.add("Error: " + dato.getIngreso() + " - " + e.getMessage());
            }
        }

        mensajes.add(0, "Se procesaron " + datosDinamica.size() + " registros");
        mensajes.add(1, "Guardados: " + exitosos + " | Omitidos: " + omitidos + " | Errores: " + errores);
        response.setTotalRegistros(datosDinamica.size());
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
    @Transactional
    public CirugiaResponseDTO crearDesdeNombres(CirugiaUpdateRequestDTO request) {
        Cirugia entity = new Cirugia();

        if (request.getPacienteNumeroIdentificacion() != null && !request.getPacienteNumeroIdentificacion().isEmpty()) {
            List<Paciente> listaPac = pacienteRepository.findAllByNumeroIdentificacion(request.getPacienteNumeroIdentificacion());
            if (!listaPac.isEmpty()) entity.setPaciente(listaPac.get(0));
        }

        if (request.getIngresoNumero() != null && !request.getIngresoNumero().isEmpty() && entity.getPaciente() != null) {
            List<Ingreso> listaIng = ingresoRepository.findAllByNumeroIngreso(request.getIngresoNumero());
            if (!listaIng.isEmpty()) entity.setIngreso(listaIng.get(0));
        }

        if (request.getCupsCodigo() != null && !request.getCupsCodigo().isEmpty()) {
            List<CupsProcedimiento> listaCups = cupsProcedimientoRepository.findAllByCodigo(request.getCupsCodigo());
            if (!listaCups.isEmpty()) entity.setCups(listaCups.get(0));
        }

        if (request.getEspecialidadNombre() != null && !request.getEspecialidadNombre().isEmpty()) {
            List<Especialidad> listaEsp = especialidadRepository.findAllByNombreContainingIgnoreCase(request.getEspecialidadNombre());
            if (listaEsp.isEmpty()) {
                try {
                    Especialidad e = new Especialidad();
                    e.setNombre(request.getEspecialidadNombre());
                    e = especialidadRepository.save(e);
                    entity.setEspecialidad(e);
                } catch (Exception ex) {
                    listaEsp = especialidadRepository.findAllByNombreContainingIgnoreCase(request.getEspecialidadNombre());
                    if (!listaEsp.isEmpty()) entity.setEspecialidad(listaEsp.get(0));
                }
            } else {
                entity.setEspecialidad(listaEsp.get(0));
            }
        }

        if (request.getMedicoNombre() != null && !request.getMedicoNombre().isEmpty()) {
            List<Medico> listaMed = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getMedicoNombre());
            if (listaMed.isEmpty()) {
                try {
                    Medico m = new Medico();
                    m.setNombreCompleto(request.getMedicoNombre());
                    m = medicoRepository.save(m);
                    entity.setMedico(m);
                } catch (Exception ex) {
                    listaMed = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getMedicoNombre());
                    if (!listaMed.isEmpty()) entity.setMedico(listaMed.get(0));
                }
            } else {
                entity.setMedico(listaMed.get(0));
            }
        }

        if (request.getAnestesiologoNombre() != null && !request.getAnestesiologoNombre().isEmpty()) {
            List<Medico> listaAnest = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getAnestesiologoNombre());
            if (listaAnest.isEmpty()) {
                try {
                    Medico m = new Medico();
                    m.setNombreCompleto(request.getAnestesiologoNombre());
                    m = medicoRepository.save(m);
                    entity.setAnestesiologo(m);
                } catch (Exception ex) {
                    listaAnest = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getAnestesiologoNombre());
                    if (!listaAnest.isEmpty()) entity.setAnestesiologo(listaAnest.get(0));
                }
            } else {
                entity.setAnestesiologo(listaAnest.get(0));
            }
        }

        if (request.getEntidadSaludNombre() != null && !request.getEntidadSaludNombre().isEmpty()) {
            List<EntidadesSalud> listaEnt = entidadesSaludRepository.findAllByNombreContainingIgnoreCase(request.getEntidadSaludNombre());
            if (listaEnt.isEmpty()) {
                try {
                    EntidadesSalud e = new EntidadesSalud();
                    e.setNombre(request.getEntidadSaludNombre().trim());
                    e = entidadesSaludRepository.save(e);
                    entity.setEntidadSalud(e);
                } catch (Exception ex) {
                    listaEnt = entidadesSaludRepository.findAllByNombreContainingIgnoreCase(request.getEntidadSaludNombre());
                    if (!listaEnt.isEmpty()) entity.setEntidadSalud(listaEnt.get(0));
                }
            } else {
                entity.setEntidadSalud(listaEnt.get(0));
            }
        }

        if (request.getTipoProcedimiento() != null) entity.setTipoProcedimiento(request.getTipoProcedimiento());
        if (request.getProcedCod() != null) entity.setProcedCod(request.getProcedCod());
        if (request.getGqx() != null) entity.setGqx(request.getGqx());
        if (request.getIntervencion() != null) entity.setIntervencion(request.getIntervencion());
        if (request.getAyudante1() != null) entity.setAyudante1(request.getAyudante1());
        if (request.getAyudante2() != null) entity.setAyudante2(request.getAyudante2());
        if (request.getLiquidacion() != null) entity.setLiquidacion(request.getLiquidacion());
        if (request.getAuditoriaPorcentaje() != null) entity.setAuditoriaPorcentaje(request.getAuditoriaPorcentaje());
        if (request.getNovedadDesc() != null) entity.setNovedad(request.getNovedadDesc());
        if (request.getAutorizacion() != null) entity.setAutorizacion(request.getAutorizacion());
        if (request.getImagenesDx() != null) entity.setImagenesDx(request.getImagenesDx());
        if (request.getCausaObjecion() != null) entity.setCausaObjecion(request.getCausaObjecion());
        if (request.getRevSupervision() != null) entity.setRevSupervision(request.getRevSupervision());
        if (request.getObservacionAuditoria() != null) entity.setObservacionAuditoria(request.getObservacionAuditoria());
        if (request.getRegimen() != null) entity.setRegimen(request.getRegimen());
        if (request.getFechaSolicitud() != null) entity.setFechaSolicitud(request.getFechaSolicitud());
        if (request.getFechaCargue() != null) entity.setFechaCargue(request.getFechaCargue());
        if (request.getHoraCargue() != null) entity.setHoraCargue(request.getHoraCargue());
        if (request.getFechaResultado() != null) entity.setFechaResultado(request.getFechaResultado());

        if (request.getEstadoAuditoria() == null || request.getEstadoAuditoria().isEmpty()) {
            entity.setEstadoAuditoria("PENDIENTE");
        } else {
            entity.setEstadoAuditoria(request.getEstadoAuditoria());
        }

        Cirugia guardado = cirugiaRepository.save(entity);
        return mapToResponse(guardado);
    }

    @Override
    @Transactional
    public CirugiaResponseDTO duplicar(Long id, CirugiaUpdateRequestDTO request) {
        Cirugia original = cirugiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cirugia no encontrada con id: " + id));

        Cirugia entity = new Cirugia();
        entity.setTipoProcedimiento(original.getTipoProcedimiento());
        entity.setPaciente(original.getPaciente());
        entity.setIngreso(original.getIngreso());
        entity.setCups(original.getCups());
        entity.setProcedCod(original.getProcedCod());
        entity.setGqx(original.getGqx());
        entity.setIntervencion(original.getIntervencion());
        entity.setEspecialidad(original.getEspecialidad());
        entity.setMedico(original.getMedico());
        entity.setAnestesiologo(original.getAnestesiologo());
        entity.setEntidadSalud(original.getEntidadSalud());
        entity.setAyudante1(original.getAyudante1());
        entity.setAyudante2(original.getAyudante2());
        entity.setLiquidacion(original.getLiquidacion());
        entity.setAuditoriaPorcentaje(original.getAuditoriaPorcentaje());
        entity.setNovedad(original.getNovedad());
        entity.setAutorizacion(original.getAutorizacion());
        entity.setImagenesDx(original.getImagenesDx());
        entity.setCausaObjecion(original.getCausaObjecion());
        entity.setRevSupervision(original.getRevSupervision());
        entity.setObservacionAuditoria(original.getObservacionAuditoria());
        entity.setRegimen(original.getRegimen());
        entity.setFechaSolicitud(original.getFechaSolicitud());
        entity.setFechaCargue(original.getFechaCargue());
        entity.setHoraCargue(original.getHoraCargue());
        entity.setFechaResultado(original.getFechaResultado());

        if (request.getPacienteNumeroIdentificacion() != null && !request.getPacienteNumeroIdentificacion().isEmpty()) {
            List<Paciente> listaPac = pacienteRepository.findAllByNumeroIdentificacion(request.getPacienteNumeroIdentificacion());
            if (!listaPac.isEmpty()) entity.setPaciente(listaPac.get(0));
        }

        if (request.getIngresoNumero() != null && !request.getIngresoNumero().isEmpty() && entity.getPaciente() != null) {
            List<Ingreso> listaIng = ingresoRepository.findAllByNumeroIngreso(request.getIngresoNumero());
            if (!listaIng.isEmpty()) entity.setIngreso(listaIng.get(0));
        }

        if (request.getCupsCodigo() != null && !request.getCupsCodigo().isEmpty()) {
            List<CupsProcedimiento> listaCups = cupsProcedimientoRepository.findAllByCodigo(request.getCupsCodigo());
            if (!listaCups.isEmpty()) entity.setCups(listaCups.get(0));
        }

        if (request.getEspecialidadNombre() != null && !request.getEspecialidadNombre().isEmpty()) {
            List<Especialidad> listaEsp = especialidadRepository.findAllByNombreContainingIgnoreCase(request.getEspecialidadNombre());
            if (listaEsp.isEmpty()) {
                try {
                    Especialidad e = new Especialidad();
                    e.setNombre(request.getEspecialidadNombre());
                    e = especialidadRepository.save(e);
                    entity.setEspecialidad(e);
                } catch (Exception ex) {
                    listaEsp = especialidadRepository.findAllByNombreContainingIgnoreCase(request.getEspecialidadNombre());
                    if (!listaEsp.isEmpty()) entity.setEspecialidad(listaEsp.get(0));
                }
            } else {
                entity.setEspecialidad(listaEsp.get(0));
            }
        }

        if (request.getMedicoNombre() != null && !request.getMedicoNombre().isEmpty()) {
            List<Medico> listaMed = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getMedicoNombre());
            if (listaMed.isEmpty()) {
                try {
                    Medico m = new Medico();
                    m.setNombreCompleto(request.getMedicoNombre());
                    m = medicoRepository.save(m);
                    entity.setMedico(m);
                } catch (Exception ex) {
                    listaMed = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getMedicoNombre());
                    if (!listaMed.isEmpty()) entity.setMedico(listaMed.get(0));
                }
            } else {
                entity.setMedico(listaMed.get(0));
            }
        }

        if (request.getAnestesiologoNombre() != null && !request.getAnestesiologoNombre().isEmpty()) {
            List<Medico> listaAnest = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getAnestesiologoNombre());
            if (listaAnest.isEmpty()) {
                try {
                    Medico m = new Medico();
                    m.setNombreCompleto(request.getAnestesiologoNombre());
                    m = medicoRepository.save(m);
                    entity.setAnestesiologo(m);
                } catch (Exception ex) {
                    listaAnest = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getAnestesiologoNombre());
                    if (!listaAnest.isEmpty()) entity.setAnestesiologo(listaAnest.get(0));
                }
            } else {
                entity.setAnestesiologo(listaAnest.get(0));
            }
        }

        if (request.getEntidadSaludNombre() != null && !request.getEntidadSaludNombre().isEmpty()) {
            List<EntidadesSalud> listaEnt = entidadesSaludRepository.findAllByNombreContainingIgnoreCase(request.getEntidadSaludNombre());
            if (listaEnt.isEmpty()) {
                try {
                    EntidadesSalud e = new EntidadesSalud();
                    e.setNombre(request.getEntidadSaludNombre().trim());
                    e = entidadesSaludRepository.save(e);
                    entity.setEntidadSalud(e);
                } catch (Exception ex) {
                    listaEnt = entidadesSaludRepository.findAllByNombreContainingIgnoreCase(request.getEntidadSaludNombre());
                    if (!listaEnt.isEmpty()) entity.setEntidadSalud(listaEnt.get(0));
                }
            } else {
                entity.setEntidadSalud(listaEnt.get(0));
            }
        }

        if (request.getTipoProcedimiento() != null) entity.setTipoProcedimiento(request.getTipoProcedimiento());
        if (request.getProcedCod() != null) entity.setProcedCod(request.getProcedCod());
        if (request.getGqx() != null) entity.setGqx(request.getGqx());
        if (request.getIntervencion() != null) entity.setIntervencion(request.getIntervencion());
        if (request.getAyudante1() != null) entity.setAyudante1(request.getAyudante1());
        if (request.getAyudante2() != null) entity.setAyudante2(request.getAyudante2());
        if (request.getLiquidacion() != null) entity.setLiquidacion(request.getLiquidacion());
        if (request.getAuditoriaPorcentaje() != null) entity.setAuditoriaPorcentaje(request.getAuditoriaPorcentaje());
        if (request.getNovedadDesc() != null) entity.setNovedad(request.getNovedadDesc());
        if (request.getAutorizacion() != null) entity.setAutorizacion(request.getAutorizacion());
        if (request.getImagenesDx() != null) entity.setImagenesDx(request.getImagenesDx());
        if (request.getCausaObjecion() != null) entity.setCausaObjecion(request.getCausaObjecion());
        if (request.getRevSupervision() != null) entity.setRevSupervision(request.getRevSupervision());
        if (request.getObservacionAuditoria() != null) entity.setObservacionAuditoria(request.getObservacionAuditoria());
        if (request.getRegimen() != null) entity.setRegimen(request.getRegimen());
        if (request.getFechaSolicitud() != null) entity.setFechaSolicitud(request.getFechaSolicitud());
        if (request.getFechaCargue() != null) entity.setFechaCargue(request.getFechaCargue());
        if (request.getHoraCargue() != null) entity.setHoraCargue(request.getHoraCargue());
        if (request.getFechaResultado() != null) entity.setFechaResultado(request.getFechaResultado());

        if (request.getEstadoAuditoria() != null) entity.setEstadoAuditoria(request.getEstadoAuditoria());

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
    public PaginadoDTO<CirugiaResponseDTO> listarTodosPageable(String fechaInicio, String fechaFin, String busqueda, String tipo, Long entidadId, int page, int size) {
        log.info("📋 Listar pageable - fechaInicio: {}, fechaFin: {}, busqueda: {}, tipo: {}, entidadId: {}, page: {}, size: {}", fechaInicio, fechaFin, busqueda, tipo, entidadId, page, size);
        Sort sort = Sort.by(Sort.Direction.DESC, "fechaCargue").and(Sort.by(Sort.Direction.DESC, "horaCargue"));
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Cirugia> cirugiasPage;
        Page<CirugiaResponseDTO> resultPage;

        boolean hasBusqueda = busqueda != null && !busqueda.isEmpty();
        boolean hasTipo = tipo != null && !tipo.isEmpty();
        boolean hasEntidad = entidadId != null;
        boolean hasFechas = fechaInicio != null && !fechaInicio.isEmpty() && fechaFin != null && !fechaFin.isEmpty();

        if (hasBusqueda && hasTipo && hasEntidad) {
            log.info("🔍 Buscando por busqueda + tipo + entidad");
            cirugiasPage = cirugiaRepository.buscarPorConFiltros(busqueda, tipo, entidadId, pageRequest);
        } else if (hasBusqueda && hasTipo) {
            log.info("🔍 Buscando por busqueda + tipo");
            cirugiasPage = cirugiaRepository.buscarPorConTipo(busqueda, tipo, pageRequest);
        } else if (hasBusqueda && hasEntidad) {
            log.info("🔍 Buscando por busqueda + entidad");
            cirugiasPage = cirugiaRepository.buscarPorConEntidad(busqueda, entidadId, pageRequest);
        } else if (hasBusqueda) {
            log.info("🔍 Buscando por término: {}", busqueda);
            cirugiasPage = cirugiaRepository.buscarPor(busqueda, pageRequest);
        } else if (hasTipo && hasEntidad) {
            log.info("🔍 Buscando por tipo: {} y entidad: {}", tipo, entidadId);
            cirugiasPage = cirugiaRepository.findByTipoProcedimientoAndEntidadSaludId(tipo, entidadId, pageRequest);
        } else if (hasTipo) {
            log.info("🔍 Buscando por tipo: {}", tipo);
            cirugiasPage = cirugiaRepository.findByTipoProcedimiento(tipo, pageRequest);
        } else if (hasEntidad) {
            log.info("🔍 Buscando por entidad: {}", entidadId);
            cirugiasPage = cirugiaRepository.findByEntidadSaludId(entidadId, pageRequest);
        } else if (hasFechas) {
            log.info("🔍 Buscando por rango de fechas (fechaCargue): {} a {}", fechaInicio, fechaFin);
            cirugiasPage = cirugiaRepository.findByFechaCargueBetween(fechaInicio, fechaFin, pageRequest);
        } else {
            log.info("🔍 Buscando todas las cirugías (sin filtro)");
            cirugiasPage = cirugiaRepository.findAllByOrderByFechaCargueDescHoraCargueDesc(pageRequest);
        }
        resultPage = cirugiasPage.map(this::mapToResponse);
        log.info("✅ Total resultados: {}", cirugiasPage.getTotalElements());
        return new PaginadoDTO<>(resultPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CirugiaResponseDTO> listarPorIngreso(Long ingresoId) {
        return cirugiaRepository.findByIngresoId(ingresoId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public CirugiaResponseDTO actualizar(Long id, CirugiaUpdateRequestDTO request) {
        Cirugia entity = cirugiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cirugia no encontrada con id: " + id));

        if (request.getPacienteNumeroIdentificacion() != null && !request.getPacienteNumeroIdentificacion().isEmpty()) {
            List<Paciente> listaPac = pacienteRepository.findAllByNumeroIdentificacion(request.getPacienteNumeroIdentificacion());
            if (!listaPac.isEmpty()) entity.setPaciente(listaPac.get(0));
        }

        if (request.getIngresoNumero() != null && !request.getIngresoNumero().isEmpty() && entity.getPaciente() != null) {
            List<Ingreso> listaIng = ingresoRepository.findAllByNumeroIngreso(request.getIngresoNumero());
            if (!listaIng.isEmpty()) entity.setIngreso(listaIng.get(0));
        }

        if (request.getCupsCodigo() != null && !request.getCupsCodigo().isEmpty()) {
            List<CupsProcedimiento> listaCups = cupsProcedimientoRepository.findAllByCodigo(request.getCupsCodigo());
            if (!listaCups.isEmpty()) entity.setCups(listaCups.get(0));
        }

        if (request.getEspecialidadNombre() != null && !request.getEspecialidadNombre().isEmpty()) {
            List<Especialidad> listaEsp = especialidadRepository.findAllByNombreContainingIgnoreCase(request.getEspecialidadNombre());
            if (listaEsp.isEmpty()) {
                try {
                    Especialidad e = new Especialidad();
                    e.setNombre(request.getEspecialidadNombre());
                    e = especialidadRepository.save(e);
                    entity.setEspecialidad(e);
                } catch (Exception ex) {
                    listaEsp = especialidadRepository.findAllByNombreContainingIgnoreCase(request.getEspecialidadNombre());
                    if (!listaEsp.isEmpty()) entity.setEspecialidad(listaEsp.get(0));
                }
            } else {
                entity.setEspecialidad(listaEsp.get(0));
            }
        }

        if (request.getMedicoNombre() != null && !request.getMedicoNombre().isEmpty()) {
            List<Medico> listaMed = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getMedicoNombre());
            if (listaMed.isEmpty()) {
                try {
                    Medico m = new Medico();
                    m.setNombreCompleto(request.getMedicoNombre());
                    m = medicoRepository.save(m);
                    entity.setMedico(m);
                } catch (Exception ex) {
                    listaMed = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getMedicoNombre());
                    if (!listaMed.isEmpty()) entity.setMedico(listaMed.get(0));
                }
            } else {
                entity.setMedico(listaMed.get(0));
            }
        }

        if (request.getAnestesiologoNombre() != null && !request.getAnestesiologoNombre().isEmpty()) {
            List<Medico> listaAnest = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getAnestesiologoNombre());
            if (listaAnest.isEmpty()) {
                try {
                    Medico m = new Medico();
                    m.setNombreCompleto(request.getAnestesiologoNombre());
                    m = medicoRepository.save(m);
                    entity.setAnestesiologo(m);
                } catch (Exception ex) {
                    listaAnest = medicoRepository.findAllByNombreCompletoContainingIgnoreCase(request.getAnestesiologoNombre());
                    if (!listaAnest.isEmpty()) entity.setAnestesiologo(listaAnest.get(0));
                }
            } else {
                entity.setAnestesiologo(listaAnest.get(0));
            }
        }

        if (request.getEntidadSaludNombre() != null && !request.getEntidadSaludNombre().isEmpty()) {
            List<EntidadesSalud> listaEnt = entidadesSaludRepository.findAllByNombreContainingIgnoreCase(request.getEntidadSaludNombre());
            if (listaEnt.isEmpty()) {
                try {
                    EntidadesSalud e = new EntidadesSalud();
                    e.setNombre(request.getEntidadSaludNombre().trim());
                    e = entidadesSaludRepository.save(e);
                    entity.setEntidadSalud(e);
                } catch (Exception ex) {
                    listaEnt = entidadesSaludRepository.findAllByNombreContainingIgnoreCase(request.getEntidadSaludNombre());
                    if (!listaEnt.isEmpty()) entity.setEntidadSalud(listaEnt.get(0));
                }
            } else {
                entity.setEntidadSalud(listaEnt.get(0));
            }
        }

        if (request.getTipoProcedimiento() != null) entity.setTipoProcedimiento(request.getTipoProcedimiento());
        if (request.getProcedCod() != null) entity.setProcedCod(request.getProcedCod());
        if (request.getGqx() != null) entity.setGqx(request.getGqx());
        if (request.getIntervencion() != null) entity.setIntervencion(request.getIntervencion());
        if (request.getAyudante1() != null) entity.setAyudante1(request.getAyudante1());
        if (request.getAyudante2() != null) entity.setAyudante2(request.getAyudante2());
        if (request.getLiquidacion() != null) entity.setLiquidacion(request.getLiquidacion());
        if (request.getAuditoriaPorcentaje() != null) entity.setAuditoriaPorcentaje(request.getAuditoriaPorcentaje());
        if (request.getNovedadDesc() != null) entity.setNovedad(request.getNovedadDesc());
        if (request.getAutorizacion() != null) entity.setAutorizacion(request.getAutorizacion());
        if (request.getImagenesDx() != null) entity.setImagenesDx(request.getImagenesDx());
        if (request.getCausaObjecion() != null) entity.setCausaObjecion(request.getCausaObjecion());
        if (request.getRevSupervision() != null) entity.setRevSupervision(request.getRevSupervision());
        if (request.getObservacionAuditoria() != null) entity.setObservacionAuditoria(request.getObservacionAuditoria());
        if (request.getEstadoAuditoria() != null) entity.setEstadoAuditoria(request.getEstadoAuditoria());
        if (request.getRegimen() != null) entity.setRegimen(request.getRegimen());

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
        dto.setRegimen(entity.getRegimen());
        dto.setFechaSolicitud(entity.getFechaSolicitud());
        dto.setFechaCargue(entity.getFechaCargue());
        dto.setHoraCargue(entity.getHoraCargue());
        dto.setFechaResultado(entity.getFechaResultado());

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

    private String buildClaveUnica(String tipo, String procedCod, String cups, String gqx, String paciente) {
        return (tipo != null ? tipo : "") + "|" +
               (procedCod != null ? procedCod : "") + "|" +
               (cups != null ? cups : "") + "|" +
               (gqx != null ? gqx : "") + "|" +
               (paciente != null ? paciente : "");
    }

    private String normalizarFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) return "";
        
        fecha = fecha.trim();
        
        if (fecha.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
            return fecha.substring(0, 10);
        }
        
        try {
            String[] parts;
            if (fecha.contains("-")) {
                parts = fecha.split("-");
            } else if (fecha.contains("/")) {
                parts = fecha.split("/");
            } else if (fecha.contains(" ")) {
                parts = fecha.split("\\s+");
            } else {
                return fecha;
            }
            
            if (parts.length == 3) {
                String p1 = parts[0].trim();
                String p2 = parts[1].trim().toLowerCase();
                String p3 = parts[2].trim().split("\\s")[0].trim();
                
                String dia, mes, anio;
                
                if (p1.length() == 4) {
                    anio = p1; mes = p2; dia = p3;
                } else if (p3.length() == 4) {
                    dia = p1; mes = p2; anio = p3;
                } else {
                    dia = p1; mes = p2; anio = p3;
                }
                
                if (anio.length() == 2) {
                    anio = "20" + anio;
                }
                
                int mesNum;
                switch (mes) {
                    case "ene": case "jan": mesNum = 1; break;
                    case "feb": case "02": case "2": mesNum = 2; break;
                    case "mar": case "03": case "3": mesNum = 3; break;
                    case "abr": case "apr": case "04": case "4": mesNum = 4; break;
                    case "may": case "05": case "5": mesNum = 5; break;
                    case "jun": case "06": case "6": mesNum = 6; break;
                    case "jul": case "07": case "7": mesNum = 7; break;
                    case "ago": case "aug": case "08": case "8": mesNum = 8; break;
                    case "sep": case "09": case "9": mesNum = 9; break;
                    case "oct": case "10": mesNum = 10; break;
                    case "nov": case "11": mesNum = 11; break;
                    case "dic": case "dec": case "12": mesNum = 12; break;
                    default: return fecha;
                }
                
                return String.format("%04d-%02d-%02d", Integer.parseInt(anio), mesNum, Integer.parseInt(dia));
            }
        } catch (Exception e) {
            return fecha;
        }
        return fecha;
    }

    private String nvl(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
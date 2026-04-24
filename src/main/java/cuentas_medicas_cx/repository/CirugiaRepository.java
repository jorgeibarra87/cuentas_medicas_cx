package cuentas_medicas_cx.repository;

import cuentas_medicas_cx.model.entity.Cirugia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CirugiaRepository extends JpaRepository<Cirugia, Long> {
    List<Cirugia> findByEntidadSaludId(Long entidadId);
    List<Cirugia> findByTipoProcedimiento(String tipo);
    List<Cirugia> findByEstadoAuditoria(String estado);
    List<Cirugia> findByIngresoContaining(String ingreso);
    List<Cirugia> findByPacienteNumeroIdentificacion(String numeroIdentificacion);
    List<Cirugia> findByMedicoId(Long medicoId);
    List<Cirugia> findByEspecialidadId(Long especialidadId);
}
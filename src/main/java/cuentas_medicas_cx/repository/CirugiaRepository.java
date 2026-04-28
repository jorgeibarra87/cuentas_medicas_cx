package cuentas_medicas_cx.repository;

import cuentas_medicas_cx.model.entity.Cirugia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CirugiaRepository extends JpaRepository<Cirugia, Long> {
    List<Cirugia> findByEntidadSaludId(Long entidadId);
    List<Cirugia> findByTipoProcedimiento(String tipo);
    List<Cirugia> findByEstadoAuditoria(String estado);
    List<Cirugia> findByPacienteNumeroIdentificacion(String numeroIdentificacion);
    List<Cirugia> findByMedicoId(Long medicoId);
    List<Cirugia> findByEspecialidadId(Long especialidadId);
    List<Cirugia> findByIngresoId(Long ingresoId);

    @Query("SELECT c FROM Cirugia c WHERE c.tipoProcedimiento = :tipo " +
           "AND c.procedCod = :procCod AND c.cups.codigo = :cups " +
           "AND c.gqx = :gqx AND c.paciente.numeroIdentificacion = :paciente")
    Optional<Cirugia> findByClaveUnica(
            @Param("tipo") String tipo,
            @Param("procCod") String procedCod,
            @Param("cups") String cups,
            @Param("gqx") String gqx,
            @Param("paciente") String paciente
    );
}
package cuentas_medicas_cx.repository;

import cuentas_medicas_cx.model.entity.Cirugia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
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
           "AND COALESCE(c.gqx, '') = COALESCE(:gqx, '') AND c.paciente.numeroIdentificacion = :paciente")
    Optional<Cirugia> findByClaveUnica(
            @Param("tipo") String tipo,
            @Param("procCod") String procedCod,
            @Param("cups") String cups,
            @Param("gqx") String gqx,
            @Param("paciente") String paciente
    );

    @Query("SELECT c FROM Cirugia c JOIN c.paciente p " +
           "WHERE c.fechaCargue >= :fechaInicio AND c.fechaCargue <= :fechaFin")
    List<Cirugia> findByRangoFechasCargue(@Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin);

    List<Cirugia> findByFechaCargueBetween(String fechaInicio, String fechaFin);

    @Query("SELECT COUNT(c) > 0 FROM Cirugia c WHERE c.tipoProcedimiento = :tipo " +
           "AND c.procedCod = :procedCod AND c.fechaCargue = :fechaCargue AND c.horaCargue = :horaCargue AND c.cups.codigo = :cups")
    boolean existsByClaveUnica(
            @Param("tipo") String tipo,
            @Param("procedCod") String procedCod,
            @Param("fechaCargue") String fechaCargue,
            @Param("horaCargue") String horaCargue,
            @Param("cups") String cups
    );

    Page<Cirugia> findByFechaCargueBetween(String fechaInicio, String fechaFin, Pageable pageable);

    Page<Cirugia> findByFechaResultadoBetween(String fechaInicio, String fechaFin, Pageable pageable);

    Page<Cirugia> findAllByOrderByFechaCargueDescHoraCargueDesc(Pageable pageable);

    @Query("SELECT c FROM Cirugia c " +
           "LEFT JOIN c.paciente p " +
           "LEFT JOIN c.cups cu " +
           "LEFT JOIN c.medico m " +
           "LEFT JOIN c.ingreso i " +
           "WHERE LOWER(p.numeroIdentificacion) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(cu.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(m.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(i.numeroIngreso) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<Cirugia> buscarPor(@Param("busqueda") String busqueda, Pageable pageable);

    @Query("SELECT c FROM Cirugia c " +
           "LEFT JOIN c.paciente p " +
           "LEFT JOIN c.cups cu " +
           "LEFT JOIN c.medico m " +
           "LEFT JOIN c.ingreso i " +
           "WHERE (LOWER(p.numeroIdentificacion) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(cu.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(m.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(i.numeroIngreso) LIKE LOWER(CONCAT('%', :busqueda, '%'))) " +
           "AND c.tipoProcedimiento = :tipo")
    Page<Cirugia> buscarPorConTipo(@Param("busqueda") String busqueda, @Param("tipo") String tipo, Pageable pageable);

    @Query("SELECT c FROM Cirugia c " +
           "LEFT JOIN c.paciente p " +
           "LEFT JOIN c.cups cu " +
           "LEFT JOIN c.medico m " +
           "LEFT JOIN c.ingreso i " +
           "WHERE (LOWER(p.numeroIdentificacion) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(cu.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(m.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(i.numeroIngreso) LIKE LOWER(CONCAT('%', :busqueda, '%'))) " +
           "AND c.entidadSalud.id = :entidadId")
    Page<Cirugia> buscarPorConEntidad(@Param("busqueda") String busqueda, @Param("entidadId") Long entidadId, Pageable pageable);

    @Query("SELECT c FROM Cirugia c " +
           "LEFT JOIN c.paciente p " +
           "LEFT JOIN c.cups cu " +
           "LEFT JOIN c.medico m " +
           "LEFT JOIN c.ingreso i " +
           "WHERE (LOWER(p.numeroIdentificacion) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(cu.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(m.nombreCompleto) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(i.numeroIngreso) LIKE LOWER(CONCAT('%', :busqueda, '%'))) " +
           "AND c.tipoProcedimiento = :tipo AND c.entidadSalud.id = :entidadId")
    Page<Cirugia> buscarPorConFiltros(@Param("busqueda") String busqueda, @Param("tipo") String tipo, @Param("entidadId") Long entidadId, Pageable pageable);

    Page<Cirugia> findByTipoProcedimientoAndEntidadSaludId(String tipo, Long entidadId, Pageable pageable);

    Page<Cirugia> findByTipoProcedimiento(String tipo, Pageable pageable);

    Page<Cirugia> findByEntidadSaludId(Long entidadId, Pageable pageable);

    @Query(value = "SELECT SUBSTRING(fechaCargue, 6, 2) as mes, COUNT(*) as total FROM cirugias " +
           "WHERE fechaCargue IS NOT NULL AND fechaCargue LIKE :anio " +
           "GROUP BY SUBSTRING(fechaCargue, 6, 2) ORDER BY mes", nativeQuery = true)
    List<Map<String, Object>> contarPorMes(@Param("anio") String anio);

    @Query(value = "SELECT COUNT(*) FROM cirugias WHERE fechaCargue IS NOT NULL AND fechaCargue LIKE :anio", nativeQuery = true)
    long contarTotalPorAnio(@Param("anio") String anio);

    @Query(value = "SELECT SUBSTRING(fechaCargue, 6, 2) as mes, estadoAuditoria as estado, COUNT(*) as total " +
           "FROM cirugias WHERE fechaCargue IS NOT NULL AND fechaCargue LIKE :anio AND estadoAuditoria IN (:estados) " +
           "GROUP BY SUBSTRING(fechaCargue, 6, 2), estadoAuditoria ORDER BY mes", nativeQuery = true)
    List<Map<String, Object>> contarPorMesYEstado(@Param("anio") String anio, @Param("estados") List<String> estados);

    @Query(value = "SELECT e.nombre as especialidad, COUNT(*) as total FROM cirugias c " +
           "JOIN especialidades e ON c.ESPECIALIDAD_ID = e.id " +
           "WHERE fechaCargue IS NOT NULL AND fechaCargue LIKE :anio " +
           "GROUP BY e.nombre ORDER BY total DESC", nativeQuery = true)
    List<Map<String, Object>> contarPorEspecialidad(@Param("anio") String anio);

    @Query(value = "SELECT c.intervencion, COUNT(*) as total FROM cirugias c " +
           "WHERE fechaCargue IS NOT NULL AND fechaCargue LIKE :anio AND c.intervencion IS NOT NULL AND c.intervencion != '' " +
           "GROUP BY c.intervencion ORDER BY total DESC", nativeQuery = true)
    List<Map<String, Object>> contarPorProcedimiento(@Param("anio") String anio);

    @Query(value = "SELECT revSupervision as auditor, COUNT(*) as total FROM cirugias " +
           "WHERE revSupervision IS NOT NULL AND revSupervision != '' AND fechaCargue LIKE :anio " +
           "GROUP BY revSupervision ORDER BY total DESC", nativeQuery = true)
    List<Map<String, Object>> contarPorAuditor(@Param("anio") String anio);
}
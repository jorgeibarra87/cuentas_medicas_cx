package cuentas_medicas_cx.repository;

import cuentas_medicas_cx.model.entity.CupsProcedimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CupsProcedimientoRepository extends JpaRepository<CupsProcedimiento, Long> {
    Optional<CupsProcedimiento> findByCodigo(String codigo);
    Optional<CupsProcedimiento> findByDescripcionContainingIgnoreCase(String descripcion);
    List<CupsProcedimiento> findByGrupoCups(String grupoCups);
    List<CupsProcedimiento> findByEstado(Boolean estado);
}
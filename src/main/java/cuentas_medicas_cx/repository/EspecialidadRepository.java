package cuentas_medicas_cx.repository;

import cuentas_medicas_cx.model.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    Optional<Especialidad> findByNombreContainingIgnoreCase(String nombre);
    List<Especialidad> findByEstado(Boolean estado);
}
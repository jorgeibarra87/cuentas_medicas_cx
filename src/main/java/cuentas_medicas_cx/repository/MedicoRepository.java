package cuentas_medicas_cx.repository;

import cuentas_medicas_cx.model.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    Optional<Medico> findByRegistroMedico(String registroMedico);
    Optional<Medico> findByNombreCompletoContainingIgnoreCase(String nombre);
    List<Medico> findByEspecialidadId(Long especialidadId);
    List<Medico> findByEstado(Boolean estado);
    Optional<Medico> findFirstByNombreCompletoContainingIgnoreCase(String nombre);
}
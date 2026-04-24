package cuentas_medicas_cx.repository;

import cuentas_medicas_cx.model.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByNumeroIdentificacion(String numeroIdentificacion);
    Optional<Paciente> findByNombreContainingIgnoreCase(String nombre);
    List<Paciente> findByTipoIdentificacion(String tipoIdentificacion);
    List<Paciente> findByEstado(Boolean estado);
}
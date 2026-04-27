package cuentas_medicas_cx.repository;

import cuentas_medicas_cx.model.entity.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    Optional<Ingreso> findByNumeroIngreso(String numeroIngreso);
    List<Ingreso> findByPacienteId(Long pacienteId);
}
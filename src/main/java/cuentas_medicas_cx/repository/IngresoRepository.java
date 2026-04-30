package cuentas_medicas_cx.repository;

import cuentas_medicas_cx.model.entity.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {
    List<Ingreso> findByNumeroIngreso(String numeroIngreso);
    List<Ingreso> findByPacienteId(Long pacienteId);
    List<Ingreso> findAllByNumeroIngreso(String numeroIngreso);
}
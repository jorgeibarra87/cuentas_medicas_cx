package cuentas_medicas_cx.repository;

import cuentas_medicas_cx.model.entity.EntidadesSalud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntidadesSaludRepository extends JpaRepository<EntidadesSalud, Long> {
    Optional<EntidadesSalud> findByNombreContainingIgnoreCase(String nombre);
    List<EntidadesSalud> findByEstado(Boolean estado);
}
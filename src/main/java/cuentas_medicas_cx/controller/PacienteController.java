package cuentas_medicas_cx.controller;

import cuentas_medicas_cx.model.dto.request.PacienteRequestDTO;
import cuentas_medicas_cx.model.dto.response.PacienteResponseDTO;
import cuentas_medicas_cx.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @Operation(summary = "Crear paciente", tags = {"Pacientes"})
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> crear(@Valid @RequestBody PacienteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.crear(request));
    }

    @Operation(summary = "Obtener paciente por ID", tags = {"Pacientes"})
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    @Operation(summary = "Listar todos los pacientes", tags = {"Pacientes"})
    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pacienteService.listarTodos());
    }

    @Operation(summary = "Actualizar paciente", tags = {"Pacientes"})
    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO request) {
        return ResponseEntity.ok(pacienteService.actualizar(id, request));
    }

    @Operation(summary = "Cambiar estado de paciente", tags = {"Pacientes"})
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PacienteResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean estado) {
        return ResponseEntity.ok(pacienteService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Eliminar paciente", tags = {"Pacientes"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar paciente por identificacion", tags = {"Pacientes"})
    @GetMapping("/buscar")
    public ResponseEntity<PacienteResponseDTO> buscarPorIdentificacion(@RequestParam String numeroIdentificacion) {
        return ResponseEntity.ok(pacienteService.buscarPorIdentificacion(numeroIdentificacion));
    }
}
package cuentas_medicas_cx.controller;

import cuentas_medicas_cx.model.dto.request.MedicoRequestDTO;
import cuentas_medicas_cx.model.dto.response.MedicoResponseDTO;
import cuentas_medicas_cx.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService medicoService;

    @Operation(summary = "Crear medico", tags = {"Medicos"})
    @PostMapping
    public ResponseEntity<MedicoResponseDTO> crear(@Valid @RequestBody MedicoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicoService.crear(request));
    }

    @Operation(summary = "Obtener medico por ID", tags = {"Medicos"})
    @GetMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicoService.obtenerPorId(id));
    }

    @Operation(summary = "Listar todos los medicos", tags = {"Medicos"})
    @GetMapping
    public ResponseEntity<List<MedicoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(medicoService.listarTodos());
    }

    @Operation(summary = "Actualizar medico", tags = {"Medicos"})
    @PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MedicoRequestDTO request) {
        return ResponseEntity.ok(medicoService.actualizar(id, request));
    }

    @Operation(summary = "Cambiar estado de medico", tags = {"Medicos"})
    @PatchMapping("/{id}/estado")
    public ResponseEntity<MedicoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean estado) {
        return ResponseEntity.ok(medicoService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Eliminar medico", tags = {"Medicos"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        medicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar medicos por especialidad", tags = {"Medicos"})
    @GetMapping("/especialidad/{especialidadId}")
    public ResponseEntity<List<MedicoResponseDTO>> listarPorEspecialidad(@PathVariable Long especialidadId) {
        return ResponseEntity.ok(medicoService.listarPorEspecialidad(especialidadId));
    }
}
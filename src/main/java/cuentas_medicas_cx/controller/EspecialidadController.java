package cuentas_medicas_cx.controller;

import cuentas_medicas_cx.model.dto.request.EspecialidadRequestDTO;
import cuentas_medicas_cx.model.dto.response.EspecialidadResponseDTO;
import cuentas_medicas_cx.service.EspecialidadService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @Operation(summary = "Crear especialidad", tags = {"Especialidades"})
    @PostMapping
    public ResponseEntity<EspecialidadResponseDTO> crear(@Valid @RequestBody EspecialidadRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(especialidadService.crear(request));
    }

    @Operation(summary = "Obtener especialidad por ID", tags = {"Especialidades"})
    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadService.obtenerPorId(id));
    }

    @Operation(summary = "Listar todas las especialidades", tags = {"Especialidades"})
    @GetMapping
    public ResponseEntity<List<EspecialidadResponseDTO>> listarTodos() {
        return ResponseEntity.ok(especialidadService.listarTodos());
    }

    @Operation(summary = "Actualizar especialidad", tags = {"Especialidades"})
    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EspecialidadRequestDTO request) {
        return ResponseEntity.ok(especialidadService.actualizar(id, request));
    }

    @Operation(summary = "Cambiar estado de especialidad", tags = {"Especialidades"})
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EspecialidadResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean estado) {
        return ResponseEntity.ok(especialidadService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Eliminar especialidad", tags = {"Especialidades"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        especialidadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
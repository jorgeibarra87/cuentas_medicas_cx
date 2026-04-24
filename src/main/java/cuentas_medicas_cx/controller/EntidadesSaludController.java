package cuentas_medicas_cx.controller;

import cuentas_medicas_cx.model.dto.request.EntidadesSaludRequestDTO;
import cuentas_medicas_cx.model.dto.response.EntidadesSaludResponseDTO;
import cuentas_medicas_cx.service.EntidadesSaludService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entidades-salud")
@RequiredArgsConstructor
public class EntidadesSaludController {

    private final EntidadesSaludService entidadesSaludService;

    @Operation(summary = "Crear entidad de salud", tags = {"Entidades Salud"})
    @PostMapping
    public ResponseEntity<EntidadesSaludResponseDTO> crear(@Valid @RequestBody EntidadesSaludRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entidadesSaludService.crear(request));
    }

    @Operation(summary = "Obtener entidad por ID", tags = {"Entidades Salud"})
    @GetMapping("/{id}")
    public ResponseEntity<EntidadesSaludResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(entidadesSaludService.obtenerPorId(id));
    }

    @Operation(summary = "Listar todas las entidades", tags = {"Entidades Salud"})
    @GetMapping
    public ResponseEntity<List<EntidadesSaludResponseDTO>> listarTodos() {
        return ResponseEntity.ok(entidadesSaludService.listarTodos());
    }

    @Operation(summary = "Actualizar entidad", tags = {"Entidades Salud"})
    @PutMapping("/{id}")
    public ResponseEntity<EntidadesSaludResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EntidadesSaludRequestDTO request) {
        return ResponseEntity.ok(entidadesSaludService.actualizar(id, request));
    }

    @Operation(summary = "Cambiar estado de entidad", tags = {"Entidades Salud"})
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EntidadesSaludResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean estado) {
        return ResponseEntity.ok(entidadesSaludService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Eliminar entidad", tags = {"Entidades Salud"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entidadesSaludService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
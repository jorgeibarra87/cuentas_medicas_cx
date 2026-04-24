package cuentas_medicas_cx.controller;

import cuentas_medicas_cx.model.dto.request.CupsProcedimientoRequestDTO;
import cuentas_medicas_cx.model.dto.response.CupsProcedimientoResponseDTO;
import cuentas_medicas_cx.service.CupsProcedimientoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cups-procedimientos")
@RequiredArgsConstructor
public class CupsProcedimientoController {

    private final CupsProcedimientoService cupsProcedimientoService;

    @Operation(summary = "Crear procedimiento CUPS", tags = {"CUPS Procedimientos"})
    @PostMapping
    public ResponseEntity<CupsProcedimientoResponseDTO> crear(@Valid @RequestBody CupsProcedimientoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cupsProcedimientoService.crear(request));
    }

    @Operation(summary = "Obtener procedimiento por ID", tags = {"CUPS Procedimientos"})
    @GetMapping("/{id}")
    public ResponseEntity<CupsProcedimientoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cupsProcedimientoService.obtenerPorId(id));
    }

    @Operation(summary = "Listar todos los procedimientos", tags = {"CUPS Procedimientos"})
    @GetMapping
    public ResponseEntity<List<CupsProcedimientoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(cupsProcedimientoService.listarTodos());
    }

    @Operation(summary = "Actualizar procedimiento", tags = {"CUPS Procedimientos"})
    @PutMapping("/{id}")
    public ResponseEntity<CupsProcedimientoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CupsProcedimientoRequestDTO request) {
        return ResponseEntity.ok(cupsProcedimientoService.actualizar(id, request));
    }

    @Operation(summary = "Cambiar estado de procedimiento", tags = {"CUPS Procedimientos"})
    @PatchMapping("/{id}/estado")
    public ResponseEntity<CupsProcedimientoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean estado) {
        return ResponseEntity.ok(cupsProcedimientoService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Eliminar procedimiento", tags = {"CUPS Procedimientos"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cupsProcedimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar procedimiento por codigo", tags = {"CUPS Procedimientos"})
    @GetMapping("/buscar")
    public ResponseEntity<CupsProcedimientoResponseDTO> buscarPorCodigo(@RequestParam String codigo) {
        return ResponseEntity.ok(cupsProcedimientoService.buscarPorCodigo(codigo));
    }
}
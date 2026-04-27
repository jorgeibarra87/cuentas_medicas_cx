package cuentas_medicas_cx.controller;

import cuentas_medicas_cx.model.dto.request.CirugiaRequestDTO;
import cuentas_medicas_cx.model.dto.response.CirugiaResponseDTO;
import cuentas_medicas_cx.service.CirugiaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cirugias")
@RequiredArgsConstructor
public class CirugiaController {

    private final CirugiaService cirugiaService;

    @Operation(summary = "Crear cirugia",
            description = "Permite crear nuevos registros de cirugias.",
            tags={"Cirugias"})
    @PostMapping
    public ResponseEntity<CirugiaResponseDTO> crear(@Valid @RequestBody CirugiaRequestDTO request) {
        CirugiaResponseDTO respuesta = cirugiaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Operation(summary = "Obtener cirugia por ID",
            description = "Recupera la información completa de una cirugia específica por su identificador único.",
            tags={"Cirugias"})
    @GetMapping("/{id}")
    public ResponseEntity<CirugiaResponseDTO> obtenerPorId(@PathVariable Long id) {
        CirugiaResponseDTO respuesta = cirugiaService.obtenerPorId(id);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Listar todos las cirugias",
            description = "Obtiene la lista completa de todos las cirugias registradas en el sistema.",
            tags={"Cirugias"})
    @GetMapping
    public ResponseEntity<List<CirugiaResponseDTO>> listarTodos() {
        List<CirugiaResponseDTO> lista = cirugiaService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Listar cirugías por ingreso",
            description = "Obtiene la lista de cirugías asociadas a un ingreso específico.",
            tags={"Cirugias"})
    @GetMapping("/ingreso/{ingresoId}")
    public ResponseEntity<List<CirugiaResponseDTO>> listarPorIngreso(@PathVariable Long ingresoId) {
        return ResponseEntity.ok(cirugiaService.listarPorIngreso(ingresoId));
    }

    @Operation(summary = "Actualizar cirugia",
            description = "Actualiza completamente la información de una cirugia existente por su ID.",
            tags={"Cirugias"})
    @PutMapping("/{id}")
    public ResponseEntity<CirugiaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CirugiaRequestDTO request
    ) {
        CirugiaResponseDTO respuesta = cirugiaService.actualizar(id, request);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Cambiar estado de cirugia",
            description = "Modifica únicamente el estado de una cirugia específica mediante su ID y el nuevo estado.",
            tags={"Cirugias"})
    @PatchMapping("/{id}/estado")
    public ResponseEntity<CirugiaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(cirugiaService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Eliminar cirugia",
            description = "Elimina permanentemente una cirugia del sistema por su identificador único.",
            tags={"Cirugias"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cirugiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
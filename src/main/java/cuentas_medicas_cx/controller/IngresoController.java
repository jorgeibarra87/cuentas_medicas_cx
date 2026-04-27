package cuentas_medicas_cx.controller;

import cuentas_medicas_cx.model.dto.request.IngresoRequestDTO;
import cuentas_medicas_cx.model.dto.response.IngresoResponseDTO;
import cuentas_medicas_cx.service.IngresoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingresos")
@RequiredArgsConstructor
public class IngresoController {

    private final IngresoService ingresoService;

    @PostMapping
    public ResponseEntity<IngresoResponseDTO> crear(@Valid @RequestBody IngresoRequestDTO request) {
        return new ResponseEntity<>(ingresoService.crear(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngresoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ingresoService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<IngresoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(ingresoService.listarTodos());
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<IngresoResponseDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(ingresoService.listarPorPaciente(pacienteId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngresoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody IngresoRequestDTO request) {
        return ResponseEntity.ok(ingresoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ingresoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
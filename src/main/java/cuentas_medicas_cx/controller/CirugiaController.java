package cuentas_medicas_cx.controller;

import cuentas_medicas_cx.model.dto.request.CirugiaRequestDTO;
import cuentas_medicas_cx.model.dto.request.CirugiaUpdateRequestDTO;
import cuentas_medicas_cx.model.dto.request.ImportarCirugiasRequestDTO;
import cuentas_medicas_cx.model.dto.response.CirugiaResponseDTO;
import cuentas_medicas_cx.model.dto.response.ImportarCirugiasResponseDTO;
import cuentas_medicas_cx.model.dto.response.PaginadoDTO;
import cuentas_medicas_cx.model.dto.external.DinamicaCirugiaDTO;
import cuentas_medicas_cx.service.CirugiaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cirugias")
@RequiredArgsConstructor
public class CirugiaController {

    private final CirugiaService cirugiaService;

    @Operation(summary = "Importar cirugías desde Dinámica (BD)",
            description = "Ejecuta consulta directa a SQL Server de Dinámica y guarda los datos.",
            tags={"Cirugias"})
    @PostMapping("/importar/bd")
    public ResponseEntity<ImportarCirugiasResponseDTO> importarDesdeDinamicaBD(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        return ResponseEntity.ok(cirugiaService.importarDesdeDinamicaBD(fechaInicio, fechaFin));
    }

    @Operation(summary = "Obtener cirugías de Dinámica sin guardar",
            description = "Consulta SQL Server de Dinámica y retorna datos sin guardar.",
            tags={"Cirugias"})
    @GetMapping("/dinamica")
    public ResponseEntity<List<DinamicaCirugiaDTO>> obtenerDeDinamica(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        return ResponseEntity.ok(cirugiaService.obtenerDeDinamica(fechaInicio, fechaFin));
    }

    @Operation(summary = "Importar cirugías desde Dinámica",
            description = "Importa cirugías desde datos de Dinámica Gerencial en un rango de fechas.",
            tags={"Cirugias"})
    @PostMapping("/importar")
    public ResponseEntity<ImportarCirugiasResponseDTO> importarDesdeDinamica(@Valid @RequestBody ImportarCirugiasRequestDTO request) {
        return ResponseEntity.ok(cirugiaService.importarDesdeDinamica(request));
    }

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

    @Operation(summary = "Listar todos las cirugias con paginación",
            description = "Obtiene la lista completa de todas las cirugias registradas en el sistema con soporte de paginación y filtro por rango de fechas, búsqueda, tipo o entidad.",
            tags={"Cirugias"})
    @GetMapping
    public ResponseEntity<PaginadoDTO<CirugiaResponseDTO>> listarTodos(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long entidadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("📥 GET /cirugias - fechaInicio: {}, fechaFin: {}, busqueda: {}, tipo: {}, entidadId: {}, page: {}, size: {}", fechaInicio, fechaFin, busqueda, tipo, entidadId, page, size);
        PaginadoDTO<CirugiaResponseDTO> lista = cirugiaService.listarTodosPageable(fechaInicio, fechaFin, busqueda, tipo, entidadId, page, size);
        log.info("✅ Retornando página con {} registros de {}", lista.getContenido().size(), lista.getTotalElementos());
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
            description = "Actualiza la información de una cirugia existente por su ID usando campos de texto.",
            tags={"Cirugias"})
    @PutMapping("/{id}")
    public ResponseEntity<CirugiaResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody CirugiaUpdateRequestDTO request
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
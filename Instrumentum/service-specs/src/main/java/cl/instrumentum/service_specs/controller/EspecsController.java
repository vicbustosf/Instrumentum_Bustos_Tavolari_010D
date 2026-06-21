package cl.instrumentum.service_specs.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_specs.model.EspecificacionElectronica;
import cl.instrumentum.service_specs.model.EspecificacionInstrumento;
import cl.instrumentum.service_specs.service.SpecsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/api/v2/especs")
@Tag(name = "Specs", description = "Endpoints para la gestión de especificación de equipos")
public class EspecsController {

    @Autowired
    private SpecsService specsService;

    // ==========================================
    // ===           INSTRUMENTOS             ===
    // ==========================================

    @GetMapping("/instrumento/{equipoId}")
    @Operation(summary = "Obtener especificación de instrumento por ID", description = "Recupera las especificaciones técnicas de un instrumento mediante el ID del equipo.")
    public ResponseEntity<Map<String, Object>> obtenerInstrumento(@PathVariable Long equipoId) {
        return specsService.obtenerInstrumentoPorId(equipoId)
                .map(i -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("mensaje", "Especificación de instrumento encontrada correctamente.");
                    response.put("especificacion", i);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "No existen especificaciones de instrumento para el equipo con ID " + equipoId + ".")));
    }

    @PostMapping("/instrumento/{equipoId}")
    @Operation(summary = "Crear especificación de instrumento", description = "Registra un nuevo set de especificaciones físicas para un instrumento asociado a un equipo existente.")
    public ResponseEntity<Map<String, Object>> crearInstrumento(
            @PathVariable Long equipoId,
            @Valid @RequestBody EspecificacionInstrumento specs) {

        if (specsService.obtenerInstrumentoPorId(equipoId).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "Ya existen especificaciones de instrumento para el equipo " + equipoId + ". Usa PUT."));
        }

        // SEGURIDAD: Sincronizar URL con Body
        specs.setIdEquipo(equipoId);
        
        EspecificacionInstrumento nueva = specsService.guardarInstrumento(equipoId, specs);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Especificación de instrumento creada correctamente.");
        response.put("especificacion", nueva);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/instrumento/{equipoId}")
    @Operation(summary = "Actualizar especificación de instrumento", description = "Modifica los datos técnicos de un instrumento musical existente basándose en su ID de equipo.")
    public ResponseEntity<Map<String, Object>> actualizarInstrumento(
            @PathVariable Long equipoId, 
            @Valid @RequestBody EspecificacionInstrumento ins) {
        
        return specsService.obtenerInstrumentoPorId(equipoId)
                .map(existente -> {
                    // SEGURIDAD: Sincronizar URL con Body
                    ins.setIdEquipo(equipoId);
                    
                    EspecificacionInstrumento actualizada = specsService.guardarInstrumento(equipoId, ins);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("mensaje", "Especificación de instrumento actualizada correctamente.");
                    response.put("especificacion", actualizada);
                        return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "No existen especificaciones de instrumento para el equipo con ID " + equipoId + ".")));
    }

    // ==========================================
    // ===           ELECTRÓNICA              ===
    // ==========================================

    @GetMapping("/electronica/{equipoId}")
    @Operation(summary = "Obtener especificación electrónica", description = "Recupera los detalles de circuitos, consumo y voltaje de un equipo por medio de su ID.")
    public ResponseEntity<Map<String, Object>> obtenerElectronica(@PathVariable Long equipoId) {
        return specsService.obtenerElectronicaPorId(equipoId)
                .map(el -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("mensaje", "Especificación electrónica encontrada correctamente.");
                    response.put("especificacion", el);
                        return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "No existen especificaciones electrónicas para el equipo con ID " + equipoId + ".")));
    }

    @PostMapping("/electronica/{equipoId}")
    @Operation(summary = "Crear especificación electrónica", description = "Registra los parámetros electrónicos iniciales para un componente o equipo.")
    public ResponseEntity<Map<String, Object>> crearElectronica(
            @PathVariable Long equipoId, 
            @Valid @RequestBody EspecificacionElectronica el) {

        if (specsService.obtenerElectronicaPorId(equipoId).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensaje", "Ya existen especificaciones electrónicas para el equipo " + equipoId + ". Usa PUT."));
        }

        // SEGURIDAD: Sincronizar URL con Body
        el.setIdEquipo(equipoId);
        
        EspecificacionElectronica nueva = specsService.guardarElectronica(equipoId, el);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Especificación electrónica creada correctamente.");
        response.put("especificacion", nueva);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/electronica/{equipoId}")
    @Operation(summary = "Actualizar especificación electrónica", description = "Actualiza el circuito, voltaje o consumo de un equipo electrónico registrado.")
    public ResponseEntity<Map<String, Object>> actualizarElectronica(
            @PathVariable Long equipoId, 
            @Valid @RequestBody EspecificacionElectronica el) {
        
        return specsService.obtenerElectronicaPorId(equipoId)
                .map(existente -> {
                    // SEGURIDAD: Sincronizar URL con Body
                    el.setIdEquipo(equipoId);
                    
                    EspecificacionElectronica actualizada = specsService.guardarElectronica(equipoId, el);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("mensaje", "Especificación electrónica actualizada correctamente.");
                    response.put("especificacion", actualizada);
                        return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "No existen especificaciones electrónicas para el equipo con ID " + equipoId + ".")));
    }

    // ==========================================
    // ===        MÉTODOS AUXILIARES          ===
    // ==========================================

    @DeleteMapping("/equipo/{equipoId}")
    @Operation(summary = "Eliminar especificaciones por equipo", description = "Remueve tanto las especificaciones asociadas a un ID del equipo.")
    public ResponseEntity<Map<String, String>> eliminarPorEquipo(@PathVariable Long equipoId) {
        specsService.eliminarPorEquipoId(equipoId);
                return ResponseEntity.ok(Map.of("mensaje", "Especificaciones eliminadas correctamente para el equipo con ID " + equipoId + "."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado en el módulo de especificaciones.";
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno del servidor: " + msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        String errores = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("mensaje", "Error de validación: " + errores));
    }
}
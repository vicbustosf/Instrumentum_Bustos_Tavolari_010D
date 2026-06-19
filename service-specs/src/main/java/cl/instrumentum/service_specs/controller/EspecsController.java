package cl.instrumentum.service_specs.controller;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_specs.model.EspecificacionElectronica;
import cl.instrumentum.service_specs.model.EspecificacionInstrumento;
import cl.instrumentum.service_specs.service.SpecsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/especs")
public class EspecsController {

    @Autowired
    private SpecsService specsService;

    // 
    //           INSTRUMENTOS              
    // 

    @GetMapping("/instrumento/{equipoId}")
    public ResponseEntity<Map<String, Object>> obtenerInstrumento(@PathVariable Long equipoId) {
        return specsService.obtenerInstrumentoPorId(equipoId)
                .map(i -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Especificación de instrumento encontrada correctamente.", "especificacion", i)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existen especificaciones de instrumento para el equipo con ID " + equipoId + ".")));
    }

    //el simbolo "?" es para indicar que el cuerpo de la respuesta puede ser de cualquier tipo,
    @PostMapping("/electronica/{equipoId}")
    public ResponseEntity<Map<String, Object>> guardarElectronica(@PathVariable Long equipoId,
                                                       @Valid @RequestBody EspecificacionElectronica specs) {

        if (specsService.obtenerElectronicaPorId(equipoId).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("mensaje", "Ya existen especificaciones electrónicas para el equipo " + equipoId + "."));
        }

        EspecificacionElectronica nueva = specsService.guardarElectronica(equipoId, specs);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Especificación electrónica creada correctamente.", "especificacion", nueva));
    }

    @PutMapping("/instrumento/{equipoId}")
    public ResponseEntity<Map<String, Object>> actualizarInstrumento(
            @PathVariable Long equipoId, 
            @Valid @RequestBody EspecificacionInstrumento ins) {
        
        return specsService.obtenerInstrumentoPorId(equipoId)
                .map(existente -> {
                    EspecificacionInstrumento actualizada = specsService.guardarInstrumento(equipoId, ins);
                    return ResponseEntity.ok(
                            Map.<String, Object>of("mensaje", "Especificación de instrumento actualizada correctamente.", "especificacion", actualizada));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existen especificaciones de instrumento para el equipo con ID " + equipoId + ".")));
    }

    // ==========================================
    // ===           ELECTRÓNICA              ===

    @GetMapping("/electronica/{equipoId}")
    public ResponseEntity<Map<String, Object>> obtenerElectronica(@PathVariable Long equipoId) {
        return specsService.obtenerElectronicaPorId(equipoId)
                .map(el -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Especificación electrónica encontrada correctamente.", "especificacion", el)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existen especificaciones electrónicas para el equipo con ID " + equipoId + ".")));
    }

    @PostMapping("/electronica/{equipoId}")
    public ResponseEntity<Map<String, Object>> crearElectronica(
            @PathVariable Long equipoId, 
            @Valid @RequestBody EspecificacionElectronica el) {
        
        EspecificacionElectronica nueva = specsService.guardarElectronica(equipoId, el);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Especificación electrónica creada correctamente.", "especificacion", nueva));
    }

    @PutMapping("/electronica/{equipoId}")
    public ResponseEntity<Map<String, Object>> actualizarElectronica(
            @PathVariable Long equipoId, 
            @Valid @RequestBody EspecificacionElectronica el) {
        
        return specsService.obtenerElectronicaPorId(equipoId)
                .map(existente -> {
                    EspecificacionElectronica actualizada = specsService.guardarElectronica(equipoId, el);
                    return ResponseEntity.ok(
                            Map.<String, Object>of("mensaje", "Especificación electrónica actualizada correctamente.", "especificacion", actualizada));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existen especificaciones electrónicas para el equipo con ID " + equipoId + ".")));
    }

    // ==========================================
    // ===        MÉTODOS AUXILIARES          ===
    

    @DeleteMapping("/equipo/{equipoId}")
    public ResponseEntity<Map<String, String>> eliminarPorEquipo(@PathVariable Long equipoId) {
        specsService.eliminarPorEquipoId(equipoId);
        return ResponseEntity.ok(Map.of("mensaje", "Especificaciones eliminadas correctamente para el equipo con ID " + equipoId + "."));
    }

    // Interceptores automáticos de excepciones para atrapar fallos del WebClient y de Validaciones
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
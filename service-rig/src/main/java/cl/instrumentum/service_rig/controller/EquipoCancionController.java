package cl.instrumentum.service_rig.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_rig.model.EquipoCancion;
import cl.instrumentum.service_rig.service.RigService;

@RestController
@RequestMapping("/api/v2/canciones")
public class EquipoCancionController {

    @Autowired
    private RigService rigService;

    @PostMapping("/{cancionId}/equipos")
    public ResponseEntity<Map<String, Object>> asignarEquipo(
            @PathVariable Long cancionId,
            @RequestBody Map<String, Object> request) {
        
        return rigService.buscarCancionPorId(cancionId)
                .map(cancion -> {
                    Long equipoId = ((Number) request.get("equipoId")).longValue();
                    Integer posicion = (Integer) request.get("posicion");
                    String seteoPerillas = (String) request.get("seteoPerillas");
                    
                    EquipoCancion ec = rigService.asignarEquipo(cancion, equipoId, posicion, seteoPerillas);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(Map.<String, Object>of("mensaje", "Equipo asignado correctamente.", "asignacion", ec));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una canción con ID " + cancionId + ".")));
    }

    @PutMapping("/{cancionId}/equipos/{equipoId}")
    public ResponseEntity<Map<String, Object>> actualizarEquipo(
            @PathVariable Long cancionId,
            @PathVariable Long equipoId,
            @RequestBody Map<String, Object> request) {
        
        return rigService.buscarCancionPorId(cancionId)
                .flatMap(cancion -> rigService.buscarEquipoCancion(cancion, equipoId))
                .map(ec -> {
                    if (request.containsKey("posicion")) ec.setPosicion((Integer) request.get("posicion"));
                    if (request.containsKey("seteoPerillas")) ec.setSeteoPerillas((String) request.get("seteoPerillas"));
                    
                    EquipoCancion actualizada = rigService.guardarEquipoCancion(ec);
                    return ResponseEntity.ok(
                            Map.<String, Object>of("mensaje", "Asignación actualizada correctamente.", "asignacion", actualizada));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No se encontró la asignación del equipo en la canción indicada.")));
    }

    @DeleteMapping("/{cancionId}/equipos/{equipoId}")
    public ResponseEntity<Map<String, String>> removerEquipo(
            @PathVariable Long cancionId,
            @PathVariable Long equipoId) {
        
        return rigService.buscarCancionPorId(cancionId)
                .map(cancion -> {
                    boolean eliminado = rigService.removerEquipo(cancion, equipoId);
                    if (eliminado) {
                        return ResponseEntity.ok(Map.of("mensaje", "Equipo removido correctamente de la canción."));
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("mensaje", "El equipo no está asignado a esta canción."));
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "No existe una canción con ID " + cancionId + ".")));
    }

    @GetMapping("/{cancionId}/setup-completo")
    public ResponseEntity<Map<String, Object>> obtenerSetupCompleto(@PathVariable Long cancionId) {
        try {
            return ResponseEntity.ok(rigService.obtenerSetupCompleto(cancionId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.<String, Object>of("mensaje", "No existe una canción con ID " + cancionId + "."));
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado.";
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
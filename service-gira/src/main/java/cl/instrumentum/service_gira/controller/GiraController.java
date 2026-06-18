package cl.instrumentum.service_gira.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_gira.model.Gira;
import cl.instrumentum.service_gira.model.ParadaGira;
import cl.instrumentum.service_gira.service.GiraService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/giras")
public class GiraController {

    @Autowired
    private GiraService giraService;

    // ENDPOINTS DE GIRAS          
    

    @GetMapping
    public List<Gira> listarGiras() {
        return giraService.listarGiras();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerGiraPorId(@PathVariable Long id) {
        return giraService.buscarGiraPorId(id)
                .map(g -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Gira encontrada correctamente.", "gira", g)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una gira con ID " + id + ".")));
    }

    @GetMapping("/banda/{idBanda}")
    public List<Gira> listarGirasPorBanda(@PathVariable Long idBanda) {
        return giraService.listarPorBanda(idBanda);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearGira(@Valid @RequestBody Gira gira) {
        Gira nueva = giraService.guardarGira(gira);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Gira creada correctamente.", "gira", nueva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarGira(@PathVariable Long id, @Valid @RequestBody Gira gira) {
        return Optional.ofNullable(giraService.actualizarGira(id, gira))
                .map(g -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Gira actualizada correctamente.", "gira", g)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una gira con ID " + id + " o no se pudo actualizar.")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarGira(@PathVariable Long id) {
        boolean eliminado = giraService.eliminarGira(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Gira " + id + " y todas sus paradas logísticas fueron eliminadas correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No existe una gira con ID " + id + "."));
        }
    }

    
    // ENDPOINTS DE PARADAS        

    @GetMapping("/paradas/gira/{idGira}")
    public List<ParadaGira> listarParadasPorGira(@PathVariable Long idGira) {
        return giraService.listarParadasPorGira(idGira);
    }

    @GetMapping("/paradas/{id}")
    public ResponseEntity<Map<String, Object>> obtenerParadaPorId(@PathVariable Long id) {
        return giraService.buscarParadaPorId(id)
                .map(p -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Parada logística encontrada correctamente.", "parada", p)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una parada logística con ID " + id + ".")));
    }

    @PostMapping("/paradas")
    public ResponseEntity<Map<String, Object>> crearParada(@Valid @RequestBody ParadaGira parada) {
        ParadaGira nueva = giraService.guardarParada(parada);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Parada logística registrada correctamente en el itinerario.", "parada", nueva));
    }

    @PutMapping("/paradas/{id}")
    public ResponseEntity<Map<String, Object>> actualizarParada(@PathVariable Long id, @Valid @RequestBody ParadaGira parada) {
        return Optional.ofNullable(giraService.actualizarParada(id, parada))
                .map(p -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Parada logística actualizada correctamente.", "parada", p)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una parada logística con ID " + id + " o no se pudo actualizar.")));
    }

    @DeleteMapping("/paradas/{id}")
    public ResponseEntity<Map<String, String>> eliminarParada(@PathVariable Long id) {
        boolean eliminado = giraService.eliminarParada(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Parada logística " + id + " eliminada correctamente del itinerario."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No existe una parada logística con ID " + id + "."));
        }
    }

    //      ESCUDO CAPTURADOR DE ERRORES   

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado en el módulo de giras.";
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
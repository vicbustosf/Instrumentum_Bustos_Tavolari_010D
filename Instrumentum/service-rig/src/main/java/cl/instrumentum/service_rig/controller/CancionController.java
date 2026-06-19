package cl.instrumentum.service_rig.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_rig.model.Cancion;
import cl.instrumentum.service_rig.service.RigService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/canciones")
public class CancionController {

    @Autowired
    private RigService rigService;

    @GetMapping("/banda/{bandaId}")
    public List<Cancion> listarPorBanda(@PathVariable Long bandaId) {
        return rigService.listarCancionesPorBanda(bandaId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        return rigService.buscarCancionPorId(id)
                .map(c -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Canción encontrada correctamente.", "cancion", c)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una canción con ID " + id + ".")));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Cancion cancion) {
        Cancion nueva = rigService.guardarCancion(cancion);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Canción creada correctamente.", "cancion", nueva));
    }

   @PutMapping("/{id}")
   public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @Valid @RequestBody Cancion datos) { // Se añade @Valid aquí
        return rigService.buscarCancionPorId(id)
                .map(c -> {
                    c.setNombre(datos.getNombre());
                    c.setDuracionSegundos(datos.getDuracionSegundos());
                    c.setBandaId(datos.getBandaId());
                    Cancion actualizada = rigService.guardarCancion(c);
                    return ResponseEntity.ok(
                            Map.<String, Object>of("mensaje", "Canción actualizada correctamente.", "cancion", actualizada));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una canción con ID " + id + ".")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        boolean eliminado = rigService.eliminarCancion(id);
        
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Canción " + id + " eliminada correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No existe una canción con ID " + id + "."));
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
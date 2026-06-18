package cl.instrumentum.service_usuario.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_usuario.model.Banda;
import cl.instrumentum.service_usuario.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/bandas")
public class BandaController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Banda> listar() {
        return usuarioService.listarBandas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        return usuarioService.buscarBandaPorId(id)
                .map(b -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Banda encontrada correctamente.", "banda", b)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una banda con ID " + id + ".")));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Banda banda) {
        Banda nueva = usuarioService.guardarBanda(banda);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Banda creada correctamente.", "banda", nueva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @RequestBody Banda datos) {
        return usuarioService.buscarBandaPorId(id)
                .map(b -> {
                    b.setNombre(datos.getNombre());
                    b.setFechaRegistro(datos.getFechaRegistro());
                    Banda actualizada = usuarioService.guardarBanda(b);
                    return ResponseEntity.ok(
                            Map.<String, Object>of("mensaje", "Banda actualizada correctamente.", "banda", actualizada));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una banda con ID " + id + ".")));
    }

    // MODIFICADO: Manejo usando el boolean del Service igual q en usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        boolean eliminado = usuarioService.eliminarBanda(id);
        
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Banda " + id + " eliminada correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No existe una banda con ID " + id + "."));
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
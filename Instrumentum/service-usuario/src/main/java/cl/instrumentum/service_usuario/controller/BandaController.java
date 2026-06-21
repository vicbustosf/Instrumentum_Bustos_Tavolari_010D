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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/api/v2/bandas")
@Tag(name = "Bandas", description = "Endpoints para la gestión de bandas musicales")
public class BandaController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todas las bandas", description = "Retorna una lista completa de las bandas registradas")
    public List<Banda> listar() {
        return usuarioService.listarBandas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar banda por ID", description = "Retorna la información de una banda específica mediante su ID")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        return usuarioService.buscarBandaPorId(id)
                .map(b -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Banda encontrada correctamente.", "banda", b)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una banda con ID " + id + ".")));
    }

    @PostMapping
    @Operation(summary = "Crear nueva banda", description = "Registra una nueva banda en el sistema")
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Banda banda) {
        Banda nueva = usuarioService.guardarBanda(banda);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Banda creada correctamente.", "banda", nueva));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar banda existente", description = "Modifica los datos de una banda mediante su ID")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody Banda banda) { // Escudo de validación activo
        
        return usuarioService.buscarBandaPorId(id)
                .map(existente -> {
                    banda.setIdBanda(id); // Respetamos el ID de la URL
                    Banda actualizada = usuarioService.guardarBanda(banda);
                    return ResponseEntity.ok(
                            Map.<String, Object>of("mensaje", "Banda actualizada correctamente.", "banda", actualizada));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una banda con ID " + id + ".")));
    }

    // MODIFICADO: Manejo usando el boolean del Service igual q en usuario
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una banda", description = "Elimina una banda del registro local y limpia en cascada sus dependencias")
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
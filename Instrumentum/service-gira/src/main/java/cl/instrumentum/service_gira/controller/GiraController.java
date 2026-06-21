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
import cl.instrumentum.service_gira.service.GiraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/api/v2/giras")
@Tag(name = "Giras", description = "Endpoints para la gestión del ciclo de vida y consultas de Giras musicales")
public class GiraController {

    @Autowired
    private GiraService giraService;

    // ENDPOINTS DE GIRAS          

    @GetMapping
    @Operation(summary = "Listar todas las giras", description = "Recupera un listado completo con todas las giras registradas en el sistema.")
    public List<Gira> listarGiras() {
        return giraService.listarGiras();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar gira por ID", description = "Obtiene los detalles de una gira específica utilizando su identificador único.")
    public ResponseEntity<Map<String, Object>> obtenerGiraPorId(@PathVariable Long id) {
        return giraService.buscarGiraPorId(id)
                .map(g -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Gira encontrada correctamente.", "gira", g)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una gira con ID " + id + ".")));
    }

    @GetMapping("/banda/{idBanda}")
    @Operation(summary = "Listar giras por banda", description = "Recupera todas las giras asociadas a una banda musical específica filtrando por su ID.")
    public List<Gira> listarGirasPorBanda(@PathVariable Long idBanda) {
        return giraService.listarPorBanda(idBanda);
    }

    @PostMapping
    @Operation(summary = "Crear una nueva gira", description = "Registra una gira en la base de datos tras validar lógicamente que la banda exista en el sistema externo.")
    public ResponseEntity<Map<String, Object>> crearGira(@Valid @RequestBody Gira gira) {
        Gira nueva = giraService.guardarGira(gira);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Gira creada correctamente.", "gira", nueva));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una gira existente", description = "Modifica los datos de una gira existente por su ID. Vuelve a validar la existencia de la banda de ser necesario.")
    public ResponseEntity<Map<String, Object>> actualizarGira(@PathVariable Long id, @Valid @RequestBody Gira gira) {
        return Optional.ofNullable(giraService.actualizarGira(id, gira))
                .map(g -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Gira actualizada correctamente.", "gira", g)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una gira con ID " + id + " o no se pudo actualizar.")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una gira", description = "Remueve físicamente de la base de datos una gira y todas sus paradas logísticas asociadas de forma segura.")
    public ResponseEntity<Map<String, String>> eliminarGira(@PathVariable Long id) {
        boolean eliminado = giraService.eliminarGira(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Gira " + id + " y todas sus paradas logísticas fueron eliminadas correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No existe una gira con ID " + id + "."));
        }
    }

    //      ESCUDO CAPTURADOR DE ERRORES   

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ?
                e.getMessage() : "Ocurrió un error inesperado en el módulo de giras.";
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
package cl.instrumentum.service_inventario.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_inventario.model.Categoria;
import cl.instrumentum.service_inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/categorias")
@Tag(name = "Controlador de Categorías", description = "Endpoints para la parametrización de categorías del inventario")
public class CategoriaController {

    @Autowired    
    private InventarioService inventarioService;

    @PostMapping
    @Operation(summary = "Crear una nueva categoría", description = "Añade una categoría al sistema para agrupar equipos.")
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Categoria categoria) {
        Categoria nueva = inventarioService.guardarCategoria(categoria);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Categoría creada correctamente.", "categoria", nueva));
    }

    @GetMapping
    @Operation(summary = "Listar todas las categorías", description = "Retorna una lista completa con las categorías disponibles.")
    public List<Categoria> listar() {
        return inventarioService.listarCategorias();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una categoría por ID", description = "Recupera una categoría según su ID.")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        Categoria c = inventarioService.obtenerCategoriaPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe una categoría con ID " + id + "."));
        return ResponseEntity.ok(Map.of("mensaje", "Categoría encontrada correctamente.", "categoria", c));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una categoría", description = "Modifica los datos de una categoría basándose en su ID.")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @Valid @RequestBody Categoria datos) {
        Categoria c = inventarioService.obtenerCategoriaPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe una categoría con ID " + id + "."));
                
        c.setNombre(datos.getNombre());
        Categoria actualizada = inventarioService.guardarCategoria(c);
        return ResponseEntity.ok(Map.of("mensaje", "Categoría actualizada correctamente.", "categoria", actualizada));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una categoría", description = "Elimina de la base de datos una categoría que no contenga ningún equipo asociado.")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        inventarioService.eliminarCategoria(id);
        return ResponseEntity.ok(Map.of("mensaje", "Categoría " + id + " eliminada correctamente."));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno del servidor: " + msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        String errores = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", "Error de validación: " + errores));
    }
}
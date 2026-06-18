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

import cl.instrumentum.service_inventario.model.Marca;
import cl.instrumentum.service_inventario.service.InventarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/marcas")
public class MarcaController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Marca marca) {
        Marca nueva = inventarioService.guardarMarca(marca);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Marca creada correctamente.", "marca", nueva));
    }

    @GetMapping
    public List<Marca> listar() {
        return inventarioService.listarMarcas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        Marca m = inventarioService.obtenerMarcaPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe una marca con ID " + id + "."));
        return ResponseEntity.ok(Map.of("mensaje", "Marca encontrada correctamente.", "marca", m));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @Valid @RequestBody Marca datos) {
        Marca m = inventarioService.obtenerMarcaPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe una marca con ID " + id + "."));
                
        m.setNombre(datos.getNombre());
        Marca actualizada = inventarioService.guardarMarca(m);
        return ResponseEntity.ok(Map.of("mensaje", "Marca actualizada correctamente.", "marca", actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        inventarioService.eliminarMarca(id);
        return ResponseEntity.ok(Map.of("mensaje", "Marca " + id + " eliminada correctamente."));
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
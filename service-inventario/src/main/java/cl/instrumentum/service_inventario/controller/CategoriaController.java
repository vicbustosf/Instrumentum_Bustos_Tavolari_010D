package cl.instrumentum.service_inventario.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_inventario.model.Categoria;
import cl.instrumentum.service_inventario.service.InventarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/categorias")
public class CategoriaController {

@Autowired    
private InventarioService inventarioService;

@PostMapping
public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Categoria categoria) {

Categoria nueva = inventarioService.guardarCategoria(categoria);

        return ResponseEntity.status(HttpStatus.CREATED)
        .body(Map.of("mensaje", "Categoría creada correctamente.",
                "categoria", nueva));
    }

@GetMapping
public List<Categoria> listar() {
        return inventarioService.listarCategorias();
    }

@GetMapping("/{id}")
public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        return inventarioService.obtenerCategoriaPorId(id)
                .map(c -> ResponseEntity.ok(
                        Map.of(
                                "mensaje", "Categoría encontrada correctamente.",
                                "categoria", c
                        )))
                .orElse(ResponseEntity.status(404)
                        .body(Map.of(
                                "mensaje",
                                "No existe una categoría con ID " + id + "."
                        )));
    }

@PutMapping("/{id}")
public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @Valid @RequestBody Categoria datos) {
        return inventarioService.obtenerCategoriaPorId(id)
                .map(c -> {
                    c.setNombre(datos.getNombre());

                    Categoria actualizada =
                            inventarioService.guardarCategoria(c);

                    return ResponseEntity.ok(
                            Map.of(
                                    "mensaje",
                                    "Categoría actualizada correctamente.",
                                    "categoria",
                                    actualizada
                            ));
                })
                .orElse(ResponseEntity.status(404)
                        .body(Map.of(
                                "mensaje",
                                "No existe una categoría con ID " + id + "."
                        )));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(
            @PathVariable Long id) {

        Optional<Categoria> categoria =
                inventarioService.obtenerCategoriaPorId(id);

        if (categoria.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "mensaje",
                            "No existe una categoría con ID " + id + "."
                    ));
        }

        inventarioService.eliminarCategoria(id);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Categoría " + id + " eliminada correctamente."
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException e) {

        String msg = e.getMessage() != null
                ? e.getMessage()
                : "Ocurrió un error inesperado.";

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "mensaje",
                        "Error interno del servidor: " + msg
                ));
    }

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        String errores = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "mensaje",
                        "Error de validación: " + errores
                ));
    }
// El @ExepitonHandler está para manejar las excepciones que puedan ocurrir en el controlador, 
// como errores de validación o errores inesperados, 
// así se puede enviar una respuesta clara al user en caso de que algo salga mal.
}
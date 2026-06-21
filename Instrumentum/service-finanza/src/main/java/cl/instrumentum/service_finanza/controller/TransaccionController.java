package cl.instrumentum.service_finanza.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_finanza.model.Transaccion;
import cl.instrumentum.service_finanza.service.TransaccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/finanzas")
@Tag(name = "Controlador de Transacciones", description = "Endpoints para la gestión financiera de las bandas musicales")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @GetMapping
    @Operation(summary = "Listar todas las transacciones", description = "Obtiene un listado completo de todos los movimientos financieros registrados en el sistema.")
    public List<Transaccion> listar() {
        return transaccionService.listarTransacciones();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transacción por ID", description = "Retorna el detalle de una transacción específica utilizando su identificador único.")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        return transaccionService.buscarPorId(id) // Usando buscarPorId de tu Service
                .map(t -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Transacción encontrada correctamente.", "transaccion", t)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una transacción con ID " + id + ".")));
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva transacción", description = "Crea un registro financiero validando previamente la existencia de la banda mediante comunicación síncrona.")
    public ResponseEntity<Map<String, Object>> registrar(@Valid @RequestBody Transaccion transaccion) {
        // Usando guardarTransaccion de tu Service (el cual internamente valida la banda con WebClient)
        Transaccion nueva = transaccionService.guardarTransaccion(transaccion);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Transacción registrada correctamente.", "transaccion", nueva));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una transacción existente", description = "Modifica los datos de un registro financiero existente identificándolo por su ID.")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @Valid @RequestBody Transaccion transaccion) {
        // Dado que tu service devuelve la entidad o null, lo envolvemos en Optional.ofNullable para usar el patrón limpio
        return Optional.ofNullable(transaccionService.actualizarTransaccion(id, transaccion))
                .map(t -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Transacción actualizada correctamente.", "transaccion", t)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe una transacción con ID " + id + " o no se pudo actualizar.")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una transacción", description = "Elimina físicamente del sistema el registro financiero correspondiente al ID enviado.")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        boolean eliminado = transaccionService.eliminarTransaccion(id);
        if (eliminado) {
            return ResponseEntity.ok(Map.of("mensaje", "Transacción " + id + " eliminada correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No existe una transacción con ID " + id + "."));
        }
    }

    @GetMapping("/banda/{idBanda}")
    @Operation(summary = "Listar transacciones por banda", description = "Recupera todo el historial de movimientos financieros asociados a una banda específica.")
    public List<Transaccion> obtenerPorBanda(@PathVariable Long idBanda) {
        return transaccionService.obtenerPorBanda(idBanda);
    }

    // Interceptores de excepciones automáticos para WebClient / BD y Validaciones
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado en el módulo de finanzas.";
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
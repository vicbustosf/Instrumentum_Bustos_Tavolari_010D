package cl.instrumentum.service_evento.controller;

import cl.instrumentum.service_evento.model.Evento;
import cl.instrumentum.service_evento.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/eventos")
@Tag(name = "Controlador de Eventos", description = "Endpoints para la gestión integral de eventos musicales")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    @Operation(summary = "Listar todos los eventos", description = "Obtiene una lista con la totalidad de los eventos registrados en la base de datos.")
    public ResponseEntity<List<Evento>> listarEventos() {
        return ResponseEntity.ok(eventoService.listarEventos());
    }

    // CORRECCIÓN 6: antes devolvía ResponseEntity.notFound().build() — 404 sin cuerpo JSON
    @GetMapping("/{id}")
    @Operation(summary = "Buscar evento por ID", description = "Recupera los detalles de un evento específico utilizando su identificador único.")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        return eventoService.buscarPorId(id)
                .map(e -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Evento encontrado correctamente.", "evento", e)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe un evento con ID " + id + ".")));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo evento", description = "Registra un nuevo evento en el sistema posterior a la validación de la existencia de la banda asociada.")
    public ResponseEntity<Map<String, Object>> crearEvento(@Valid @RequestBody Evento evento) {
        Evento nuevo = eventoService.guardarEvento(evento);
        return ResponseEntity.status(201)
                .body(Map.of("mensaje", "Evento creado correctamente.", "evento", nuevo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un evento existente", description = "Actualiza los datos de un evento existente buscando por su ID. Vuelve a validar la existencia de la banda.")
    public ResponseEntity<Map<String, Object>> actualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody Evento evento) {

        Evento actualizado = eventoService.actualizarEvento(id, evento);

        if (actualizado == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("mensaje", "No existe un evento con ID " + id + "."));
        }

        return ResponseEntity.ok(
                Map.of("mensaje", "Evento actualizado correctamente.", "evento", actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un evento", description = "Remueve permanentemente un evento de la base de datos a través de su identificador único.")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        if (eventoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("mensaje", "No existe un evento con ID " + id + "."));
        }
        eventoService.eliminarEvento(id);
        return ResponseEntity.ok(Map.of("mensaje", "Evento " + id + " eliminado correctamente."));
    }

    @GetMapping("/banda/{idBanda}")
    @Operation(summary = "Obtener eventos por ID de Banda", description = "Filtra y recupera todos los eventos que pertenezcan a una banda musical específica.")
    public ResponseEntity<List<Evento>> obtenerPorBanda(@PathVariable Long idBanda) {
        return ResponseEntity.ok(eventoService.obtenerPorBanda(idBanda));
    }

    // CORRECCIÓN 7: captura errores de WebClient (banda no existe / servicio caído)
    // y los devuelve como JSON con "mensaje" en vez del error 500 por defecto de Spring
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
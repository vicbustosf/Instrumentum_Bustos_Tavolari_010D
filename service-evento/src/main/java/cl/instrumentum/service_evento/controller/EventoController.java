package cl.instrumentum.service_evento.controller;

import cl.instrumentum.service_evento.model.Evento;
import cl.instrumentum.service_evento.service.EventoService;
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
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<Evento>> listarEventos() {
        return ResponseEntity.ok(eventoService.listarEventos());
    }

    // CORRECCIÓN 6: antes devolvía ResponseEntity.notFound().build() — 404 sin cuerpo JSON
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        return eventoService.buscarPorId(id)
                .map(e -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Evento encontrado correctamente.", "evento", e)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe un evento con ID " + id + ".")));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearEvento(@Valid @RequestBody Evento evento) {
        Evento nuevo = eventoService.guardarEvento(evento);
        return ResponseEntity.status(201)
                .body(Map.of("mensaje", "Evento creado correctamente.", "evento", nuevo));
    }

    @PutMapping("/{id}")
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
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        if (eventoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("mensaje", "No existe un evento con ID " + id + "."));
        }
        eventoService.eliminarEvento(id);
        return ResponseEntity.ok(Map.of("mensaje", "Evento " + id + " eliminado correctamente."));
    }

    @GetMapping("/banda/{idBanda}")
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
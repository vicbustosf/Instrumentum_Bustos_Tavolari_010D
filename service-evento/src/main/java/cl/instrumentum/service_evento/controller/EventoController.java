package cl.instrumentum.service_evento.controller;

import cl.instrumentum.service_evento.model.Evento;
import cl.instrumentum.service_evento.service.EventoService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<Evento>> listarEventos() {
        return ResponseEntity.ok(eventoService.listarEventos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerPorId(@PathVariable Long id) {
        Optional<Evento> evento = eventoService.buscarPorId(id);
        return evento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearEvento(
            @Valid @RequestBody Evento evento) {

        Evento nuevo = eventoService.guardarEvento(evento);

        return ResponseEntity.status(201)
                .body(Map.of(
                        "mensaje", "Evento creado correctamente.",
                        "evento", nuevo
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody Evento evento) {

        Evento actualizado = eventoService.actualizarEvento(id, evento);

        if (actualizado == null) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "mensaje",
                            "No existe un evento con ID " + id
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Evento actualizado correctamente.",
                        "evento",
                        actualizado
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(
            @PathVariable Long id) {

        Optional<Evento> evento = eventoService.buscarPorId(id);

        if (evento.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "mensaje",
                            "No existe un evento con ID " + id
                    ));
        }

        eventoService.eliminarEvento(id);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Evento " + id + " eliminado correctamente."
                ));
    }

    @GetMapping("/banda/{idBanda}")
    public ResponseEntity<List<Evento>> obtenerPorBanda(
            @PathVariable Long idBanda) {

        return ResponseEntity.ok(
                eventoService.obtenerPorBanda(idBanda));
    }

    
}
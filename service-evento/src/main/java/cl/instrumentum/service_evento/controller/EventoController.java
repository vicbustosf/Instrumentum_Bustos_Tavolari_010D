package cl.instrumentum.service_evento.controller;

import cl.instrumentum.service_evento.model.Evento;
import cl.instrumentum.service_evento.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/eventos")
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
    public ResponseEntity<Evento> crearEvento(@Valid @RequestBody Evento evento) {
        return ResponseEntity.ok(eventoService.guardarEvento(evento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody Evento evento) {

        Evento actualizado = eventoService.actualizarEvento(id, evento);

        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEvento(@PathVariable Long id) {

        if (eventoService.eliminarEvento(id)) {
            return ResponseEntity.ok(
                    Map.of("mensaje", "Evento eliminado correctamente","idEvento", id)
            );
        }
        return ResponseEntity.status(404).body(Map.of(
                        "mensaje", "No se encontró el evento con ID " + id
                ));
    }

    @GetMapping("/banda/{idBanda}")
    public ResponseEntity<List<Evento>> obtenerPorBanda(
            @PathVariable Long idBanda) {

        return ResponseEntity.ok(
                eventoService.obtenerPorBanda(idBanda));
    }

    @GetMapping("/banda/{idBanda}/fechas")
    public ResponseEntity<List<Evento>> obtenerPorBandaYFechas(
            @PathVariable Long idBanda,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        return ResponseEntity.ok(
                eventoService.obtenerPorBandaYFechas(idBanda, desde, hasta));
    }

    @GetMapping("/banda/{idBanda}/cancion/{idCancion}")
    public ResponseEntity<List<Evento>> obtenerPorBandaYCancion(
            @PathVariable Long idBanda,
            @PathVariable String idCancion) {

        return ResponseEntity.ok(
                eventoService.obtenerPorBandaYCancion(idBanda, idCancion));
    }
}
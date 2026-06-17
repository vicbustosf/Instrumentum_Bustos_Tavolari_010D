package cl.instrumentum.service_finanza.controller;

import cl.instrumentum.service_finanza.model.Transaccion;
import cl.instrumentum.service_finanza.service.TransaccionService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @GetMapping
    public ResponseEntity<List<Transaccion>> listarTransacciones() {
        return ResponseEntity.ok(transaccionService.listarTransacciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaccion> obtenerPorId(@PathVariable Long id) {
        Optional<Transaccion> transaccion = transaccionService.buscarPorId(id);

        return transaccion.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearTransaccion(
            @Valid @RequestBody Transaccion transaccion) {

        Transaccion nueva = transaccionService.guardarTransaccion(transaccion);

        return ResponseEntity.status(201)
                .body(Map.of(
                        "mensaje", "Transacción creada correctamente.",
                        "transaccion", nueva
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarTransaccion(
            @PathVariable Long id,
            @Valid @RequestBody Transaccion transaccion) {

        Transaccion actualizada =
                transaccionService.actualizarTransaccion(id, transaccion);

        if (actualizada == null) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "mensaje",
                            "No existe una transacción con ID " + id
                    ));
        }

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Transacción actualizada correctamente.",
                        "transaccion",
                        actualizada
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(
            @PathVariable Long id) {

        Optional<Transaccion> transaccion =
                transaccionService.buscarPorId(id);

        if (transaccion.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "mensaje",
                            "No existe una transacción con ID " + id
                    ));
        }

        transaccionService.eliminarTransaccion(id);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Transacción " + id + " eliminada correctamente."
                ));
    }

    @GetMapping("/banda/{idBanda}")
    public ResponseEntity<List<Transaccion>> obtenerPorBanda(
            @PathVariable Long idBanda) {

        return ResponseEntity.ok(
                transaccionService.obtenerPorBanda(idBanda)
        );
    }
}
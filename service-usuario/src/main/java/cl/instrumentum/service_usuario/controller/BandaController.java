package cl.instrumentum.service_usuario.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_usuario.model.Banda;
import cl.instrumentum.service_usuario.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bandas")
public class BandaController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Banda> listar() {
        return usuarioService.listarBandas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Banda> obtener(@PathVariable Long id) {
        return usuarioService.buscarBandaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Banda> actualizar(@PathVariable Long id, @RequestBody Banda datos) {
        return usuarioService.buscarBandaPorId(id)
                .map(b -> {
                    b.setNombre(datos.getNombre());
                    b.setFechaRegistro(datos.getFechaRegistro());
                    return ResponseEntity.ok(usuarioService.guardarBanda(b));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Banda> crear(@Valid @RequestBody Banda banda) {
        return ResponseEntity.ok(usuarioService.guardarBanda(banda));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        Optional<Banda> banda = usuarioService.buscarBandaPorId(id);

        if (banda.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("mensaje","No existe una banda con ID " + id));
        }
        usuarioService.eliminarBanda(id);
        return ResponseEntity.ok(
                Map.of("mensaje","Banda " + id + " eliminada correctamente."));
    }
}
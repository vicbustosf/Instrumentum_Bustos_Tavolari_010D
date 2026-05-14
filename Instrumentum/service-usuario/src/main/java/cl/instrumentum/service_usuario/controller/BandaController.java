package cl.instrumentum.service_usuario.controller;

import java.util.List;
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

    @PostMapping
    public ResponseEntity<Banda> crear(@Valid @RequestBody Banda banda) {
        return ResponseEntity.ok(usuarioService.guardarBanda(banda));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarBanda(id);
        return ResponseEntity.noContent().build();
    }
}
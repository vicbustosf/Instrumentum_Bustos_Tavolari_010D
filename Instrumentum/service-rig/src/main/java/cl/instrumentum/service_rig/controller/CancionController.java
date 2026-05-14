package cl.instrumentum.service_rig.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_rig.model.Cancion;
import cl.instrumentum.service_rig.service.RigService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/canciones")
public class CancionController {

    @Autowired
    private RigService rigService;

    @PostMapping
    public ResponseEntity<Cancion> crearCancion(@Valid @RequestBody Cancion cancion) {
        Cancion nueva = rigService.crearCancion(cancion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/banda/{bandaId}")
    public List<Cancion> listarPorBanda(@PathVariable Long bandaId) {
        return rigService.listarCancionesPorBanda(bandaId);
    }

    @DeleteMapping("/{cancionId}")
    public ResponseEntity<Void> eliminarCancion(@PathVariable Long cancionId) {
        rigService.eliminarCancion(cancionId);
        return ResponseEntity.noContent().build();
    }
}
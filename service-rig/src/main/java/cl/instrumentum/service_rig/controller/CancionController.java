package cl.instrumentum.service_rig.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_rig.model.Cancion;
import cl.instrumentum.service_rig.service.RigService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/canciones")
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

    @PutMapping("/{id}")
    public ResponseEntity<Cancion> actualizar(@PathVariable Long id, @RequestBody Cancion datos) {
        return rigService.buscarCancionPorId(id)
                .map(c -> {
                    c.setNombre(datos.getNombre());
                    c.setDuracionSegundos(datos.getDuracionSegundos());
                    return ResponseEntity.ok(rigService.actualizarCancion(c));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{cancionId}")
public ResponseEntity<Map<String, String>> eliminarCancion(
        @PathVariable Long cancionId) {

    Optional<Cancion> cancion = rigService.buscarCancionPorId(cancionId);

    if (cancion.isEmpty()) {
        return ResponseEntity.status(404)
                .body(Map.of("mensaje","No existe una canción con ID " + cancionId));
    }

    rigService.eliminarCancion(cancionId);

    return ResponseEntity.ok(
            Map.of( "mensaje","Canción " + cancionId + " eliminada correctamente."));
    }
}
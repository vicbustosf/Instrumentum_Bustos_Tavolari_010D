package cl.instrumentum.service_rig.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_rig.model.Cancion;
import cl.instrumentum.service_rig.model.EquipoCancion;
import cl.instrumentum.service_rig.service.RigService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/canciones")
public class EquipoCancionController {

    @Autowired
    private RigService rigService;


    //La funcion de este post es asignar
    //  un equipo a una canción, con su posición y seteo de perillas.
    @PostMapping("/{cancionId}/equipos")
    public ResponseEntity<EquipoCancion> asignarEquipo(
            @PathVariable Long cancionId,
            @Valid @RequestBody Map<String, Object> request) {
        Long equipoId = ((Number) request.get("equipoId")).longValue();
        Integer posicion = (Integer) request.get("posicion");
        String seteoPerillas = (String) request.get("seteoPerillas");
        EquipoCancion ec = rigService.asignarEquipo(cancionId, equipoId, posicion, seteoPerillas);
        return ResponseEntity.status(HttpStatus.CREATED).body(ec);
    }

    @PutMapping("/{cancionId}/equipos/{equipoId}")
    public ResponseEntity<EquipoCancion> actualizarEquipo(
            @PathVariable Long cancionId,
            @PathVariable Long equipoId,
            @Valid @RequestBody Map<String, Object> request) {
        Integer posicion = (Integer) request.get("posicion");
        String seteoPerillas = (String) request.get("seteoPerillas");
        EquipoCancion ec = rigService.actualizarEquipo(cancionId, equipoId, posicion, seteoPerillas);
        return ResponseEntity.ok(ec);
    }

    @DeleteMapping("/{cancionId}/equipos/{equipoId}")
    public ResponseEntity<Map<String, String>> removerEquipo(
            @PathVariable Long cancionId,
            @PathVariable Long equipoId) {

        Optional<Cancion> cancion = rigService.buscarCancionPorId(cancionId);

        if (cancion.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "mensaje",
                            "No existe una canción con ID " + cancionId
                    ));
        }
        try {
            rigService.removerEquipo(cancionId, equipoId);
            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Equipo " + equipoId +
                            " eliminado correctamente de la canción " +
                            cancionId + "."
        ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "mensaje",
                            "La canción " + cancionId +
                            " no tiene asignado el equipo " + equipoId
                        ));
        }
    }

    @GetMapping("/{cancionId}/setup-completo")
    public ResponseEntity<?> obtenerSetupCompleto(@PathVariable Long cancionId) {
        return ResponseEntity.ok(rigService.obtenerSetupCompleto(cancionId));
    }
}
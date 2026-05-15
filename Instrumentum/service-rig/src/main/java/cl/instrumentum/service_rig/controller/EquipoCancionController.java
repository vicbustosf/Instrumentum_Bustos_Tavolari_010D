package cl.instrumentum.service_rig.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_rig.model.EquipoCancion;
import cl.instrumentum.service_rig.service.RigService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/canciones")
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
    public ResponseEntity<Void> removerEquipo(
            @PathVariable Long cancionId,
            @PathVariable Long equipoId) {
        rigService.removerEquipo(cancionId, equipoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{cancionId}/setup-completo")
    public ResponseEntity<?> obtenerSetupCompleto(@PathVariable Long cancionId) {
        return ResponseEntity.ok(rigService.obtenerSetupCompleto(cancionId));
    }
}
package cl.instrumentum.service_specs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_specs.model.EspecificacionElectronica;
import cl.instrumentum.service_specs.model.EspecificacionInstrumento;
import cl.instrumentum.service_specs.service.SpecsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/especs")
public class EspecsController {

    @Autowired
    private SpecsService specsService;

    @PostMapping("/instrumento/{equipoId}")
    public ResponseEntity<EspecificacionInstrumento> crearInstrumento(
            @PathVariable Long equipoId,
            @Valid @RequestBody EspecificacionInstrumento espec) {
        EspecificacionInstrumento guardada = specsService.guardarInstrumento(equipoId, espec);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PostMapping("/electronica/{equipoId}")
    public ResponseEntity<EspecificacionElectronica> crearElectronica(
            @PathVariable Long equipoId,
            @Valid @RequestBody EspecificacionElectronica espec) {
        EspecificacionElectronica guardada = specsService.guardarElectronica(equipoId, espec);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<?> obtenerPorEquipo(@PathVariable Long equipoId) {
        Object espec = specsService.obtenerEspecificacionPorEquipo(equipoId);
        if (espec == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(espec);
    }

    @DeleteMapping("/equipo/{equipoId}")
    public ResponseEntity<Void> eliminarPorEquipo(@PathVariable Long equipoId) {
        specsService.eliminarPorEquipoId(equipoId);
        return ResponseEntity.noContent().build();
    }
}
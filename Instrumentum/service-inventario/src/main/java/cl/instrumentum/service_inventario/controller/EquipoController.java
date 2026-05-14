package cl.instrumentum.service_inventario.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_inventario.model.Equipo;
import cl.instrumentum.service_inventario.service.InventarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/equipos")
public class EquipoController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<Equipo> crear(@Valid @RequestBody Equipo equipo) {
        Equipo nuevo = inventarioService.guardarEquipo(equipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping
    public ResponseEntity<List<Equipo>> listarPorPropietario(@RequestParam Long propietarioId) {
        return ResponseEntity.ok(inventarioService.listarEquiposPorPropietario(propietarioId));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Equipo>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(inventarioService.buscarEquipos(nombre, marca, categoria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtener(@PathVariable Long id) {
        return inventarioService.obtenerEquipoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipo> actualizar(@PathVariable Long id, @Valid @RequestBody Equipo equipoActualizado) {
        return inventarioService.obtenerEquipoPorId(id)
                .map(equipo -> {
                    equipo.setNombre(equipoActualizado.getNombre());
                    equipo.setModelo(equipoActualizado.getModelo());
                    equipo.setMarca(equipoActualizado.getMarca());
                    equipo.setCategoria(equipoActualizado.getCategoria());
                    equipo.setPropietarioId(equipoActualizado.getPropietarioId());
                    equipo.setTipoPropietario(equipoActualizado.getTipoPropietario());
                    equipo.setTipoEquipo(equipoActualizado.getTipoEquipo());
                    return ResponseEntity.ok(inventarioService.guardarEquipo(equipo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminarEquipo(id);
        return ResponseEntity.noContent().build();
    }
}
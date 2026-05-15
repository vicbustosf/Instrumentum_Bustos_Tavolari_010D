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
    public ResponseEntity<List<Equipo>> listarTodos() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }

    @GetMapping("/propietario/{propietarioId}")
    public ResponseEntity<List<Equipo>> listarPorPropietario(@PathVariable Long propietarioId) {
        return ResponseEntity.ok(inventarioService.listarEquiposPorPropietario(propietarioId));
    }

    @GetMapping("/buscar/{nombre}/{marca}/{categoria}")
    public ResponseEntity<List<Equipo>> buscar(
        @PathVariable String nombre,
        @PathVariable String marca,
        @PathVariable String categoria) {
        String n = "_".equals(nombre) ? null : nombre; 
        String m = "_".equals(marca)  ? null : marca;
        String c = "_".equals(categoria) ? null : categoria;
        return ResponseEntity.ok(inventarioService.buscarEquipos(n, m, c));
//El simbolo "?" significa que el parámetro es opcional,
//  pero como estamos usando @PathVariable, no podemos omitirlo


        // Se usa "_" como comodín para indicar que no se quiere filtrar por ese campo,
        // ya que no se pueden usar query params con @PathVariable. 
        // Esto permite buscar por cualquier combinación de campos.
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
package cl.instrumentum.service_inventario.controller;
 
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_inventario.model.Equipo;
import cl.instrumentum.service_inventario.repository.EquipoRepository;
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
 
    // FIX: conflicto git resuelto. Se usa /todos para no colisionar con /{id}
    @GetMapping("/todos")
    public ResponseEntity<List<Equipo>> listarTodos() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }
 
    @GetMapping("/propietario/{propietarioId}")
    public ResponseEntity<List<Equipo>> listarPorPropietario(@PathVariable Long propietarioId) {
        return ResponseEntity.ok(inventarioService.listarEquiposPorPropietario(propietarioId));
    }
 
    // Se usa _ como comodín para campos opcionales ya que no se pueden omitir @PathVariable.
    // Ejemplo: /buscar/_/Gibson/_ devuelve todos los equipos de Gibson.
    @GetMapping("/buscar/{nombre}/{marca}/{categoria}")
    public ResponseEntity<List<Equipo>> buscar(
        @PathVariable String nombre,
        @PathVariable String marca,
        @PathVariable String categoria) {
        String n = "_".equals(nombre)    ? null : nombre;
        String m = "_".equals(marca)     ? null : marca;
        String c = "_".equals(categoria) ? null : categoria;
        return ResponseEntity.ok(inventarioService.buscarEquipos(n, m, c));
    }
 // ? significa que el campo es opcional. 
 // Si se omite, se pasa null al servicio para no filtrar por ese campo.

 
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
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {

        Optional<Equipo> equipo = inventarioService.obtenerEquipoPorId(id);

        if (equipo.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "mensaje","No existe un equipo con ID " + id));
        }
        inventarioService.eliminarEquipo(id);
        return ResponseEntity.ok(
                Map.of("mensaje","Equipo " + id + " eliminado correctamente."));
    }
}
package cl.instrumentum.service_inventario.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_inventario.model.Marca;
import cl.instrumentum.service_inventario.service.InventarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/marcas")
public class MarcaController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<Marca> crear(@Valid @RequestBody Marca marca) {
        return ResponseEntity.ok(inventarioService.guardarMarca(marca));
    }
    
    @GetMapping
    public List<Marca> listar() {
        return inventarioService.listarMarcas();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marca> actualizar(@PathVariable Long id, @RequestBody Marca datos) {
        return inventarioService.obtenerMarcaPorId(id)
                .map(m -> {
                    m.setNombre(datos.getNombre());
                    return ResponseEntity.ok(inventarioService.guardarMarca(m));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    

    @GetMapping("/{id}")
    public ResponseEntity<Marca> obtener(@PathVariable Long id) {
        return inventarioService.obtenerMarcaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {

        Optional<Marca> marca = inventarioService.obtenerMarcaPorId(id);

        if (marca.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("mensaje","No existe una marca con ID " + id));
        }
        inventarioService.eliminarMarca(id);

        return ResponseEntity.ok(
                Map.of( "mensaje","Marca " + id + " eliminada correctamente."));
    }

    
}
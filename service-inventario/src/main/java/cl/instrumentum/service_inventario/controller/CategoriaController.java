package cl.instrumentum.service_inventario.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_inventario.model.Categoria;
import cl.instrumentum.service_inventario.service.InventarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<Categoria> crear(@Valid @RequestBody Categoria categoria) {
        return ResponseEntity.ok(inventarioService.guardarCategoria(categoria));
    }

    @GetMapping
    public List<Categoria> listar() {
        return inventarioService.listarCategorias();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @RequestBody Categoria datos) {
        return inventarioService.obtenerCategoriaPorId(id)
                .map(c -> { 
                    c.setNombre(datos.getNombre());
                    return ResponseEntity.ok(inventarioService.guardarCategoria(c));
                })
                .orElse(ResponseEntity.notFound().build());
    }
//el simbolo -> se usa para indicar que lo que viene después es una función lambda, es decir,
//  una función anónima que se puede pasar como argumento a otro método.
//  En este caso, se está usando para mapear el resultado de obtenerCategoriaPorId(id) a una respuesta HTTP

//o sea, si se encuentra la categoría con el id dado, 
// se actualiza su nombre y se devuelve una respuesta 200 OK con la categoría actualizada.

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtener(@PathVariable Long id) {
        return inventarioService.obtenerCategoriaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
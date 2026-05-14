package cl.instrumentum.service_inventario.controller;

import java.util.List;
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
}
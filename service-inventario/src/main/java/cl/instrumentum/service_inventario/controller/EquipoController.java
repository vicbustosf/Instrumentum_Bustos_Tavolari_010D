package cl.instrumentum.service_inventario.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_inventario.model.Equipo;
import cl.instrumentum.service_inventario.service.InventarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/equipos")
public class EquipoController {

@Autowired
private InventarioService inventarioService;

@PostMapping
public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Equipo equipo) {
    Equipo nuevo = inventarioService.guardarEquipo(equipo);

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                    "mensaje", "Equipo creado correctamente.",
                    "equipo", nuevo
            ));
}

@GetMapping("/todos")
public List<Equipo> listarTodos() {
    return inventarioService.listarTodos();
}

@GetMapping("/propietario/{propietarioId}")
public List<Equipo> listarPorPropietario(@PathVariable Long propietarioId) {
    return inventarioService.listarEquiposPorPropietario(propietarioId);
}

@GetMapping("/buscar/{nombre}/{marca}/{categoria}")
public List<Equipo> buscar(
        @PathVariable String nombre,
        @PathVariable String marca,
        @PathVariable String categoria) {

    String n = "_".equals(nombre) ? null : nombre;
    String m = "_".equals(marca) ? null : marca;
    String c = "_".equals(categoria) ? null : categoria;

    return inventarioService.buscarEquipos(n, m, c);
}

@GetMapping("/{id}")
public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
    return inventarioService.obtenerEquipoPorId(id)
            .map(e -> ResponseEntity.ok(
                    Map.<String, Object>of(
                            "mensaje", "Equipo encontrado correctamente.",
                            "equipo", e)))
            .orElse(ResponseEntity.status(404)
                    .body(Map.<String, Object>of(
                            "mensaje", "No existe un equipo con ID " + id + ".")));
}

@PutMapping("/{id}")
public ResponseEntity<Map<String, Object>> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody Equipo datos) {

    return inventarioService.obtenerEquipoPorId(id)
            .map(e -> {
                e.setNombre(datos.getNombre());
                e.setModelo(datos.getModelo());
                e.setMarca(datos.getMarca());
                e.setCategoria(datos.getCategoria());
                e.setPropietarioId(datos.getPropietarioId());
                e.setTipoPropietario(datos.getTipoPropietario());
                e.setTipoEquipo(datos.getTipoEquipo());

                Equipo actualizado = inventarioService.guardarEquipo(e);

                return ResponseEntity.ok(
                        Map.<String, Object>of(
                                "mensaje", "Equipo actualizado correctamente.",
                                "equipo", actualizado));
            })
            .orElse(ResponseEntity.status(404)
                    .body(Map.<String, Object>of(
                            "mensaje", "No existe un equipo con ID " + id + ".")));
}

@DeleteMapping("/{id}")
public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {

    Optional<Equipo> equipo = inventarioService.obtenerEquipoPorId(id);

    if (equipo.isEmpty()) {
        return ResponseEntity.status(404)
                .body(Map.of(
                        "mensaje",
                        "No existe un equipo con ID " + id + "."));
    }

    inventarioService.eliminarEquipo(id);

    return ResponseEntity.ok(
            Map.of(
                    "mensaje",
                    "Equipo " + id + " eliminado correctamente."));
}

@ExceptionHandler(RuntimeException.class)
public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
    String msg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado.";

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                    "mensaje",
                    "Error interno del servidor: " + msg));
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
    String errores = e.getBindingResult().getFieldErrors()
            .stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.joining(", "));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                    "mensaje",
                    "Error de validación: " + errores));
}


}

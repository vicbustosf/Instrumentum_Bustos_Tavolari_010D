package cl.instrumentum.service_inventario.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
                .body(Map.of("mensaje", "Equipo creado correctamente.", "equipo", nuevo));
    }

    @GetMapping
    public List<Equipo> listar() {
        return inventarioService.listarTodos(); // o el nombre exacto que tenga en tu Service
    }

    @GetMapping("/propietario/{propietarioId}")
    public List<Equipo> listarPorPropietario(@PathVariable Long propietarioId) {
        return inventarioService.listarEquiposPorPropietario(propietarioId);
    }

    // GET: Buscar equipos con filtros opcionales por Query Params
   @GetMapping("/buscar")
    public ResponseEntity<List<Equipo>> buscarEquipos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String categoria) {

        String filtroNombre = (nombre == null || nombre.trim().isEmpty()) ? null : nombre;
        String filtroMarca = (marca == null || marca.trim().isEmpty()) ? null : marca;
        String filtroCategoria = (categoria == null || categoria.trim().isEmpty()) ? null : categoria;

        List<Equipo> resultados = inventarioService.buscarEquipos(filtroNombre, filtroMarca, filtroCategoria);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
        Equipo e = inventarioService.obtenerEquipoPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un equipo con ID " + id + "."));
        return ResponseEntity.ok(Map.of("mensaje", "Equipo encontrado correctamente.", "equipo", e));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @Valid @RequestBody Equipo datos) {
        Equipo e = inventarioService.obtenerEquipoPorId(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un equipo con ID " + id + "."));
                
        e.setNombre(datos.getNombre());
        e.setModelo(datos.getModelo());
        e.setMarca(datos.getMarca());
        e.setCategoria(datos.getCategoria());
        e.setPropietarioId(datos.getPropietarioId());
        e.setTipoPropietario(datos.getTipoPropietario());
        e.setTipoEquipo(datos.getTipoEquipo());

        Equipo actualizado = inventarioService.guardarEquipo(e);
        return ResponseEntity.ok(Map.of("mensaje", "Equipo actualizado correctamente.", "equipo", actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        inventarioService.eliminarEquipo(id);
        return ResponseEntity.ok(Map.of("mensaje", "Equipo " + id + " eliminado correctamente y dependencias procesadas."));
    }

    // INTERCEPTORES DE EXCEPCIONES NATIVAS (RESPUESTAS JSON PARA POSTMAN)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno del servidor: " + msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        String errores = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", "Error de validación: " + errores));
    }
}
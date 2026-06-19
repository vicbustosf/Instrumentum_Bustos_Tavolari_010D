package cl.instrumentum.service_mantenimiento.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_mantenimiento.model.Mantenimiento;
import cl.instrumentum.service_mantenimiento.service.MantenimientoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/mantenimientos")
public class MantenimientoController {

    @Autowired
    private MantenimientoService mantenimientoService;

    // GET: Buscar un mantenimiento específico por su ID único
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        return mantenimientoService.buscarPorId(id) // Usando buscarPorId de tu Service
                .map(m -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Mantenimiento encontrado correctamente.", "mantenimiento", m)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe un mantenimiento con ID " + id + ".")));
    }

    // GET: Listar todos los mantenimientos asociados a un equipo específico
    @GetMapping("/equipo/{equipoId}")
    public List<Mantenimiento> listarPorEquipo(@PathVariable Long equipoId) {
        return mantenimientoService.listarPorEquipo(equipoId); // Usando tu método real
    }

    // POST: Registrar un mantenimiento (valida internamente que el equipo exista con WebClient)
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@Valid @RequestBody Mantenimiento m) {
        Mantenimiento guardado = mantenimientoService.registrarMantenimiento(m);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Mantenimiento registrado correctamente.", "mantenimiento", guardado));
    }

    // PUT: Actualizar un mantenimiento existente
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @Valid @RequestBody Mantenimiento m) {
        return mantenimientoService.buscarPorId(id)
                .map(existente -> {
                    // CORRECCIÓN: Evitamos que el registro se transfiera a otro equipo.
                    if (!existente.getEquipoId().equals(m.getEquipoId())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.<String, Object>of("mensaje", "Error: No puedes transferir un registro de mantenimiento a un equipo distinto."));
                    }
                    
                    // Seteamos el ID de la URI al objeto para asegurar que modifique el correcto
                    m.setId(id); 
                    Mantenimiento actualizado = mantenimientoService.registrarMantenimiento(m);
                    return ResponseEntity.ok(
                            Map.<String, Object>of("mensaje", "Mantenimiento actualizado correctamente.", "mantenimiento", actualizado));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe un mantenimiento con ID " + id + ".")));
    }

    // DELETE: Eliminar TODOS los mantenimientos asociados a un equipo
    @DeleteMapping("/equipo/{equipoId}")
    public ResponseEntity<Map<String, String>> eliminarPorEquipo(@PathVariable Long equipoId) {
        boolean eliminado = mantenimientoService.eliminarMantenimiento(equipoId);
        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No existen mantenimientos registrados para el equipo con ID " + equipoId + "."));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Mantenimientos eliminados correctamente para el equipo con ID " + equipoId + "."));
    }

    // DELETE: Eliminar un mantenimiento específico por su ID
    @DeleteMapping("/equipo/{equipoId}")
    public ResponseEntity<Map<String, String>> eliminarMantenimiento(@PathVariable Long equipoId) {
        boolean eliminado = mantenimientoService.eliminarMantenimiento(equipoId);
        
        // CORRECCIÓN: Si devuelve false, significa que el equipo no tenía mantenimientos registrados.
        // Respondemos con un 200 OK informativo en lugar de un 404 semánticamente confuso.
        if (!eliminado) {
            return ResponseEntity.ok(Map.of(
                "mensaje", "El equipo con ID " + equipoId + " no tenía mantenimientos registrados."
            ));
        }
        
        // Si devuelve true, se borraron exitosamente
        return ResponseEntity.ok(Map.of(
            "mensaje", "Mantenimientos eliminados para el equipo con ID " + equipoId
        ));
    }


    // GET: Verificar si un equipo requiere mantenimiento preventivo
    @GetMapping("/equipo/{equipoId}/requiere")
    public boolean requiereMantenimiento(@PathVariable Long equipoId) {
        return mantenimientoService.requiereMantenimiento(equipoId);
    }

    // Interceptores automáticos de excepciones para WebClient y Validaciones
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado en el módulo de mantenimientos.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno del servidor: " + msg));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        String errores = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", "Error de validación: " + errores));
    }
}
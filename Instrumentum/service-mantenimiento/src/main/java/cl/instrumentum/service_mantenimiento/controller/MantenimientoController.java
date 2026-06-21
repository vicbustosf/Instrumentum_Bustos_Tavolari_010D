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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/mantenimientos")
@Tag(name = "Controlador de Mantenimientos", description = "Endpoints para la gestión y consulta de mantenimientos de equipos")
public class MantenimientoController {

    @Autowired
    private MantenimientoService mantenimientoService;

    // GET: Buscar un mantenimiento específico por su ID único
    @GetMapping("/{id}")
    @Operation(summary = "Buscar mantenimiento por ID", description = "Obtiene los detalles de un registro de mantenimiento específico utilizando su ID único.")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Long id) {
        return mantenimientoService.buscarPorId(id) // Usando buscarPorId de tu Service
                .map(m -> ResponseEntity.ok(
                        Map.<String, Object>of("mensaje", "Mantenimiento encontrado correctamente.", "mantenimiento", m)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("mensaje", "No existe un mantenimiento con ID " + id + ".")));
    }

    // GET: Listar todos los mantenimientos asociados a un equipo específico
    @GetMapping("/equipo/{equipoId}")
    @Operation(summary = "Listar mantenimientos por equipo", description = "Retorna una lista con todos los mantenimientos asociados a un ID de equipo específico ordenados por fecha.")
    public List<Mantenimiento> listarPorEquipo(@PathVariable Long equipoId) {
        return mantenimientoService.listarPorEquipo(equipoId); // Usando tu método real
    }

    // POST: Registrar un mantenimiento (valida internamente que el equipo exista con WebClient)
    @PostMapping
    @Operation(summary = "Registrar mantenimiento", description = "Crea un nuevo registro de mantenimiento validando previamente la existencia del equipo.")
    public ResponseEntity<Map<String, Object>> registrar(@Valid @RequestBody Mantenimiento m) {
        Mantenimiento guardado = mantenimientoService.registrarMantenimiento(m);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.<String, Object>of("mensaje", "Mantenimiento registrado correctamente.", "mantenimiento", guardado));
    }

    // PUT: Actualizar un mantenimiento existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mantenimiento existente", description = "Modifica los datos de un mantenimiento existente sin permitir la transferencia del registro a otro equipo.")
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

    
    //DELETE: Eliminar TODOS los mantenimientos asociados a un equipo
    @DeleteMapping("/equipo/{equipoId}")
    @Operation(summary = "Eliminar mantenimientos por equipo", description = "Remueve de manera lógica o física todos los mantenimientos asociados a un equipo específico.")
    public ResponseEntity<Map<String, String>> eliminarMantenimientoPorEquipo(@PathVariable Long equipoId) {
        // Llama al método del service que borra todos los registros de un equipo
        boolean eliminado = mantenimientoService.eliminarMantenimiento(equipoId);
        
        // Semántica correcta: 200 OK si estaba vacío
        if (!eliminado) {
            return ResponseEntity.ok(Map.of(
                "mensaje", "El equipo con ID " + equipoId + " no tenía mantenimientos registrados."
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Todos los mantenimientos fueron eliminados para el equipo con ID " + equipoId
        ));
    }

    
    // DELETE: Eliminar UN mantenimiento específico por su ID de registro
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mantenimiento por ID", description = "Elimina un único registro de mantenimiento del sistema mediante su ID único.")
    public ResponseEntity<Map<String, String>> eliminarMantenimientoPorId(@PathVariable Long id) {
        // Llama al método del service que borra un registro individual
        boolean eliminado = mantenimientoService.eliminarMantenimientoPorId(id);
        
        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No existe un registro de mantenimiento con ID " + id));
        }
        
        return ResponseEntity.ok(Map.of(
            "mensaje", "Registro de mantenimiento eliminado correctamente."
        ));
    }


    // GET: Verificar si un equipo requiere mantenimiento preventivo
    @GetMapping("/equipo/{equipoId}/requiere")
    @Operation(summary = "Verificar requerimiento de mantenimiento", description = "Determina si un equipo requiere mantenimiento preventivo basándose en si pasaron más de 6 meses desde el último.")
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
package cl.instrumentum.service_rig.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import cl.instrumentum.service_rig.service.RigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

// Es auxiliar para el InventarioService, 
// para consultar si un equipo está asignado a alguna canción, 
// sin necesidad de traer toda la info del rig.

@RestController
@RequestMapping("/api/v2/equipos")
@Tag(name = "Controlador Auxiliar de Equipos", description = "Endpoints de soporte rápido para otros microservicios (como Inventario)")
public class EquipoAuxController {

    @Autowired
    private RigService rigService;

    // FIX: antes devolvía Map<String, Boolean> {"asignado": true/false}
    // pero InventarioService lo consumía con bodyToMono(Boolean.class),
    // causando un 500. Se simplifica para devolver el boolean directamente.
    @GetMapping("/en-cancion/{equipoId}")
    @Operation(summary = "Verificar asignación de equipo", description = "Responde directamente con un valor booleano si el equipo está asignado a alguna canción del sistema.")
    public boolean estaEnCancion(@PathVariable Long equipoId) {
        return rigService.equipoEstaEnAlgunaCancion(equipoId);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado en el auxiliar de equipos.";
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
package cl.instrumentum.service_logistica.controller;

import cl.instrumentum.service_logistica.model.Contenedor;
import cl.instrumentum.service_logistica.model.ContenedorEquipo;
import cl.instrumentum.service_logistica.service.LogisticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2/logistica")
@Tag(name = "Logistica", description = "Gestion de contenedores y empaque de equipos para shows")
public class ContenedorController {

    @Autowired
    private LogisticaService logisticaService;

// ---------------- CRUD Contenedores ---------------- \\

// Crear contenedor
    @Operation(summary = "Crear un nuevo contenedor", description = "Registra un nuevo contenedor en la base de datos")
    // DESPUÉS
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody Contenedor contenedor) {
        try {
            Contenedor nuevo = logisticaService.crearContenedor(contenedor);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Contenedor registrado exitosamente", "contenedor", nuevo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }
    }

// Listar todos los contenedores
    @Operation(summary = "Lista todos los contenedores", description = "Obtiene todos los contenedores registrados en el sistema")
    @GetMapping("/todos")
    public List<Contenedor> listarTodos() {
        return logisticaService.listarTodosContenedores();
    }

// Listar contendedor por id
    @Operation(summary = "Obtener un contenedor por ID", description = "Busca un contenedor específico en el sistema por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return logisticaService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: Contenedor no encontrado"));
    }

// Listar contenedor por banda id
    @Operation(summary = "Listar contenedores de una banda", description = "Obtiene todos los contenedores asociados al ID de una banda")
    @GetMapping("/banda/{idBanda}")
    public ResponseEntity<?> listarPorBanda(@PathVariable Long idBanda) {
        try {
            return ResponseEntity.ok(logisticaService.listarPorBanda(idBanda));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

// Actualizar contenedor
    @Operation(summary = "Actualizar un contenedor", description = "Modifica los datos de nombre o peso de un contenedor usando su ID")
    @PutMapping("/{id}")

    public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody Contenedor datos) {
        return logisticaService.buscarPorId(id)
                .map(c -> {
                    c.setNombreCaja(datos.getNombreCaja());
                    c.setPeso(datos.getPeso());
                    logisticaService.actualizarContenedor(c);
                    return ResponseEntity.ok("Contenedor actualizado exitosamente");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Contenedor no encontrado"));
    }

// Eliminar contenedor
    @Operation(summary = "Eliminar un contenedor", description = "Borra el contenedor y desasocia todos los equipos que tenía guardados")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        try {
            logisticaService.eliminarContenedor(id);
            return ResponseEntity.ok("Contenedor y sus equipos asociados eliminados exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

// ---------------- CRUD Equipos en contenedores ---------------- \\

// Agregar equipo a un contenedor
    @Operation(summary = "Agregar equipo a un contenedor", description = "Valida con Inventario que el instrumento exista y, si es correcto, lo mete en la caja")
    @PostMapping("/{id}/equipos")
    public ResponseEntity<String> agregarEquipo(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        try {
            logisticaService.agregarEquipo(id, body.get("idEquipo"));
            return ResponseEntity.status(HttpStatus.CREATED).body("Equipo agregado al contenedor exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

// Listar todos los equipos en contenedores
    @Operation(summary = "Listar todos los equipos con sus contenedores", description = "Devuelve todas las relaciones equipo-contenedor")
    @GetMapping("/todosEquipos")
    public List<ContenedorEquipo> listarEquiposEnContenedores() {
        return logisticaService.listarTodosLosEquiposEnContenedores();
    }

// Listar equipos de un contenedor
    @Operation(summary = "Listar equipos de un contenedor", description = "Muestra qué instrumentos están metidos dentro de un baúl específico")
    @GetMapping("/{id}/equipos")
    public ResponseEntity<?> listarEquipos(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(logisticaService.listarEquiposDeContenedor(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

// Remover un equipo de un contenedor
    @Operation(summary = "Remover equipo de un contenedor", description = "Saca un instrumento sin afectar al contenedor")
    @DeleteMapping("/{id}/equipos/{idEquipo}")
    public ResponseEntity<String> removerEquipo(@PathVariable Long id, @PathVariable Long idEquipo) {
        try {
            logisticaService.removerEquipo(id, idEquipo);
            return ResponseEntity.ok("Equipo removido del contenedor exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
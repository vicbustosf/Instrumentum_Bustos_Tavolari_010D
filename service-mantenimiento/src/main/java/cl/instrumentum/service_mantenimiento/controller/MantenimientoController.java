package cl.instrumentum.service_mantenimiento.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.instrumentum.service_mantenimiento.model.Mantenimiento;
import cl.instrumentum.service_mantenimiento.service.MantenimientoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/mantenimientos")
public class MantenimientoController {

    @Autowired
    private MantenimientoService mantenimientoService;


    //Este post se encarga de registrar un nuevo mantenimiento, 
    // recibe un objeto Mantenimiento en el cuerpo de la solicitud, lo valida 
    // y lo guarda utilizando el servicio. Devuelve el mantenimiento guardado con un estado HTTP 201 (CREATED).
    @PostMapping
    public ResponseEntity<Mantenimiento> registrar(@Valid @RequestBody Mantenimiento mantenimiento) {
        Mantenimiento guardado = mantenimientoService.registrarMantenimiento(mantenimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/equipo/{equipoId}")
    public List<Mantenimiento> listarPorEquipo(@PathVariable Long equipoId) {
        return mantenimientoService.listarPorEquipo(equipoId);
    }


    //Si el equipo requiere mantenimiento, devuelve un mapa con la clave "alerta" y el valor true.
    @GetMapping("/alerta/{equipoId}")
    public ResponseEntity<Map<String, Boolean>> alertaMantenimiento(@PathVariable Long equipoId) {
        boolean alerta = mantenimientoService.requiereMantenimiento(equipoId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("alerta", alerta);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Mantenimiento> actualizar(@PathVariable Long id, @RequestBody Mantenimiento datos) {
        return mantenimientoService.buscarPorId(id)
                .map(m -> {
                    m.setFecha(datos.getFecha());
                    m.setDescripcion(datos.getDescripcion());
                    m.setCosto(datos.getCosto());
                    return ResponseEntity.ok(mantenimientoService.registrarMantenimiento(m));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/equipo/{equipoId}")
    public ResponseEntity<Map<String, String>> eliminarPorEquipo(@PathVariable Long equipoId) {
        List<Mantenimiento> mantenimientos =
                mantenimientoService.listarPorEquipo(equipoId);

        if (mantenimientos.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("mensaje",
                            "No existen mantenimientos registrados para el equipo "+ equipoId));
        }
        mantenimientoService.eliminarPorEquipo(equipoId);
        return ResponseEntity.ok(
                Map.of("mensaje",
                        "Los mantenimientos del equipo "
                                + equipoId
                                + " fueron eliminados correctamente."
                    ));
    }
}
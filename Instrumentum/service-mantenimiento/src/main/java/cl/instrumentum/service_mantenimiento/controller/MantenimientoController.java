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
@RequestMapping("/api/v1/mantenimientos")
public class MantenimientoController {

    @Autowired
    private MantenimientoService mantenimientoService;


    //Registrar nuevo mantenimiento, se espera un JSON con los datos del mantenimiento 
    //     pero debe existir el equipo al que se le asigna.
    @PostMapping
    public ResponseEntity<Mantenimiento> registrar(@Valid @RequestBody Mantenimiento mantenimiento) {
        Mantenimiento guardado = mantenimientoService.registrarMantenimiento(mantenimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // Listar mantenimientos por equipo, pide ID del equipo como parámetro en la URL
    @GetMapping("/equipo/{equipoId}")
    public List<Mantenimiento> listarPorEquipo(@PathVariable Long equipoId) {
        return mantenimientoService.listarPorEquipo(equipoId);
    }


    // Endpoint para verificar si un equipo requiere mantenimiento, 
    // devuelve un JSON con {"alerta": true/false}, si es true, el equipo requiere 
    // mantenimiento, si es false, no lo requiere. 
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
    public ResponseEntity<Void> eliminarPorEquipo(@PathVariable Long equipoId) {
        mantenimientoService.eliminarPorEquipo(equipoId);
        return ResponseEntity.noContent().build();
    }
}
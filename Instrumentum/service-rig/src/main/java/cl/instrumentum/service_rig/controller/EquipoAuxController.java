package cl.instrumentum.service_rig.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.instrumentum.service_rig.service.RigService;
 
@RestController
@RequestMapping("/api/v1/equipos")
public class EquipoAuxController {
 
    @Autowired
    private RigService rigService;
 
    // FIX: antes devolvía Map<String, Boolean> {"asignado": true/false}
    // pero InventarioService lo consumía con bodyToMono(Boolean.class),
    // causando un 500. Se simplifica para devolver el boolean directamente.
    @GetMapping("/en-cancion/{equipoId}")
    public boolean estaEnCancion(@PathVariable Long equipoId) {
        return rigService.equipoEstaEnAlgunaCancion(equipoId);
    }
}
 
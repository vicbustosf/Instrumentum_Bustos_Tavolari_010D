package cl.instrumentum.service_rig.controller;

import java.util.HashMap;
import java.util.Map;
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

    @GetMapping("/en-cancion/{equipoId}")
    public Map<String, Boolean> estaEnCancion(@PathVariable Long equipoId) {
        boolean asignado = rigService.equipoEstaEnAlgunaCancion(equipoId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("asignado", asignado);
        return response;
    }
}
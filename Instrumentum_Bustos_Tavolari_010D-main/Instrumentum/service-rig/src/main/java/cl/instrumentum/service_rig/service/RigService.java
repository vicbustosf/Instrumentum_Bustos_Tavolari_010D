package cl.instrumentum.service_rig.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import cl.instrumentum.service_rig.model.Cancion;
import cl.instrumentum.service_rig.model.EquipoCancion;
import cl.instrumentum.service_rig.repository.CancionRepository;
import cl.instrumentum.service_rig.repository.EquipoCancionRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RigService {

    @Autowired
    private CancionRepository cancionRepository;

    @Autowired
    private EquipoCancionRepository equipoCancionRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private void validarBanda(Long bandaId) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/v2/bandas/" + bandaId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("La banda con ID " + bandaId + " no existe o el módulo de usuarios no responde.");
        }
    }

    private void validarEquipo(Long equipoId) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/api/v2/equipos/" + equipoId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("El equipo con ID " + equipoId + " no existe o el módulo de inventario no responde.");
        }
    }

    public List<Cancion> listarCancionesPorBanda(Long bandaId) {
        return cancionRepository.findByBandaId(bandaId);
    }

    public Optional<Cancion> buscarCancionPorId(Long id) {
        return cancionRepository.findById(id);
    }

    public Cancion guardarCancion(Cancion cancion) {
        if (cancion.getBandaId() != null) {
            validarBanda(cancion.getBandaId());
        }
        return cancionRepository.save(cancion);
    }

    public boolean equipoEstaEnAlgunaCancion(Long equipoId) {
    return equipoCancionRepository.existsByEquipoId(equipoId);
}

    public boolean eliminarCancion(Long id) {
        if (cancionRepository.existsById(id)) {
            cancionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<EquipoCancion> buscarEquipoCancion(Cancion cancion, Long equipoId) {
        return equipoCancionRepository.findByCancionAndEquipoId(cancion, equipoId);
    }

    @Transactional
    public EquipoCancion asignarEquipo(Cancion cancion, Long equipoId, Integer posicion, String seteoPerillas) {
        validarEquipo(equipoId);
        
        if (equipoCancionRepository.findByCancionAndEquipoId(cancion, equipoId).isPresent()) {
            throw new RuntimeException("El equipo ya se encuentra asignado a esta canción.");
        }

        EquipoCancion ec = new EquipoCancion();
        ec.setCancion(cancion);
        ec.setEquipoId(equipoId);
        ec.setPosicion(posicion);
        ec.setSeteoPerillas(seteoPerillas);
        return equipoCancionRepository.save(ec);
    }

    public EquipoCancion guardarEquipoCancion(EquipoCancion ec) {
        return equipoCancionRepository.save(ec);
    }

    @Transactional
    public boolean removerEquipo(Cancion cancion, Long equipoId) {
        Optional<EquipoCancion> ecOpt = equipoCancionRepository.findByCancionAndEquipoId(cancion, equipoId);
        if (ecOpt.isPresent()) {
            equipoCancionRepository.delete(ecOpt.get());
            return true;
        }
        return false;
    }

    public Map<String, Object> obtenerSetupCompleto(Long cancionId) {
        return cancionRepository.findById(cancionId)
                .map(cancion -> {
                    List<EquipoCancion> equipos = equipoCancionRepository.findByCancionOrderByPosicionAsc(cancion);
                    return Map.<String, Object>of("cancion", cancion, "equipos", equipos);
                })
                .orElseThrow(() -> new RuntimeException("No existe una canción con ID " + cancionId));
    }
}
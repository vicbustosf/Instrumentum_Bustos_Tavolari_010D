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
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class RigService {

    @Autowired
    private CancionRepository cancionRepository;

    @Autowired
    private EquipoCancionRepository equipoCancionRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;


    private void validarBanda(Long bandaId) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/api/v2/bandas/" + bandaId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void validarEquipo(Long equipoId) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/v2/equipos/" + equipoId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public Cancion crearCancion(Cancion cancion) {
        if (cancion.getBandaId() == null)
        {
            throw new RuntimeException();
        }

        validarBanda(cancion.getBandaId());
        return cancionRepository.save(cancion);
    }

    public List<Cancion> listarCancionesPorBanda(Long bandaId) {
        return cancionRepository.findByBandaId(bandaId);
    }

    public Optional<Cancion> buscarCancionPorId(Long id) {
        return cancionRepository.findById(id);
    }

    public Cancion actualizarCancion(Cancion cancion) {
        return cancionRepository.save(cancion);
    }

    // El transactional sirve para que si algo falla en el proceso de eliminación, 
    // se revierta toda la operación y no quede nada a medias
    @Transactional
    public void eliminarCancion(Long cancionId) {
        Cancion cancion = cancionRepository.findById(cancionId).orElseThrow(RuntimeException::new);
        cancionRepository.delete(cancion);
    }

    @Transactional
    public EquipoCancion asignarEquipo(Long cancionId, Long equipoId, Integer posicion, String seteoPerillas) {
        Cancion cancion = cancionRepository.findById(cancionId).orElseThrow(RuntimeException::new);
        validarEquipo(equipoId);
        EquipoCancion ec = new EquipoCancion();
        ec.setCancion(cancion);
        ec.setEquipoId(equipoId);
        ec.setPosicion(posicion);
        ec.setSeteoPerillas(seteoPerillas);
        return equipoCancionRepository.save(ec);
    }

    
    @Transactional
    public EquipoCancion actualizarEquipo(Long cancionId, Long equipoId, Integer posicion, String seteoPerillas) {
        Cancion cancion = cancionRepository.findById(cancionId).orElseThrow(RuntimeException::new);
        EquipoCancion ec = equipoCancionRepository.findByCancionAndEquipoId(cancion, equipoId)
                .orElseThrow(RuntimeException::new);
        if (posicion != null) ec.setPosicion(posicion);
        if (seteoPerillas != null) ec.setSeteoPerillas(seteoPerillas);
        return equipoCancionRepository.save(ec);
    }

    @Transactional
    public void removerEquipo(Long cancionId, Long equipoId) {
        Cancion cancion = cancionRepository.findById(cancionId).orElseThrow(RuntimeException::new);
        EquipoCancion ec = equipoCancionRepository.findByCancionAndEquipoId(cancion, equipoId)
                .orElseThrow(RuntimeException::new);
        equipoCancionRepository.delete(ec);
    }

    public boolean equipoEstaEnAlgunaCancion(Long equipoId) {
        return equipoCancionRepository.existsByEquipoId(equipoId);
    }

    public Map<String, Object> obtenerSetupCompleto(Long cancionId) {
        Cancion cancion = cancionRepository.findById(cancionId).orElseThrow(RuntimeException::new);
        List<EquipoCancion> equipos = equipoCancionRepository.findByCancionOrderByPosicionAsc(cancion);
        return Map.of("cancion", cancion, "equipos", equipos);
    }
}
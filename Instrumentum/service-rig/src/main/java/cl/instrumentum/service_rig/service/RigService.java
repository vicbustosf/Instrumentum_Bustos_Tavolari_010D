package cl.instrumentum.service_rig.service;

import java.util.List;
import java.util.Map;
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

    @PostConstruct
    public void cargarDatosPrueba() {
        if (cancionRepository.count() > 0) return;
        Cancion cancion = new Cancion(null, "Mi Primera Cancion", 1L, 180, new java.util.ArrayList<>());
        cancion = cancionRepository.save(cancion);
        equipoCancionRepository.save(new EquipoCancion(null, cancion, 1L, 1, "Volumen 7, Tonos al maximo"));
    }

    private void validarBanda(Long bandaId) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/api/v1/bandas/" + bandaId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private void validarEquipo(Long equipoId) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/v1/equipos/" + equipoId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public Cancion crearCancion(Cancion cancion) {
        if (cancion.getBandaId() == null) throw new RuntimeException();
        validarBanda(cancion.getBandaId());
        return cancionRepository.save(cancion);
    }

    public List<Cancion> listarCancionesPorBanda(Long bandaId) {
        return cancionRepository.findByBandaId(bandaId);
    }

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
        //Instancia la canción, si no existe lanza error 404
        Cancion cancion = cancionRepository.findById(cancionId).orElseThrow(RuntimeException::new);

        //Busca los equipos asociados a la canción, ordenados por posición
        List<EquipoCancion> equipos = equipoCancionRepository.findByCancionOrderByPosicionAsc(cancion);
        //map que devuelve un objeto con la canción y la lista de equipos asociados a esa canción
        return Map.of("cancion", cancion, "equipos", equipos);
    }
}
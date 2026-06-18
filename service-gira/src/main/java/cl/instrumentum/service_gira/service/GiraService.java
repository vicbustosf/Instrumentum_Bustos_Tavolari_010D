package cl.instrumentum.service_gira.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import cl.instrumentum.service_gira.model.Gira;
import cl.instrumentum.service_gira.model.ParadaGira;
import cl.instrumentum.service_gira.repository.GiraRepository;
import cl.instrumentum.service_gira.repository.ParadaGiraRepository;

@Service
public class GiraService {

    @Autowired
    private GiraRepository giraRepository;

    @Autowired
    private ParadaGiraRepository paradaRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;


    //        VALIDACIONES WEBCLIENT       


    // Valida que la banda exista en service-usuario (Asumiendo puerto 8081)
    private void validarBanda(Long idBanda) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/api/v2/bandas/" + idBanda)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // Valida que el evento exista en service-evento (Asumiendo puerto 8086, ajusta si es otro)
    private void validarEvento(Long idEvento) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8086/api/v2/eventos/" + idEvento)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    //       GESTIÓN DE GIRAS   
    

    public List<Gira> listarGiras() {
        return giraRepository.findAll();
    }

    public List<Gira> listarPorBanda(Long idBanda) {
        return giraRepository.findByIdBanda(idBanda);
    }

    public Optional<Gira> buscarGiraPorId(Long id) {
        return giraRepository.findById(id);
    }

    public Gira guardarGira(Gira gira) {
        validarBanda(gira.getIdBanda());
        return giraRepository.save(gira);
    }

    public Gira actualizarGira(Long id, Gira giraActualizada) {
        return giraRepository.findById(id)
                .map(gira -> {
                    validarBanda(giraActualizada.getIdBanda()); // Valida por si cambiaron la banda
                    gira.setIdBanda(giraActualizada.getIdBanda());
                    gira.setNombreGira(giraActualizada.getNombreGira());
                    gira.setFechaInicio(giraActualizada.getFechaInicio());
                    gira.setFechaFin(giraActualizada.getFechaFin());
                    return giraRepository.save(gira);
                })
                .orElse(null); // El controlador lo envolverá en Optional.ofNullable()
    }

    // @Transactional asegura que si falla el borrado de paradas, no se borre la gira a medias
    @Transactional
    public boolean eliminarGira(Long id) {
        if (giraRepository.existsById(id)) {
            paradaRepository.deleteByIdGira(id); // Limpia las paradas asociadas primero
            giraRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 
    //      GESTIÓN DE PARADAS    
    // 

    public List<ParadaGira> listarParadasPorGira(Long idGira) {
        return paradaRepository.findByIdGira(idGira);
    }

    public Optional<ParadaGira> buscarParadaPorId(Long id) {
        return paradaRepository.findById(id);
    }

    public ParadaGira guardarParada(ParadaGira parada) {
        if (!giraRepository.existsById(parada.getIdGira())) {
            throw new RuntimeException("La gira con ID " + parada.getIdGira() + " no existe.");
        }
        validarEvento(parada.getIdEvento());
        return paradaRepository.save(parada);
    }

    public ParadaGira actualizarParada(Long id, ParadaGira paradaActualizada) {
        return paradaRepository.findById(id)
                .map(parada -> {
                    if (!giraRepository.existsById(paradaActualizada.getIdGira())) {
                        throw new RuntimeException("La gira con ID " + paradaActualizada.getIdGira() + " no existe.");
                    }
                    validarEvento(paradaActualizada.getIdEvento()); // Valida por si cambiaron el evento asociado
                    parada.setIdGira(paradaActualizada.getIdGira());
                    parada.setIdEvento(paradaActualizada.getIdEvento());
                    parada.setCiudad(paradaActualizada.getCiudad());
                    parada.setAlojamiento(paradaActualizada.getAlojamiento());
                    parada.setTransporte(paradaActualizada.getTransporte());
                    return paradaRepository.save(parada);
                })
                .orElse(null);
    }

    public boolean eliminarParada(Long id) {
        if (paradaRepository.existsById(id)) {
            paradaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
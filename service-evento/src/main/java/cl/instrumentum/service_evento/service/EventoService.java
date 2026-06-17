package cl.instrumentum.service_evento.service;

import cl.instrumentum.service_evento.model.Evento;
import cl.instrumentum.service_evento.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    // CORRECCIÓN PROBLEMA 1: WebClient inyectado para validar que la banda exista
    // antes de guardar o actualizar un evento.
    @Autowired
    private WebClient.Builder webClientBuilder;

    // CORRECCIÓN PROBLEMA 1: Valida que la banda exista en service usuario.
    // Si no existe o el servicio no responde, lanza RuntimeException y aborta la operación.
    private void validarBanda(Long idBanda) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/api/v1/bandas/" + idBanda)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> buscarPorId(Long id) {
        return eventoRepository.findById(id);
    }

    public Evento guardarEvento(Evento evento) {
        // CORRECCIÓN PROBLEMA 1: Se valida la banda antes de persistir
        validarBanda(evento.getIdBanda());
        return eventoRepository.save(evento);
    }

    public Evento actualizarEvento(Long id, Evento eventoActualizado) {
        return eventoRepository.findById(id)
                .map(evento -> {
                    // CORRECCIÓN PROBLEMA 1: Se valida la banda antes de actualizar,
                    // por si el idBanda del body cambió a uno que no existe
                    validarBanda(eventoActualizado.getIdBanda());
                    evento.setIdBanda(eventoActualizado.getIdBanda());
                    evento.setNombre(eventoActualizado.getNombre());
                    evento.setFecha(eventoActualizado.getFecha());
                    evento.setCanciones(eventoActualizado.getCanciones());
                    return eventoRepository.save(evento);
                })
                .orElse(null);
    }

    public boolean eliminarEvento(Long id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Evento> obtenerPorBanda(Long idBanda) {
        return eventoRepository.findByIdBanda(idBanda);
    }
}
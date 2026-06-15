package cl.instrumentum.service_evento.service;

import cl.instrumentum.service_evento.model.Evento;
import cl.instrumentum.service_evento.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> buscarPorId(Long id) {
        return eventoRepository.findById(id);
    }

    public Evento guardarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Evento actualizarEvento(Long id, Evento eventoActualizado) {
        return eventoRepository.findById(id)
                .map(evento -> {
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
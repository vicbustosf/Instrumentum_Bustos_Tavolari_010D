package cl.instrumentum.service_evento.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.instrumentum.service_evento.model.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByIdBanda(Long idBanda);

}
package cl.instrumentum.service_evento.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import cl.instrumentum.service_evento.model.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByIdBanda(Long idBanda);

    List<Evento> findByIdBandaAndFechaBetween(Long idBanda, LocalDate desde, LocalDate hasta);

    // Busca eventos que contengan un id de canción en el campo canciones
    @Query("""
        SELECT e FROM Evento e
        WHERE e.idBanda = :idBanda
        AND (e.canciones = :id
             OR e.canciones LIKE CONCAT(:id, ',%')
             OR e.canciones LIKE CONCAT('%,', :id, ',%')
             OR e.canciones LIKE CONCAT('%,', :id))
        """)
    List<Evento> findByIdBandaAndCancion(@Param("idBanda") Long idBanda,
                                          @Param("id") String id);
}
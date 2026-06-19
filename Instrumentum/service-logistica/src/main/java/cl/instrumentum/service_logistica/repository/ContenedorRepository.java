package cl.instrumentum.service_logistica.repository;

import cl.instrumentum.service_logistica.model.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {
    List<Contenedor> findByIdBanda(Long idBanda);
}
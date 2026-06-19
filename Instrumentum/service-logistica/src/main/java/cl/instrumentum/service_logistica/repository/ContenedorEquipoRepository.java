package cl.instrumentum.service_logistica.repository;

import cl.instrumentum.service_logistica.model.Contenedor;
import cl.instrumentum.service_logistica.model.ContenedorEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ContenedorEquipoRepository extends JpaRepository<ContenedorEquipo, Long> {
    List<ContenedorEquipo> findByContenedor(Contenedor contenedor);
    
    Optional<ContenedorEquipo> findByContenedorAndIdEquipo(Contenedor contenedor, Long idEquipo);
}
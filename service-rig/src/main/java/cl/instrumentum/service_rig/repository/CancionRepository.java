package cl.instrumentum.service_rig.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.instrumentum.service_rig.model.Cancion;

public interface CancionRepository extends JpaRepository<Cancion, Long> {
    
    List<Cancion> findByBandaId(Long bandaId);
}
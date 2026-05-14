package cl.instrumentum.service_rig.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.instrumentum.service_rig.model.Cancion;
import cl.instrumentum.service_rig.model.EquipoCancion;

public interface EquipoCancionRepository extends JpaRepository<EquipoCancion, Long> {

    List<EquipoCancion> findByCancionOrderByPosicionAsc(Cancion cancion);

    Optional<EquipoCancion> findByCancionAndEquipoId(Cancion cancion, Long equipoId);
    
    boolean existsByEquipoId(Long equipoId);
}
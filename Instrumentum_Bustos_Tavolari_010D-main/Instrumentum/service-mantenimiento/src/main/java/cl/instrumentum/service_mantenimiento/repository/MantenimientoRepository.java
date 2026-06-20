package cl.instrumentum.service_mantenimiento.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.instrumentum.service_mantenimiento.model.Mantenimiento;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    //METODOS PROPIOS
    List<Mantenimiento> findByEquipoIdOrderByFechaDesc(Long equipoId);

    Optional<Mantenimiento> findTopByEquipoIdOrderByFechaDesc(Long equipoId);
    
}
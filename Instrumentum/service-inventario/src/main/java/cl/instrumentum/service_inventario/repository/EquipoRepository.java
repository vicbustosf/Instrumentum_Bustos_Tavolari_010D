package cl.instrumentum.service_inventario.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import cl.instrumentum.service_inventario.model.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    List<Equipo> findByPropietarioId(Long propietarioId);

    Optional<Equipo> findByNombre(String nombre);

    @Query("""
       SELECT e FROM Equipo e WHERE  
       (:nombre IS NULL OR e.nombre = :nombre) AND  
       (:marca IS NULL OR e.marca.nombre = :marca) AND  
       (:categoria IS NULL OR e.categoria.nombre = :categoria)
           
           """)
    List<Equipo> buscarConFiltrosExactos(@Param("nombre") String nombre,
                                          @Param("marca") String marca,
                                          @Param("categoria") String categoria);
}
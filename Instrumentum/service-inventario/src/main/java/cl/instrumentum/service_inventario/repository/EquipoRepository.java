package cl.instrumentum.service_inventario.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import cl.instrumentum.service_inventario.model.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByPropietarioId(Long propietarioId);

    // Método para buscar por nombre exacto, ejemplo:
    // nombre "Stratocaster", marca "Fender", categoria "Guitarra"
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

//  Optional: Es una clase contenedora que puede contener un valor o estar vacía. 
//  Se utiliza para evitar problemas de null y proporciona métodos para manejar 
//  la presencia o ausencia de un valor de manera segura.

//  En este caso, el método findByNombre devuelve un Optional<Equipo>,
//  lo que significa que puede contener un objeto Equipo si se encuentra
//  uno con el nombre especificado, o estar vacío si no se encuentra ningún
//  equipo con ese nombre.
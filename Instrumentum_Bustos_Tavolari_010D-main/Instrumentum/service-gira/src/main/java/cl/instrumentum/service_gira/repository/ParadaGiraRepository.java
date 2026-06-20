package cl.instrumentum.service_gira.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import cl.instrumentum.service_gira.model.ParadaGira;

public interface ParadaGiraRepository extends JpaRepository<ParadaGira, Long> {
    
    // Usamos @Query para hacer el cruce manualmente y nombramos el método de forma limpia
    @Query
        ("""
        SELECT p 
        FROM ParadaGira p 
        WHERE p.gira.idGira = :idGira
         """)
    List<ParadaGira> obtenerPorIdGira(@Param("idGira") Long idGira);
    
}
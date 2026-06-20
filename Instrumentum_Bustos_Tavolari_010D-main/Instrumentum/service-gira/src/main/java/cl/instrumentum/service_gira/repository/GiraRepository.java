package cl.instrumentum.service_gira.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.instrumentum.service_gira.model.Gira;

@Repository
public interface GiraRepository extends JpaRepository<Gira, Long> {
    
    // Encuentra todas las giras asociadas a una banda específica
    List<Gira> findByIdBanda(Long idBanda);
}
package cl.instrumentum.service_gira.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.instrumentum.service_gira.model.ParadaGira;

@Repository
public interface ParadaGiraRepository extends JpaRepository<ParadaGira, Long> {
    
    // Lista todas las paradas logísticas que pertenecen a una gira
    List<ParadaGira> findByIdGira(Long idGira);
    
    Optional<ParadaGira> findByIdEvento(Long idEvento);
    
    // Ideal para limpiar la BD si se elimina una gira completa
    void deleteByIdGira(Long idGira);
}
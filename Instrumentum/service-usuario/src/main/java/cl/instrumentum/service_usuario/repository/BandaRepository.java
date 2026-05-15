package cl.instrumentum.service_usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.instrumentum.service_usuario.model.Banda;

@Repository
public interface BandaRepository extends JpaRepository<Banda, Long> {

    // Método personalizado para encontrar una banda por su nombre
    Banda findByNombre(String nombre);
}
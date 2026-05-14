package cl.instrumentum.service_inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.instrumentum.service_inventario.model.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
}
package cl.instrumentum.service_inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.instrumentum.service_inventario.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
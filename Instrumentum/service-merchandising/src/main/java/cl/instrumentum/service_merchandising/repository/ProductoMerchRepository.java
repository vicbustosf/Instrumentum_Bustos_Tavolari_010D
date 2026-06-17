package cl.instrumentum.service_merchandising.repository;

import cl.instrumentum.service_merchandising.model.ProductoMerch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoMerchRepository extends JpaRepository<ProductoMerch, Long> {
    List<ProductoMerch> findByIdBanda(Long idBanda);
}
package cl.instrumentum.service_merchandising.repository;

import cl.instrumentum.service_merchandising.model.ProductoMerch;
import cl.instrumentum.service_merchandising.model.VentaMerch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VentaMerchRepository extends JpaRepository<VentaMerch, Long> {
    List<VentaMerch> findByProducto(ProductoMerch producto);

    List<VentaMerch> findByIdEventoOrigen(Long idEventoOrigen);
}
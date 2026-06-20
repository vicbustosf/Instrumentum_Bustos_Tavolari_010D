package cl.instrumentum.service_finanza.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.instrumentum.service_finanza.model.Transaccion;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByIdBanda(Long idBanda);

}

package cl.instrumentum.service_specs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cl.instrumentum.service_specs.model.EspecificacionElectronica;

@Repository
public interface EspecificacionElectronicaRepository
        extends JpaRepository<EspecificacionElectronica, Long> {

    List<EspecificacionElectronica> findByTipoCircuito(String tipoCircuito);

    // Equipos cuyo consumo supera un umbral dado (útil para alertas)
    @Query("""
            SELECT e FROM EspecificacionElectronica e
            WHERE e.consumo > :umbralMa
            """)
    List<EspecificacionElectronica> findByConsumoMayorA(Double umbralMa);
}

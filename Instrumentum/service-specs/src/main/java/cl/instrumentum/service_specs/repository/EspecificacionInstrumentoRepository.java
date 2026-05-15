package cl.instrumentum.service_specs.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.instrumentum.service_specs.model.EspecificacionInstrumento;

public interface EspecificacionInstrumentoRepository extends JpaRepository<EspecificacionInstrumento, Long> {

    // Métodos de consulta personalizados
    List<EspecificacionInstrumento> findByTipoMadera(String tipoMadera);

    List<EspecificacionInstrumento> findByConfigPastillas(String configPastillas);
}
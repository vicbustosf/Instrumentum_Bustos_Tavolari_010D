package cl.instrumentum.service_mantenimiento.service;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import cl.instrumentum.service_mantenimiento.model.Mantenimiento;
import cl.instrumentum.service_mantenimiento.repository.MantenimientoRepository;

 
@Service
public class MantenimientoService {
 
    @Autowired
    private MantenimientoRepository mantenimientoRepository;
 
    @Autowired
    private WebClient.Builder webClientBuilder;
 
    
    public Optional<Mantenimiento> buscarPorId(Long id) {
        return mantenimientoRepository.findById(id);
    }
 
    private void validarEquipo(Long equipoId) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/v2/equipos/" + equipoId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
 
    public Mantenimiento registrarMantenimiento(Mantenimiento mantenimiento) {
            if (mantenimiento.getEquipoId() == null) 
                throw new RuntimeException();

        validarEquipo(mantenimiento.getEquipoId());
            if (mantenimiento.getFecha() == null) 
                mantenimiento.setFecha(LocalDate.now());

            if (mantenimiento.getCosto() == null) mantenimiento.setCosto(0.0);

        return mantenimientoRepository.save(mantenimiento);
    }
 
    public List<Mantenimiento> listarPorEquipo(Long equipoId) {
        return mantenimientoRepository.findByEquipoIdOrderByFechaDesc(equipoId);
    }
 
    public boolean requiereMantenimiento(Long equipoId) {
        Optional<Mantenimiento> ultimo = mantenimientoRepository.findTopByEquipoIdOrderByFechaDesc(equipoId);
        if (ultimo.isEmpty()) return true;
        return ultimo.get().getFecha().isBefore(LocalDate.now().minusMonths(6));
    }
 
    
    //usamos @Transactional para asegurar que la eliminación se realice correctamente y 
    // evitar problemas de integridad, o sea, que no queden registros huérfanos en la base de datos 
    // relacionados con el equipo eliminado.
    @Transactional
    public void eliminarMantenimiento(Long equipoId) {
        mantenimientoRepository.deleteByEquipoId(equipoId);
    }
}
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
import jakarta.annotation.PostConstruct;
 
@Service
public class MantenimientoService {
 
    @Autowired
    private MantenimientoRepository mantenimientoRepository;
 
    @Autowired
    private WebClient.Builder webClientBuilder;
 
    @PostConstruct
    public void cargarDatosPrueba() {
        if (mantenimientoRepository.count() > 0) return;

        // Seeds originales
        mantenimientoRepository.save(new Mantenimiento(null, 1L, LocalDate.now().minusMonths(2), "Cambio de cuerdas", 50.0));
        mantenimientoRepository.save(new Mantenimiento(null, 1L, LocalDate.now().minusMonths(7), "Ajuste de pastillas", 30.0));

        // Seeds nuevos
        mantenimientoRepository.save(new Mantenimiento(null, 4L, LocalDate.now().minusMonths(2), "Cambio de cuerdas y limpieza de pastillas", 25.0));
        mantenimientoRepository.save(new Mantenimiento(null, 5L, LocalDate.now().minusMonths(8), "Revisión de válvulas y limpieza general", 120.0));
        // equipoId:5 quedará con alerta:true intencionalmente (más de 6 meses)
    }

    public Optional<Mantenimiento> buscarPorId(Long id) {
        return mantenimientoRepository.findById(id);
    }
 
    private void validarEquipo(Long equipoId) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/v1/equipos/" + equipoId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
 
    public Mantenimiento registrarMantenimiento(Mantenimiento mantenimiento) {
        if (mantenimiento.getEquipoId() == null) throw new RuntimeException();
        validarEquipo(mantenimiento.getEquipoId());
        if (mantenimiento.getFecha() == null) mantenimiento.setFecha(LocalDate.now());
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
 
    // FIX: faltaba @Transactional. deleteByEquipoId() es una derived delete query
    // de Spring Data JPA y necesita transacción activa, sin ella lanza
    // TransactionRequiredException → 500 Internal Server Error.
    @Transactional
    public void eliminarPorEquipo(Long equipoId) {
        mantenimientoRepository.deleteByEquipoId(equipoId);
    }
}
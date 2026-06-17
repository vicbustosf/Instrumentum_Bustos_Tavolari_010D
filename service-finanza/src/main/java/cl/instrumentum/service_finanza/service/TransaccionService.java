package cl.instrumentum.service_finanza.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import cl.instrumentum.service_finanza.model.Transaccion;
import cl.instrumentum.service_finanza.repository.TransaccionRepository;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    // CORRECCIÓN PROBLEMA 1: WebClient inyectado para validar que la banda exista
    // antes de guardar o actualizar una transacción.
    @Autowired
    private WebClient.Builder webClientBuilder;

    // CORRECCIÓN PROBLEMA 1: Valida que la banda exista en service-usuario (puerto 8081).
    // Si no existe o el servicio no responde, lanza RuntimeException y aborta la operación.
    private void validarBanda(Long idBanda) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/api/v1/bandas/" + idBanda)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<Transaccion> listarTransacciones() {
        return transaccionRepository.findAll();
    }

    public Optional<Transaccion> buscarPorId(Long id) {
        return transaccionRepository.findById(id);
    }

    public Transaccion guardarTransaccion(Transaccion transaccion) {
        // CORRECCIÓN PROBLEMA 1: Se valida la banda antes de persistir
        validarBanda(transaccion.getIdBanda());
        return transaccionRepository.save(transaccion);
    }

    public Transaccion actualizarTransaccion(Long id, Transaccion transaccionActualizada) {
        return transaccionRepository.findById(id)
                .map(transaccion -> {
                    // CORRECCIÓN PROBLEMA 1: Se valida la banda antes de actualizar,
                    // por si el idBanda del body cambió a uno que no existe
                    validarBanda(transaccionActualizada.getIdBanda());
                    transaccion.setIdBanda(transaccionActualizada.getIdBanda());
                    transaccion.setTipoMovimiento(transaccionActualizada.getTipoMovimiento());
                    transaccion.setMonto(transaccionActualizada.getMonto());
                    transaccion.setFecha(transaccionActualizada.getFecha());
                    transaccion.setDescripcion(transaccionActualizada.getDescripcion());

                    return transaccionRepository.save(transaccion);
                })
                .orElse(null);
    }

    public boolean eliminarTransaccion(Long id) {
        if (transaccionRepository.existsById(id)) {
            transaccionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Transaccion> obtenerPorBanda(Long idBanda) {
        return transaccionRepository.findByIdBanda(idBanda);
    }
}
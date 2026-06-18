package cl.instrumentum.service_specs.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import cl.instrumentum.service_specs.model.EspecificacionElectronica;
import cl.instrumentum.service_specs.model.EspecificacionInstrumento;
import cl.instrumentum.service_specs.repository.EspecificacionElectronicaRepository;
import cl.instrumentum.service_specs.repository.EspecificacionInstrumentoRepository;
import jakarta.annotation.PostConstruct;

@Service
public class SpecsService {

    @Autowired
    private EspecificacionInstrumentoRepository instrumentoRepository;

    @Autowired
    private EspecificacionElectronicaRepository electronicaRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private void validarEquipo(Long equipoId) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/v2/equipos/" + equipoId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public EspecificacionInstrumento guardarInstrumento(Long equipoId, EspecificacionInstrumento espec) {
        validarEquipo(equipoId);   // ← presente
        espec.setIdEquipo(equipoId);
        return instrumentoRepository.save(espec);
    }

    public EspecificacionElectronica guardarElectronica(Long equipoId, EspecificacionElectronica datos) {
        validarEquipo(equipoId); // ← falta esta línea
        datos.setIdEquipo(equipoId);
        return electronicaRepository.save(datos);
    }

    public Object obtenerEspecificacionPorEquipo(Long equipoId) {
        Optional<EspecificacionInstrumento> inst = instrumentoRepository.findById(equipoId);
        if (inst.isPresent()) {
            return inst.get();
        }
        Optional<EspecificacionElectronica> elec = electronicaRepository.findById(equipoId);
        return elec.orElse(null);
    }

    // CORRECCIÓN PROBLEMA 2: Se reemplaza deleteById() por findById().ifPresent(repo::delete).
    //
    // El problema original era que deleteById() en Spring Data JPA lanza
    // EmptyResultDataAccessException si el registro no existe, causando un 500.
    // Un equipo solo puede ser INSTRUMENTO o ELECTRONICO, nunca ambos, por lo tanto
    // siempre uno de los dos repositorios no va a encontrar nada.
    //
    // Con ifPresent(), si no existe el registro simplemente no hace nada,
    // en vez de explotar con una excepción.
    public void eliminarPorEquipoId(Long equipoId) {
        instrumentoRepository.findById(equipoId)
                .ifPresent(instrumentoRepository::delete);

        electronicaRepository.findById(equipoId)
                .ifPresent(electronicaRepository::delete);
    }

    public Optional<EspecificacionInstrumento> obtenerInstrumentoPorId(Long equipoId) {
        return instrumentoRepository.findById(equipoId);
    }

    public Optional<EspecificacionElectronica> obtenerElectronicaPorId(Long equipoId) {
        return electronicaRepository.findById(equipoId);
    }
}
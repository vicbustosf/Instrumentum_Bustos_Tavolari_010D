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

    @PostConstruct
    public void cargarDatosPrueba() {
        if (instrumentoRepository.count() > 0) return;

        
        instrumentoRepository.save(new EspecificacionInstrumento(1L, "Aliso", "HSS", "010"));
        instrumentoRepository.save(new EspecificacionInstrumento(2L, "Caoba", "HH", "011"));
        electronicaRepository.save(new EspecificacionElectronica(3L, "9V", 15.0, "Distortion"));

     
        instrumentoRepository.save(new EspecificacionInstrumento(4L, "Aliso", "PJ", "045"));
        electronicaRepository.save(new EspecificacionElectronica(5L, "220V", 100.0, "Valvular"));
    }


    private void validarEquipo(Long equipoId) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/v1/equipos/" + equipoId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public EspecificacionInstrumento guardarInstrumento(Long equipoId, EspecificacionInstrumento espec) {
        validarEquipo(equipoId);
        espec.setIdEquipo(equipoId);
        return instrumentoRepository.save(espec);
    }

    public EspecificacionElectronica guardarElectronica(Long equipoId, EspecificacionElectronica espec) {
        validarEquipo(equipoId);
        espec.setIdEquipo(equipoId);
        return electronicaRepository.save(espec);
    }

    public Object obtenerEspecificacionPorEquipo(Long equipoId) {
        Optional<EspecificacionInstrumento> inst = instrumentoRepository.findById(equipoId);
        if (inst.isPresent()){ 
            return inst.get();
        }
        Optional<EspecificacionElectronica> elec = electronicaRepository.findById(equipoId);
        return elec.orElse(null);
    }

    public void eliminarPorEquipoId(Long equipoId) {
        instrumentoRepository.deleteById(equipoId);
        electronicaRepository.deleteById(equipoId);
    }

    

    public Optional<EspecificacionInstrumento> obtenerInstrumentoPorId(Long equipoId) {
        return instrumentoRepository.findById(equipoId);
    }

    public Optional<EspecificacionElectronica> obtenerElectronicaPorId(Long equipoId) {
        return electronicaRepository.findById(equipoId);
    }
}
package cl.instrumentum.service_gira.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import cl.instrumentum.service_gira.model.Gira;
import cl.instrumentum.service_gira.model.ParadaGira;
import cl.instrumentum.service_gira.repository.GiraRepository;
import cl.instrumentum.service_gira.repository.ParadaGiraRepository;

@Service
public class GiraService {

    @Autowired
    private GiraRepository giraRepository;

    @Autowired
    private ParadaGiraRepository paradaRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;


    //        VALIDACIONES WEBCLIENT       


    // Valida que la banda exista en service-usuario (Asumiendo puerto 8081)
    private void validarBanda(Long idBanda) {
        webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/api/v2/bandas/" + idBanda)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // Valida que el evento exista en service-evento (Asumiendo puerto 8086, ajusta si es otro)
    private void validarEvento(Long idEvento) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8086/api/v2/eventos/" + idEvento)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            // Captura el error en caso de que service-evento no responda o devuelva 404
            throw new RuntimeException("El evento con ID " + idEvento + " no existe o service-evento no responde.");
        }
    }

    //       GESTIÓN DE GIRAS   
    

    public List<Gira> listarGiras() {
        return giraRepository.findAll();
    }

    public List<Gira> listarPorBanda(Long idBanda) {
        return giraRepository.findByIdBanda(idBanda);
    }

    public Optional<Gira> buscarGiraPorId(Long id) {
        return giraRepository.findById(id);
    }

    public Gira guardarGira(Gira gira) {
        validarBanda(gira.getIdBanda());
        return giraRepository.save(gira);
    }

    public Gira actualizarGira(Long id, Gira giraActualizada) {
        return giraRepository.findById(id)
                .map(gira -> {
                    validarBanda(giraActualizada.getIdBanda()); // Valida por si cambiaron la banda
                    gira.setIdBanda(giraActualizada.getIdBanda());
                    gira.setNombreGira(giraActualizada.getNombreGira());
                    gira.setFechaInicio(giraActualizada.getFechaInicio());
                    gira.setFechaFin(giraActualizada.getFechaFin());
                    return giraRepository.save(gira);
                })
                .orElse(null); // El controlador lo envolverá en Optional.ofNullable()
    }

    // @Transactional asegura que si falla el borrado de paradas, no se borre la gira a medias
    @Transactional
    public boolean eliminarGira(Long id) {
        if (giraRepository.existsById(id)) {
            paradaRepository.deleteByIdGira(id); // Limpia las paradas asociadas primero
            giraRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 
    //      GESTIÓN DE PARADAS    
    // 

    public List<ParadaGira> listarParadasPorGira(Long idGira) {
        return paradaRepository.findByIdGira(idGira);
    }

    public Optional<ParadaGira> buscarParadaPorId(Long id) {
        return paradaRepository.findById(id);
    }

   public ParadaGira guardarParada(ParadaGira parada) {
        // 1. Validamos que el objeto Gira asociado no venga nulo y tenga un ID válido
        if (parada.getGira() == null || parada.getGira().getIdGira() == null) {
            throw new RuntimeException("El ID de la gira es obligatorio para registrar la parada.");
        }

        Long idGira = parada.getGira().getIdGira();

        // 2. Buscamos la Gira real en la base de datos para establecer el vínculo físico obligatorio
        Gira giraExistente = giraRepository.findById(idGira)
                .orElseThrow(() -> new RuntimeException("La gira con ID " + idGira + " no existe."));

        // 3. Validamos de forma lógica que el evento externo exista vía WebClient
        validarEvento(parada.getIdEvento());

        // 4. Seteamos la entidad gestionada por JPA a la parada para que se guarde la Foreign Key correctamente
        parada.setGira(giraExistente);

        return paradaRepository.save(parada);
    }

    public ParadaGira actualizarParada(Long id, ParadaGira paradaActualizada) {
        return paradaRepository.findById(id).map(parada -> {
            // 1. Validamos que los datos de la Gira en la actualización sean correctos
            if (paradaActualizada.getGira() == null || paradaActualizada.getGira().getIdGira() == null) {
                    throw new RuntimeException("El ID de la gira es obligatorio para actualizar la parada.");
                    }

            Long idGiraNueva = paradaActualizada.getGira().getIdGira();

            // 2. Si cambiaron la parada a otra Gira distinta, buscamos la nueva Gira para verificar que exista
                Gira giraNueva = giraRepository.findById(idGiraNueva)
                    .orElseThrow(() -> new RuntimeException("La gira con ID " + idGiraNueva + " no existe."));

            // 3. Validamos que el evento externo siga existiendo (por si fue modificado)
            validarEvento(paradaActualizada.getIdEvento());

            // 4. Actualizamos los campos utilizando la nueva estructura de objetos
                    parada.setGira(giraNueva); // Reemplaza al antiguo .setIdGira()
                    parada.setIdEvento(paradaActualizada.getIdEvento());
                    parada.setCiudad(paradaActualizada.getCiudad());
                    parada.setAlojamiento(paradaActualizada.getAlojamiento());
                    parada.setTransporte(paradaActualizada.getTransporte());

                    return paradaRepository.save(parada);
                })
                .orElse(null);
    }

    public boolean eliminarParada(Long id) {
        if (paradaRepository.existsById(id)) {
            paradaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
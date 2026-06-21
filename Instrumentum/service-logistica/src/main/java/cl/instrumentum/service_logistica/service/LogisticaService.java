package cl.instrumentum.service_logistica.service;

import cl.instrumentum.service_logistica.model.Contenedor;
import cl.instrumentum.service_logistica.model.ContenedorEquipo;
import cl.instrumentum.service_logistica.repository.ContenedorEquipoRepository;
import cl.instrumentum.service_logistica.repository.ContenedorRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LogisticaService {

    @Autowired
    private ContenedorRepository contenedorRepository;

    @Autowired
    private ContenedorEquipoRepository contenedorEquipoRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;


// ---------------- Validar banda y equipo ---------------- \\

    private void validarBanda(Long idBanda) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/v2/bandas/" + idBanda)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.NotFound ex) {
            throw new RuntimeException("Error: Banda no encontrada");
        } catch (Exception ex) {
            throw new RuntimeException("Error: No se pudo validar la banda");
        }
    }

    public void validarEquipo(Long idEquipo) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/api/v2/equipos/" + idEquipo)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.NotFound ex) {
            throw new RuntimeException("Error: Equipo no encontrado");
        } catch (Exception ex) {
            throw new RuntimeException("Error: No se pudo validar el equipo");
        }
    }

// ---------------- CRUD Contenedores ---------------- \\

// Crear contenedor
    public Contenedor crearContenedor(Contenedor contenedor) {
        if (contenedor.getIdBanda() == null)
            throw new RuntimeException("Error: El ID de banda es obligatorio para crear un contenedor");
        validarBanda(contenedor.getIdBanda());
        if (contenedor.getPeso() == null) contenedor.setPeso(0.0);
        return contenedorRepository.save(contenedor);
    }

// Listar todos los contendores
    public List<Contenedor> listarTodosContenedores() {
        return contenedorRepository.findAll();
    }

// Listar contendedor por id
    public Optional<Contenedor> buscarPorId(Long id) {
        return contenedorRepository.findById(id);
    }

// Listar contenedor por banda id
    public List<Contenedor> listarPorBanda(Long idBanda) {
        validarBanda(idBanda);
        return contenedorRepository.findByIdBanda(idBanda);
    }

// Actualizar contenedor
    public Contenedor actualizarContenedor(Contenedor contenedor) {
        return contenedorRepository.save(contenedor);
    }

// Eliminar contenedor
    @Transactional
    public void eliminarContenedor(Long id) {
        Contenedor con = contenedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Contenedor no encontrado"));
        contenedorRepository.delete(con);
    }

// ---------------- CRUD Equipos en contenedores ---------------- \\

// Agregar equipo a un contenedor
    @Transactional
    public ContenedorEquipo agregarEquipo(Long contenedorId, Long idEquipo) {
        Contenedor c = contenedorRepository.findById(contenedorId)
                .orElseThrow(() -> new RuntimeException("Error: Contenedor no encontrado"));
        validarEquipo(idEquipo);
        return contenedorEquipoRepository.save(new ContenedorEquipo(null, c, idEquipo));
    }

// Listar todos los equipos en contenedores
    public List<ContenedorEquipo> listarTodosLosEquiposEnContenedores() {
        return contenedorEquipoRepository.findAll();
    }


// Listar equipos de un contenedor
    public List<ContenedorEquipo> listarEquiposDeContenedor(Long contenedorId) {
        Contenedor con = contenedorRepository.findById(contenedorId)
                .orElseThrow(() -> new RuntimeException("Error: Contenedor no encontrado"));

        List<ContenedorEquipo> equipos = contenedorEquipoRepository.findByContenedor(con);
        if (equipos.isEmpty()) {
            throw new RuntimeException("Error: Equipo no existe");
        }
        return equipos;
    }

// Remover un equipo de un contenedor
    @Transactional
    public void removerEquipo(Long contenedorId, Long idEquipo) {
        Contenedor c = contenedorRepository.findById(contenedorId)
                .orElseThrow(() -> new RuntimeException("Error: Contenedor no encontrado"));
        ContenedorEquipo conE = contenedorEquipoRepository
                .findByContenedorAndIdEquipo(c, idEquipo)
                .orElseThrow(() -> new RuntimeException("Error: Equipo no ese en este contenedor"));
        contenedorEquipoRepository.delete(conE);
    }
}
// Agregarle (quizá) un actualizar equipo en contenedor. Igual no creo porque se puede quitar y agregar otro.
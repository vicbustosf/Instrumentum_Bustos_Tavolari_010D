package cl.instrumentum.service_inventario.service;
 
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import cl.instrumentum.service_inventario.model.*;
import cl.instrumentum.service_inventario.repository.*;
import jakarta.annotation.PostConstruct;
 
@Service
public class InventarioService {
 
    @Autowired
    private EquipoRepository equipoRepository;
 
    @Autowired
    private MarcaRepository marcaRepository;
 
    @Autowired
    private CategoriaRepository categoriaRepository;
 
    @Autowired
    private WebClient.Builder webClientBuilder;
 
 
    public Marca guardarMarca(Marca marca) {
        return marcaRepository.save(marca);
    }
 
    public List<Marca> listarMarcas() {
        return marcaRepository.findAll();
    }
 
    public List<Equipo> listarTodos() {
        return equipoRepository.findAll();
    }
 
    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
 
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }
 
    private void validarPropietario(Long propietarioId, String tipoPropietario) {

        String path;

        if ("USUARIO".equals(tipoPropietario)) {
            path = "/api/v2/usuarios/{id}";
        } else if ("BANDA".equals(tipoPropietario)) {
            path = "/api/v2/bandas/{id}";
        } else {
            throw new RuntimeException("Tipo propietario inválido");
        }

        webClientBuilder.build()
            .get()
            .uri(uriBuilder -> uriBuilder
                .scheme("http")
                .host("localhost")
                .port(8081)
                .path(path)
                .build(propietarioId)
            )
            .retrieve()
            .toBodilessEntity()
            .block();
    }
 
    public Optional<Marca> obtenerMarcaPorId(Long id) {
        return marcaRepository.findById(id);
    }
 
    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }
 
    public Equipo guardarEquipo(Equipo equipo) {
        if (equipo.getId() != null && equipoRepository.findById(equipo.getId()).isEmpty()) 
            {
            throw new RuntimeException();
        }
        Optional<Equipo> existing = equipoRepository.findByNombre(equipo.getNombre());
        if (existing.isPresent() && !existing.get().getId().equals(equipo.getId())) 
            {
            throw new RuntimeException();
        }
        validarPropietario(equipo.getPropietarioId(), equipo.getTipoPropietario());
        return equipoRepository.save(equipo);
    }
 
    public List<Equipo> listarEquiposPorPropietario(Long propietarioId) {
        return equipoRepository.findByPropietarioId(propietarioId);
    }
 
    public List<Equipo> buscarEquipos(String nombre, String marca, String categoria) {
        return equipoRepository.buscarConFiltrosExactos(nombre, marca, categoria);
    }
 
    public Optional<Equipo> obtenerEquipoPorId(Long id) {
        return equipoRepository.findById(id);
    }
    
    //AÑADIR MENSAJE DE CONFIRMACION
    public void eliminarMarca(Long id) {
        marcaRepository.deleteById(id);
    }

    //AÑADIR MENSAJE DE CONFIRMACION
    public void eliminarCategoria(Long id) {
        categoriaRepository.deleteById(id);
    }

     //AÑADIR MENSAJE DE CONFIRMACION
    public void eliminarEquipo(Long id) {
        equipoRepository.findById(id).orElseThrow(RuntimeException::new);
 
        Boolean enCancion = webClientBuilder.build()
                .get()
                .uri("http://localhost:8085/api/v2/equipos/en-cancion/" + id)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
 
        if (Boolean.TRUE.equals(enCancion)) {
            throw new RuntimeException();
        }
 
        // URL corregida de specs a especs para que coincida con EspecsController
        try {
            webClientBuilder.build()
                    .delete()
                    .uri("http://localhost:8083/api/v2/especs/equipo/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {}
 
        try {
            webClientBuilder.build()
                    .delete()
                    .uri("http://localhost:8084/api/v2/mantenimientos/equipo/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {}
 
        equipoRepository.deleteById(id);
    }
}
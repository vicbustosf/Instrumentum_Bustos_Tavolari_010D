package cl.instrumentum.service_inventario.service;
 
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException; // Nativa para 404
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import cl.instrumentum.service_inventario.model.*;
import cl.instrumentum.service_inventario.repository.*;
import lombok.extern.slf4j.Slf4j; // SLF4J para logs del sistema
// SLF4J se utiliza para registrar eventos importantes, 
// especialmente errores de comunicación entre servicios, 
// sin interrumpir el flujo normal de la aplicación. 
@Slf4j
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
            throw new IllegalArgumentException("Tipo propietario inválido: " + tipoPropietario);
        }

        try {
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
        } catch (Exception e) {
            log.error("Fallo de comunicación o validación con service-user para propietario ID {}", propietarioId);
            throw new IllegalArgumentException("El propietario con ID " + propietarioId + " (" + tipoPropietario + ") no existe o el servicio no está disponible.");
        }
    }
 
    public Optional<Marca> obtenerMarcaPorId(Long id) {
        return marcaRepository.findById(id);
    }
 
    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }
 
    public Equipo guardarEquipo(Equipo equipo) {
        //Validación de existencia previa en la Base de Datos para actualizaciones de equipos
        if (equipo.getId() != null && equipoRepository.findById(equipo.getId()).isEmpty()) {
            throw new NoSuchElementException("No se puede actualizar un equipo que no existe en la base de datos.");
        }
        // Validación de nombres duplicados
        Optional<Equipo> existing = equipoRepository.findByNombre(equipo.getNombre());
        if (existing.isPresent() && !existing.get().getId().equals(equipo.getId())) {
            throw new IllegalArgumentException("Ya existe un equipo registrado con el nombre: " + equipo.getNombre());
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
    
    public void eliminarMarca(Long id) {
        if (!marcaRepository.existsById(id)) {
            throw new NoSuchElementException("No existe una marca con ID " + id + ".");
        }
        if (!equipoRepository.findByMarca_Id(id).isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar la marca con ID " + id + " porque tiene equipos asociados.");
        }
        marcaRepository.deleteById(id);
    }

    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new NoSuchElementException("No existe una categoría con ID " + id + ".");
        }
        if (!equipoRepository.findByCategoria_Id(id).isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar la categoría con ID " + id + " porque tiene equipos asociados.");
        }
        categoriaRepository.deleteById(id);
    }

    public void eliminarEquipo(Long id) {
        equipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un equipo con ID " + id + "."));

        Boolean enCancion = false;
        try {
            enCancion = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8085/api/v2/equipos/en-cancion/" + id)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al conectar con service-rig para verificar equipo {}: {}", id, e.getMessage());
            throw new IllegalArgumentException("No se pudo verificar si el equipo está en uso por fallas en el módulo de Rigs.");
        }
        
        if (Boolean.TRUE.equals(enCancion)) {
            throw new IllegalArgumentException("No se puede eliminar el equipo porque está asignado en una o más canciones activas.");
        }
 
        // Eliminaciones en cascada lógicas 
        try {
            webClientBuilder.build()
                    .delete()
                    .uri("http://localhost:8083/api/v2/especs/equipo/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("INTEGRIDAD AFECTADA: No se eliminaron las especificaciones del equipo {} en specs. Motivo: {}", id, e.getMessage());
        }
 
        try {
            webClientBuilder.build()
                    .delete()
                    .uri("http://localhost:8084/api/v2/mantenimientos/equipo/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("INTEGRIDAD AFECTADA: No se eliminaron los mantenimientos del equipo {} en mantenimiento. Motivo: {}", id, e.getMessage());
        }

        // =========================================================
        // Notificar a Logística para eliminar de contenedores
        // =========================================================
        try {
            webClientBuilder.build()
                    .delete()
                    .uri("http://localhost:8090/api/v2/logistica/equipos/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("INTEGRIDAD AFECTADA: No se eliminó el equipo {} de los contenedores de logística. Motivo: {}", id, e.getMessage());
        }
        // =========================================================
 
        equipoRepository.deleteById(id);
    }
}
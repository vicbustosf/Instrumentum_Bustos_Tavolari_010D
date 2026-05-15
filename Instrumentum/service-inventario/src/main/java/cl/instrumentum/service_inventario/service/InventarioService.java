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
 
    @PostConstruct
    public void cargarDatosPrueba() {
        if (marcaRepository.count() > 0) return;

        Marca fender = marcaRepository.save(new Marca(null, "Fender"));
        Marca gibson = marcaRepository.save(new Marca(null, "Gibson"));
        Marca boss = marcaRepository.save(new Marca(null, "Boss"));
        Marca marshall = marcaRepository.save(new Marca(null, "Marshall"));

        Categoria guitarra = categoriaRepository.save(new Categoria(null, "Guitarra"));
        Categoria pedal = categoriaRepository.save(new Categoria(null, "Pedal"));
        Categoria amplificador = categoriaRepository.save(new Categoria(null, "Amplificador"));

        // Equipos seed originales
        equipoRepository.save(new Equipo(null, "Stratocaster", "USA", fender, guitarra, 1L, "USUARIO", "INSTRUMENTO"));
        equipoRepository.save(new Equipo(null, "Les Paul", "Standard", gibson, guitarra, 1L, "USUARIO", "INSTRUMENTO"));
        equipoRepository.save(new Equipo(null, "DS-1", "Distortion", boss, pedal, 2L, "BANDA", "ELECTRONICO"));

        // Equipos nuevos
        equipoRepository.save(new Equipo(null, "Precision Bass", "Player Series", gibson, guitarra, 2L, "USUARIO", "INSTRUMENTO"));
        equipoRepository.save(new Equipo(null, "JCM800", "2203", marshall, amplificador, 1L, "BANDA", "ELECTRONICO"));
    }
 
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
        try {
            String url;
            if ("USUARIO".equals(tipoPropietario)) {
                url = "http://localhost:8081/api/v1/usuarios/" + propietarioId;
            } else if ("BANDA".equals(tipoPropietario)) {
                url = "http://localhost:8081/api/v1/bandas/" + propietarioId;
            } else {
                throw new RuntimeException();
            }
            webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response -> {
                    throw new RuntimeException();
                })
                .toBodilessEntity()
                .block();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Optional<Marca> obtenerMarcaPorId(Long id) {
        return marcaRepository.findById(id);
    }

    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }
 
    public Equipo guardarEquipo(Equipo equipo) {
        if (equipo.getId() != null && equipoRepository.findById(equipo.getId()).isEmpty()) {
            throw new RuntimeException();
        }
        Optional<Equipo> existing = equipoRepository.findByNombre(equipo.getNombre());
        if (existing.isPresent() && !existing.get().getId().equals(equipo.getId())) {
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
 
    public void eliminarEquipo(Long id) {
        equipoRepository.findById(id).orElseThrow(RuntimeException::new);
 
        // FIX: ahora el endpoint devuelve boolean directo, bodyToMono(Boolean.class) funciona correctamente
        Boolean enCancion = webClientBuilder.build()
                .get()
                .uri("http://localhost:8085/api/v1/equipos/en-cancion/" + id)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
 
        if (Boolean.TRUE.equals(enCancion)) {
            throw new RuntimeException();
        }
 
        try {
            webClientBuilder.build()
                    .delete()
                    .uri("http://localhost:8083/api/v1/specs/equipo/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {}
 
        try {
            webClientBuilder.build()
                    .delete()
                    .uri("http://localhost:8084/api/v1/mantenimientos/equipo/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {}
 
        equipoRepository.deleteById(id);
    }
<<<<<<< HEAD
}
=======
}


/*
El DELETE tambien da 500 internal Server Error,
Los dos POST de specs me fallaron con un  400 bad request,
El delete de matenimiento da 500 Internal Server Err,
Este en mantenimieto "POST http://localhost:8085/api/v1/canciones/2/equipos" funciona (200) pero la respuesta es muy larga de 3667 lineas.
                     "PUT http://localhost:8085/api/v1/canciones/2/equipos/1" funciona (200) pero la respuesta es muy larga de 3667 lineas.
                     "GET http://localhost:8085/api/v1/canciones/2/setup-completo" Funciona (200) pero la respuesta es muy larga de 3667 lineas.
*/

//yA NOOOO - oSCAR
>>>>>>> mi-respaldo

package cl.instrumentum.service_usuario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import cl.instrumentum.service_usuario.model.Banda;
import cl.instrumentum.service_usuario.model.Usuario;
import cl.instrumentum.service_usuario.repository.BandaRepository;
import cl.instrumentum.service_usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BandaRepository bandaRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getBanda() != null && usuario.getBanda().getIdBanda() != null) {
            Banda banda = bandaRepository.findById(usuario.getBanda().getIdBanda())
                    .orElseThrow(() -> new RuntimeException("La banda no existe."));
            usuario.setBanda(banda);
        }
        return usuarioRepository.save(usuario);
    }

    // MODIFICADO: Retorna boolean verificando existencia
    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Usuario> usuariosPorBanda(Long idBanda) {
        return usuarioRepository.findAllByBandaId(idBanda);
    }

    public List<Banda> listarBandas() {
        return bandaRepository.findAll();
    }

    public Optional<Banda> buscarBandaPorId(Long id) {
        return bandaRepository.findById(id);
    }

    public Banda guardarBanda(Banda banda) {
        return bandaRepository.save(banda);
    }

    // MODIFICACION 1: Retorna boolean verificando existencia
    // MODIFICACION 2: Verifica usuarios asociados antes de eliminar
    public boolean eliminarBanda(Long id) {
        if (!bandaRepository.existsById(id)) {
            return false;
        }
            
        // 1. Regla de negocio: No borrar si hay usuarios adentro
        List<Usuario> usuariosAsociados = usuarioRepository.findAllByBandaId(id);
        if (!usuariosAsociados.isEmpty()) {
            throw new RuntimeException("No se puede eliminar la banda porque tiene " + usuariosAsociados.size() + " usuario(s) vinculado(s).");
        }

        // 2. Lista para rastrear qué microservicios fallaron
        List<String> microserviciosFallidos = new ArrayList<>();

        // ==========================================================
        // LIMPIEZA MANEJADA CON WEBCLIENT 
        // ==========================================================
        
        try {
            webClientBuilder.build().delete()
                .uri("http://localhost:8086/api/v2/eventos/banda/" + id)
                .retrieve().toBodilessEntity().block();
        } catch (Exception e) {
            microserviciosFallidos.add("Eventos");
        }

        try {
            webClientBuilder.build().delete()
                .uri("http://localhost:8087/api/v2/finanzas/banda/" + id)
                .retrieve().toBodilessEntity().block();
        } catch (Exception e) {
            microserviciosFallidos.add("Finanzas");
        }

        try {
            webClientBuilder.build().delete()
                .uri("http://localhost:8088/api/v2/giras/banda/" + id)
                .retrieve().toBodilessEntity().block();
        } catch (Exception e) {
            microserviciosFallidos.add("Giras");
        }

        try {
            webClientBuilder.build().delete()
                .uri("http://localhost:8090/api/v2/logistica/banda/" + id)
                .retrieve().toBodilessEntity().block();
        } catch (Exception e) {
            microserviciosFallidos.add("Logística");
        }

        try {
            webClientBuilder.build().delete()
                .uri("http://localhost:8091/api/v2/merchandising/banda/" + id)
                .retrieve().toBodilessEntity().block();
        } catch (Exception e) {
            microserviciosFallidos.add("Merchandising");
        }

        try {
            webClientBuilder.build().delete()
                .uri("http://localhost:8085/api/v2/canciones/banda/" + id)
                .retrieve().toBodilessEntity().block();
        } catch (Exception e) {
            microserviciosFallidos.add("Rig (Canciones)");
        }
        
        // ==========================================================
        // 3. Evaluación Final de Errores
        // ==========================================================
        
        if (!microserviciosFallidos.isEmpty()) {
            throw new RuntimeException("No se puede eliminar la banda. Falló la conexión o limpieza en los siguientes módulos: " 
                + String.join(", ", microserviciosFallidos));
        }

        // 4. Paso Final: Borrar la banda localmente SOLO si todos los microservicios confirmaron la limpieza
        bandaRepository.deleteById(id);
        return true;
    }
}



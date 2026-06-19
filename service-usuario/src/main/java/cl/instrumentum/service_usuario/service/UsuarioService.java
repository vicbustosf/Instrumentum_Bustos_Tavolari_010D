package cl.instrumentum.service_usuario.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        if (bandaRepository.existsById(id)) {
            
            // 1. Verificamos si hay usuarios que pertenezcan a esta banda
            List<Usuario> usuariosAsociados = usuarioRepository.findAllByBandaId(id);
            
            // 2. Si la lista no está vacía, frenamos la eliminación con una excepción clara
            if (!usuariosAsociados.isEmpty()) {
                throw new RuntimeException("Conflicto de integridad: No se puede eliminar la banda con ID " 
                        + id + " porque tiene " + usuariosAsociados.size() + " usuario(s) vinculado(s).");
            }
            
            // 3. Si está limpia, procedemos a borrarla
            bandaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
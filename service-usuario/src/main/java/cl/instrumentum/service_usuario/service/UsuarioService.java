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
        return usuarioRepository.findByIdBanda(idBanda);
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

    // MODIFICADO: Retorna boolean verificando existencia
    public boolean eliminarBanda(Long id) {
        if (bandaRepository.existsById(id)) {
            bandaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
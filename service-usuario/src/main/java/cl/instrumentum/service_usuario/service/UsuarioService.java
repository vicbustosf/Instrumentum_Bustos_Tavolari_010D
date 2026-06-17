package cl.instrumentum.service_usuario.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cl.instrumentum.service_usuario.model.Banda;
import cl.instrumentum.service_usuario.model.Usuario;
import cl.instrumentum.service_usuario.repository.BandaRepository;
import cl.instrumentum.service_usuario.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;

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

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
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

    public void eliminarBanda(Long id) {
        bandaRepository.deleteById(id);
    }
}
package cl.instrumentum.service_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.instrumentum.service_auth.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}
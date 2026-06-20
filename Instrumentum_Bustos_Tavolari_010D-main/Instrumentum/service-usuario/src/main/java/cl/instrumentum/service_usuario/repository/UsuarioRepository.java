package cl.instrumentum.service_usuario.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import cl.instrumentum.service_usuario.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByUsername(String username);

    @Query("""
        SELECT u
        FROM Usuario u
        WHERE u.banda.idBanda = :idBanda
    """)
    List<Usuario> findAllByBandaId(@Param("idBanda") Long idBanda);
}
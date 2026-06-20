package cl.instrumentum.service_auth.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import cl.instrumentum.service_auth.repository.UsuarioRepository;
import cl.instrumentum.service_auth.model.Rol;
import cl.instrumentum.service_auth.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired 
    private UsuarioRepository usuarioRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public String registrar(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuarioRepo.save(usuario);
        return "Usuario registrado";
    }

    public String login(String username, String password){
        Usuario user = usuarioRepo.findByNombreUsuario(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(passwordEncoder.matches(password, user.getContrasena())) {
            List<String> roles = user.getRoles().stream()
                .map(Rol::getNombreRol).collect(Collectors.toList());
            return jwtService.generarToken(username, roles);
        }
        throw new RuntimeException("Credenciales inválidas");
    }
}
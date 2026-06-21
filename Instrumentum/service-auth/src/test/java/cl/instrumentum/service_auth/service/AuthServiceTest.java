package cl.instrumentum.service_auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import cl.instrumentum.service_auth.model.Rol;
import cl.instrumentum.service_auth.model.Usuario;
import cl.instrumentum.service_auth.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    public void registrarUsuarioTest() {
        Usuario usuarioInput = new Usuario();
        usuarioInput.setNombreUsuario("diego.valdes");
        usuarioInput.setContrasena("rawPassword123");
        usuarioInput.setCorreo("diego@instrumentum.cl");

        String passwordEncriptada = "$2a$10$EncryptedPasswordStringHere";
        
        when(passwordEncoder.encode("rawPassword123")).thenReturn(passwordEncriptada);
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioInput);

        String resultado = authService.registrar(usuarioInput);

        assertEquals("Usuario registrado", resultado);
        assertEquals(passwordEncriptada, usuarioInput.getContrasena());
        verify(passwordEncoder).encode("rawPassword123");
        verify(usuarioRepo).save(usuarioInput);
    }

    @Test
    public void loginUsuarioTest() {
        String nombreUsuario = "diego.valdes";
        String contrasenaPlana = "rawPassword123";
        String contrasenaEncriptada = "$2a$10$EncryptedPasswordStringHere";
        String tokenEsperado = "eyJhbGciOiJIUzI1NiJ9.mockTokenJWT";

        Rol rolUser = new Rol();
        rolUser.setId(1L);
        rolUser.setNombreRol("Músico");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setNombreUsuario(nombreUsuario);
        usuarioExistente.setContrasena(contrasenaEncriptada);
        usuarioExistente.setRoles(Set.of(rolUser));

        List<String> rolesEsperados = List.of("Músico");

        when(usuarioRepo.findByNombreUsuario(nombreUsuario)).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.matches(contrasenaPlana, contrasenaEncriptada)).thenReturn(true);
        when(jwtService.generarToken(nombreUsuario, rolesEsperados)).thenReturn(tokenEsperado);

        String tokenObtenido = authService.login(nombreUsuario, contrasenaPlana);

        assertNotNull(tokenObtenido);
        assertEquals(tokenEsperado, tokenObtenido);
        verify(usuarioRepo).findByNombreUsuario(nombreUsuario);
        verify(passwordEncoder).matches(contrasenaPlana, contrasenaEncriptada);
        verify(jwtService).generarToken(nombreUsuario, rolesEsperados);
    }
}
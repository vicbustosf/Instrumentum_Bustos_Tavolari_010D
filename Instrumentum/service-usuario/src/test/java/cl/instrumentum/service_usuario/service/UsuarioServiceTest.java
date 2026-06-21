package cl.instrumentum.service_usuario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import cl.instrumentum.service_usuario.model.Banda;
import cl.instrumentum.service_usuario.model.Usuario;
import cl.instrumentum.service_usuario.repository.BandaRepository;
import cl.instrumentum.service_usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BandaRepository bandaRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Banda bandaMock;
    private Usuario usuarioMock;

    @BeforeEach
    public void setUp() {
        bandaMock = new Banda(1L, "Los Prisioneros", LocalDate.now());
        usuarioMock = new Usuario(10L, "jorge_gonzalez", "jorge@instrumentum.cl", "Musico", bandaMock);
    }

    @Test
    public void listarUsuariosTest() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioMock));

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("jorge_gonzalez", resultado.get(0).getUsername());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    public void buscarUsuarioPorIdTest() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioMock));

        Optional<Usuario> resultado = usuarioService.buscarUsuarioPorId(10L);

        assertTrue(resultado.isPresent());
        assertEquals(10L, resultado.get().getIdUser());
        assertEquals("jorge_gonzalez", resultado.get().getUsername());
        verify(usuarioRepository, times(1)).findById(10L);
    }

    @Test
    public void crearUsuarioTest() {
        when(bandaRepository.findById(1L)).thenReturn(Optional.of(bandaMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        Usuario resultado = usuarioService.guardarUsuario(usuarioMock);

        assertNotNull(resultado);
        assertEquals("jorge_gonzalez", resultado.getUsername());
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }

    @Test
    public void eliminarUsuarioTest() {
        when(usuarioRepository.existsById(10L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(10L);

        boolean resultado = usuarioService.eliminarUsuario(10L);

        assertTrue(resultado);
        verify(usuarioRepository, times(1)).deleteById(10L);
    }

    @Test
    public void buscarUsuariosPorBandaTest() {
        when(usuarioRepository.findAllByBandaId(1L)).thenReturn(List.of(usuarioMock));

        List<Usuario> resultado = usuarioService.usuariosPorBanda(1L);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1L, resultado.get(0).getBanda().getIdBanda());
        verify(usuarioRepository, times(1)).findAllByBandaId(1L);
    }
}
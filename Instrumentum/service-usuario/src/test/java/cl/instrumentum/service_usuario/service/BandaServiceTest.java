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
import cl.instrumentum.service_usuario.repository.BandaRepository;

@ExtendWith(MockitoExtension.class)
public class BandaServiceTest {

    @Mock
    private BandaRepository bandaRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Banda bandaMock;

    @BeforeEach
    public void setUp() {
        bandaMock = new Banda(1L, "Los Prisioneros", LocalDate.now());
    }

    @Test
    public void listarBandasTest() {
        when(bandaRepository.findAll()).thenReturn(List.of(bandaMock));

        List<Banda> resultado = usuarioService.listarBandas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Los Prisioneros", resultado.get(0).getNombre());
        verify(bandaRepository, times(1)).findAll();
    }

    @Test
    public void buscarBandaPorIdTest() {
        when(bandaRepository.findById(1L)).thenReturn(Optional.of(bandaMock));

        Optional<Banda> resultado = usuarioService.buscarBandaPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdBanda());
        assertEquals("Los Prisioneros", resultado.get().getNombre());
        verify(bandaRepository, times(1)).findById(1L);
    }

    @Test
    public void crearBandaTest() {
        when(bandaRepository.save(any(Banda.class))).thenReturn(bandaMock);

        Banda resultado = usuarioService.guardarBanda(bandaMock);

        assertNotNull(resultado);
        assertEquals("Los Prisioneros", resultado.getNombre());
        verify(bandaRepository, times(1)).save(bandaMock);
    }

    @Test
    public void actualizarBandaTest() {
        when(bandaRepository.save(any(Banda.class))).thenReturn(bandaMock);

        Banda resultado = usuarioService.guardarBanda(bandaMock);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdBanda());
        verify(bandaRepository, times(1)).save(bandaMock);
    }
}
package cl.instrumentum.service_rig.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.instrumentum.service_rig.model.Cancion;
import cl.instrumentum.service_rig.repository.CancionRepository;

@ExtendWith(MockitoExtension.class)
public class CancionServiceTest {

    @Mock
    private CancionRepository cancionRepository;

    @InjectMocks
    private RigService rigService;

    @Test
    public void listarCancionesTest() {
        Long bandaId = 1L;
        List<Cancion> cancionesSimuladas = List.of(
            new Cancion(1L, "Cancion A", bandaId, 180, new ArrayList<>()),
            new Cancion(2L, "Cancion B", bandaId, 240, new ArrayList<>())
        );
        when(cancionRepository.findByBandaId(bandaId)).thenReturn(cancionesSimuladas);

        List<Cancion> resultado = rigService.listarCancionesPorBanda(bandaId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Cancion A", resultado.get(0).getNombre());
        verify(cancionRepository, times(1)).findByBandaId(bandaId);
    }

    @Test
    public void buscarCancionPorIdTest() {
        Long cancionId = 1L;
        Cancion cancionSimulada = new Cancion(cancionId, "Cancion A", 1L, 180, new ArrayList<>());
        when(cancionRepository.findById(cancionId)).thenReturn(Optional.of(cancionSimulada));

        Optional<Cancion> resultado = rigService.buscarCancionPorId(cancionId);

        assertTrue(resultado.isPresent());
        assertEquals("Cancion A", resultado.get().getNombre());
        verify(cancionRepository, times(1)).findById(cancionId);
    }

    @Test
    public void eliminarCancionTest() {
        Long cancionId = 1L;
        when(cancionRepository.existsById(cancionId)).thenReturn(true);
        doNothing().when(cancionRepository).deleteById(cancionId);

        boolean resultado = rigService.eliminarCancion(cancionId);

        assertTrue(resultado);
        verify(cancionRepository, times(1)).existsById(cancionId);
        verify(cancionRepository, times(1)).deleteById(cancionId);
    }
}
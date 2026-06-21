package cl.instrumentum.service_inventario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.instrumentum.service_inventario.model.Marca;
import cl.instrumentum.service_inventario.repository.EquipoRepository;
import cl.instrumentum.service_inventario.repository.MarcaRepository;

@ExtendWith(MockitoExtension.class)
class MarcaServiceTest {

    @Mock
    private MarcaRepository marcaRepository;

    @Mock
    private EquipoRepository equipoRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Marca marcaSample;

    @BeforeEach
    void setUp() {
        // CORRECCIÓN: Se cambió '1Long' por '1L'
        marcaSample = new Marca(1L, "Gibson");
    }

    @Test
    void crearMarcaTest() {
        when(marcaRepository.save(any(Marca.class))).thenReturn(marcaSample);

        Marca resultado = inventarioService.guardarMarca(marcaSample);

        assertNotNull(resultado);
        assertEquals("Gibson", resultado.getNombre());
        verify(marcaRepository, times(1)).save(marcaSample);
    }

    @Test
    void listarMarcasTest() {
        when(marcaRepository.findAll()).thenReturn(List.of(marcaSample));

        List<Marca> resultado = inventarioService.listarMarcas();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarMarcaPorIdTest() {
        // CORRECCIÓN: Uso de '1L' en lugar de '1Long'
        when(marcaRepository.findById(1L)).thenReturn(Optional.of(marcaSample));

        Optional<Marca> resultado = inventarioService.obtenerMarcaPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Gibson", resultado.get().getNombre());
    }

    @Test
    void eliminarMarcaTest() {
        // CORRECCIÓN: Uso de '1L' en lugar de '1Long'
        when(marcaRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.findByMarca_Id(1L)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> inventarioService.eliminarMarca(1L));
        verify(marcaRepository, times(1)).deleteById(1L);
    }
}
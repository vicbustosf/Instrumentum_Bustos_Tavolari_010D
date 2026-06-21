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

import cl.instrumentum.service_inventario.model.Categoria;
import cl.instrumentum.service_inventario.repository.CategoriaRepository;
import cl.instrumentum.service_inventario.repository.EquipoRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private EquipoRepository equipoRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Categoria categoriaSample;

    @BeforeEach
    void setUp() {
        // CORRECCIÓN: Se cambió '1Long' por '1L'
        categoriaSample = new Categoria(1L, "Teclados");
    }

    @Test
    void crearCategoriaTest() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSample);

        Categoria resultado = inventarioService.guardarCategoria(categoriaSample);

        assertNotNull(resultado);
        assertEquals("Teclados", resultado.getNombre());
        verify(categoriaRepository, times(1)).save(categoriaSample);
    }

    @Test
    void listarCategoriasTest() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoriaSample));

        List<Categoria> resultado = inventarioService.listarCategorias();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarCategoriaPorIdTest() {
        // CORRECCIÓN: Uso de '1L' en lugar de '1Long'
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaSample));

        Optional<Categoria> resultado = inventarioService.obtenerCategoriaPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Teclados", resultado.get().getNombre());
    }

    @Test
    void eliminarCategoriaTest() {
        // CORRECCIÓN: Uso de '1L' en lugar de '1Long'
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(equipoRepository.findByCategoria_Id(1L)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> inventarioService.eliminarCategoria(1L));
        verify(categoriaRepository, times(1)).deleteById(1L);
    }
}
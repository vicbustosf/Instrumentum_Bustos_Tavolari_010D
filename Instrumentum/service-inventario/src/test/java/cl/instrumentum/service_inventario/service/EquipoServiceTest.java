package cl.instrumentum.service_inventario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import cl.instrumentum.service_inventario.model.Categoria;
import cl.instrumentum.service_inventario.model.Equipo;
import cl.instrumentum.service_inventario.model.Marca;
import cl.instrumentum.service_inventario.repository.EquipoRepository;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class EquipoServiceTest {

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private InventarioService inventarioService;

    private Equipo equipoSample;
    
    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    private WebClient.RequestBodySpec requestBodySpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        Marca marca = new Marca(1L, "Fender");
        Categoria categoria = new Categoria(1L, "Cuerdas");
        equipoSample = new Equipo(1L, "Stratocaster", "American Pro", marca, categoria, 10L, "USUARIO", "INSTRUMENTO");

        // Inicializamos todos los componentes de la cadena de WebClient
        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        requestBodySpec = mock(WebClient.RequestBodySpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientForValidation() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());
    }

    @Test
    void crearEquipoTest() {
        mockWebClientForValidation();
        when(equipoRepository.findById(equipoSample.getId())).thenReturn(Optional.of(equipoSample));
        when(equipoRepository.findByNombre(equipoSample.getNombre())).thenReturn(Optional.of(equipoSample));
        when(equipoRepository.save(any(Equipo.class))).thenReturn(equipoSample);

        Equipo resultado = inventarioService.guardarEquipo(equipoSample);

        assertNotNull(resultado);
        assertEquals("Stratocaster", resultado.getNombre());
        verify(equipoRepository, times(1)).save(equipoSample);
    }

    @Test
    void listarTodosEquiposTest() {
        when(equipoRepository.findAll()).thenReturn(List.of(equipoSample));

        List<Equipo> resultado = inventarioService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(equipoRepository, times(1)).findAll();
    }

    @Test
    void buscarEquipoPorIdTest() {
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoSample));

        Optional<Equipo> resultado = inventarioService.obtenerEquipoPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void listarEquiposPorPropietarioTest() {
        when(equipoRepository.findByPropietarioId(10L)).thenReturn(List.of(equipoSample));

        List<Equipo> resultado = inventarioService.listarEquiposPorPropietario(10L);

        assertEquals(1, resultado.size());
        verify(equipoRepository, times(1)).findByPropietarioId(10L);
    }

    @Test
    void buscarEquiposConFiltrosTest() {
        when(equipoRepository.buscarConFiltrosExactos("Stratocaster", "Fender", "Cuerdas"))
                .thenReturn(List.of(equipoSample));

        List<Equipo> resultado = inventarioService.buscarEquipos("Stratocaster", "Fender", "Cuerdas");

        assertFalse(resultado.isEmpty());
        verify(equipoRepository, times(1)).buscarConFiltrosExactos("Stratocaster", "Fender", "Cuerdas");
    }

@SuppressWarnings("unchecked")
    @Test
    void eliminarEquipoTest() {
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoSample));
        when(webClientBuilder.build()).thenReturn(webClient);
        
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.just(Boolean.FALSE)).when(responseSpec).bodyToMono(Boolean.class);
        
        doReturn(requestBodyUriSpec).when(webClient).delete();
        doReturn(requestBodySpec).when(requestBodyUriSpec).uri(anyString());
        doReturn(responseSpec).when(requestBodySpec).retrieve();
        doReturn(Mono.empty()).when(responseSpec).toBodilessEntity();

        assertDoesNotThrow(() -> inventarioService.eliminarEquipo(1L));
        verify(equipoRepository, times(1)).deleteById(1L);
    }
}
package cl.instrumentum.service_gira.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import cl.instrumentum.service_gira.model.Gira;
import cl.instrumentum.service_gira.repository.GiraRepository;
import cl.instrumentum.service_gira.repository.ParadaGiraRepository;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class GiraServiceTest {

    @Mock
    private GiraRepository giraRepository;

    @Mock
    private ParadaGiraRepository paradaRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private GiraService giraService;

    private Gira giraMock;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        giraMock = new Gira(1L, 10L, "Tour Volver", LocalDate.now(), LocalDate.now().plusDays(5), new ArrayList<>());
        
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(mock(ResponseEntity.class)));
    }

    @Test
    void listarGirasTest() {
        List<Gira> lista = List.of(giraMock);
        when(giraRepository.findAll()).thenReturn(lista);

        List<Gira> resultado = giraService.listarGiras();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(giraRepository, times(1)).findAll();
    }

    @Test
    void listarGirasPorBandaTest() {
        List<Gira> lista = List.of(giraMock);
        when(giraRepository.findByIdBanda(10L)).thenReturn(lista);

        List<Gira> resultado = giraService.listarPorBanda(10L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getIdBanda());
        verify(giraRepository, times(1)).findByIdBanda(10L);
    }

    @Test
    void buscarGiraPorIdTest() {
        when(giraRepository.findById(1L)).thenReturn(Optional.of(giraMock));

        Optional<Gira> resultado = giraService.buscarGiraPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Tour Volver", resultado.get().getNombreGira());
        verify(giraRepository, times(1)).findById(1L);
    }

    @Test
    void guardarGiraTest() {
        when(giraRepository.save(any(Gira.class))).thenReturn(giraMock);

        Gira resultado = giraService.guardarGira(giraMock);

        assertNotNull(resultado);
        assertEquals("Tour Volver", resultado.getNombreGira());
        verify(giraRepository, times(1)).save(giraMock);
    }

    @Test
    void actualizarGiraTest() {
        Gira giraActualizada = new Gira(null, 10L, "Tour Editado", LocalDate.now(), LocalDate.now().plusDays(10), new ArrayList<>());
        when(giraRepository.findById(1L)).thenReturn(Optional.of(giraMock));
        when(giraRepository.save(any(Gira.class))).thenReturn(giraMock);

        Gira resultado = giraService.actualizarGira(1L, giraActualizada);

        assertNotNull(resultado);
        assertEquals("Tour Editado", resultado.getNombreGira());
        verify(giraRepository, times(1)).findById(1L);
        verify(giraRepository, times(1)).save(giraMock);
    }

    @Test
    void eliminarGiraTest() {
        when(giraRepository.existsById(1L)).thenReturn(true);
        when(paradaRepository.obtenerPorIdGira(1L)).thenReturn(new ArrayList<>());
        doNothing().when(giraRepository).deleteById(1L);
        
        boolean resultado = giraService.eliminarGira(1L);

        assertTrue(resultado);
        verify(giraRepository, times(1)).existsById(1L);
        verify(paradaRepository, times(1)).obtenerPorIdGira(1L);
        verify(giraRepository, times(1)).deleteById(1L);
    }
}
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
import cl.instrumentum.service_gira.model.ParadaGira;
import cl.instrumentum.service_gira.repository.GiraRepository;
import cl.instrumentum.service_gira.repository.ParadaGiraRepository;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ParadaGiraServiceTest {

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
    private GiraService giraService; // El servicio bajo prueba maneja ambas entidades

    private Gira giraMock;
    private ParadaGira paradaMock;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        giraMock = new Gira(1L, 10L, "Tour Volver", LocalDate.now(), LocalDate.now().plusDays(5), new ArrayList<>());
        paradaMock = new ParadaGira(105L, giraMock, 500L, "Santiago", "Hotel Costanera", "Van Privada");

        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(mock(ResponseEntity.class)));
    }

    @Test
    void listarParadasPorGiraTest() {
        List<ParadaGira> paradas = List.of(paradaMock);
        when(paradaRepository.obtenerPorIdGira(1L)).thenReturn(paradas);

        List<ParadaGira> resultado = giraService.listarParadasPorGira(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(paradaRepository, times(1)).obtenerPorIdGira(1L);
    }

    @Test
    void buscarParadaPorIdTest() {
        when(paradaRepository.findById(105L)).thenReturn(Optional.of(paradaMock));

        Optional<ParadaGira> resultado = giraService.buscarParadaPorId(105L);

        assertTrue(resultado.isPresent());
        assertEquals("Santiago", resultado.get().getCiudad());
        verify(paradaRepository, times(1)).findById(105L);
    }

    @Test
    void guardarParadaTest() {
        when(giraRepository.findById(1L)).thenReturn(Optional.of(giraMock));
        when(paradaRepository.save(any(ParadaGira.class))).thenReturn(paradaMock);

        ParadaGira resultado = giraService.guardarParada(paradaMock);

        assertNotNull(resultado);
        assertEquals("Santiago", resultado.getCiudad());
        verify(giraRepository, times(1)).findById(1L);
        verify(paradaRepository, times(1)).save(paradaMock);
    }

    @Test
    void actualizarParadaTest() {
        ParadaGira paradaActualizada = new ParadaGira(null, giraMock, 500L, "Mendoza", "Hotel Central", "Bus");
        when(paradaRepository.findById(105L)).thenReturn(Optional.of(paradaMock));
        when(giraRepository.findById(1L)).thenReturn(Optional.of(giraMock));
        when(paradaRepository.save(any(ParadaGira.class))).thenReturn(paradaMock);

        ParadaGira resultado = giraService.actualizarParada(105L, paradaActualizada);

        assertNotNull(resultado);
        assertEquals("Mendoza", resultado.getCiudad());
        assertEquals("Hotel Central", resultado.getAlojamiento());
        verify(paradaRepository, times(1)).findById(105L);
        verify(giraRepository, times(1)).findById(1L);
        verify(paradaRepository, times(1)).save(paradaMock);
    }

    @Test
    void eliminarParadaTest() {
        when(paradaRepository.existsById(105L)).thenReturn(true);

        boolean resultado = giraService.eliminarParada(105L);

        assertTrue(resultado);
        verify(paradaRepository, times(1)).existsById(105L);
        verify(paradaRepository, times(1)).deleteById(105L);
    }
}
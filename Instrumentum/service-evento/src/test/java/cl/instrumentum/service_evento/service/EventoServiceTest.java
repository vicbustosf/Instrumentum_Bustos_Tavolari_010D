package cl.instrumentum.service_evento.service;

import cl.instrumentum.service_evento.model.Evento;
import cl.instrumentum.service_evento.repository.EventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EventoService eventoService;

    private Evento evento;

    @BeforeEach
    void setUp() {
        evento = new Evento(1L, 5L, "Concierto de Prueba", LocalDate.now(), "1,2,3");
    }

    /**
     * Helper para mockear el encadenamiento fluido de WebClient en flujos exitosos.
     */
    @SuppressWarnings("unchecked")
    private void mockWebClientFlujoFeliz() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));
    }

    @Test
    public void listarEventosTest() {
        // Arrange
        List<Evento> listaEventos = List.of(evento);
        when(eventoRepository.findAll()).thenReturn(listaEventos);

        // Act
        List<Evento> resultado = eventoService.listarEventos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Concierto de Prueba", resultado.get(0).getNombre());
        verify(eventoRepository, times(1)).findAll();
    }

    @Test
    public void buscarEventoPorIdTest() {
        // Arrange
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        // Act
        Optional<Evento> resultado = eventoService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdEvento());
        verify(eventoRepository, times(1)).findById(1L);
    }

    @Test
    public void guardarEventoTest() {
        // Arrange
        mockWebClientFlujoFeliz();
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        // Act
        Evento resultado = eventoService.guardarEvento(evento);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdEvento());
        assertEquals(5L, resultado.getIdBanda());
        verify(eventoRepository, times(1)).save(evento);
    }

    @Test
    public void actualizarEventoTest() {
        // Arrange
        mockWebClientFlujoFeliz();
        Evento eventoModificado = new Evento(1L, 5L, "Nombre Actualizado", LocalDate.now(), "4,5");
        
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoModificado);

        // Act
        Evento resultado = eventoService.actualizarEvento(1L, eventoModificado);

        // Assert
        assertNotNull(resultado);
        assertEquals("Nombre Actualizado", resultado.getNombre());
        assertEquals("4,5", resultado.getCanciones());
        verify(eventoRepository, times(1)).findById(1L);
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    public void eliminarEventoTest() {
        // Arrange
        when(eventoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eventoRepository).deleteById(1L);

        // Act
        boolean resultado = eventoService.eliminarEvento(1L);

        // Assert
        assertTrue(resultado);
        verify(eventoRepository, times(1)).existsById(1L);
        verify(eventoRepository, times(1)).deleteById(1L);
    }

    @Test
    public void obtenerEventoPorBandaTest() {
        // Arrange
        List<Evento> listaPorBanda = List.of(evento);
        when(eventoRepository.findByIdBanda(5L)).thenReturn(listaPorBanda);

        // Act
        List<Evento> resultado = eventoService.obtenerPorBanda(5L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getIdBanda());
        verify(eventoRepository, times(1)).findByIdBanda(5L);
    }
}
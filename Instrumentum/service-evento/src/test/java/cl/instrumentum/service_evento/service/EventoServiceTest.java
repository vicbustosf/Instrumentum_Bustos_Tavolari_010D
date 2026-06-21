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
        List<Evento> listaEventos = List.of(evento);
        when(eventoRepository.findAll()).thenReturn(listaEventos);

        List<Evento> resultado = eventoService.listarEventos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Concierto de Prueba", resultado.get(0).getNombre());
        verify(eventoRepository, times(1)).findAll();
    }

    @Test
    public void buscarEventoPorIdTest() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        Optional<Evento> resultado = eventoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdEvento());
        verify(eventoRepository, times(1)).findById(1L);
    }

    @Test
    public void guardarEventoTest() {
        mockWebClientFlujoFeliz();
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        Evento resultado = eventoService.guardarEvento(evento);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdEvento());
        assertEquals(5L, resultado.getIdBanda());
        verify(eventoRepository, times(1)).save(evento);
    }

    @Test
    public void actualizarEventoTest() {
        mockWebClientFlujoFeliz();
        Evento eventoModificado = new Evento(1L, 5L, "Nombre Actualizado", LocalDate.now(), "4,5");
        
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoModificado);

        Evento resultado = eventoService.actualizarEvento(1L, eventoModificado);

        assertNotNull(resultado);
        assertEquals("Nombre Actualizado", resultado.getNombre());
        assertEquals("4,5", resultado.getCanciones());
        verify(eventoRepository, times(1)).findById(1L);
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    public void eliminarEventoTest() {
        when(eventoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eventoRepository).deleteById(1L);

        boolean resultado = eventoService.eliminarEvento(1L);

        assertTrue(resultado);
        verify(eventoRepository, times(1)).existsById(1L);
        verify(eventoRepository, times(1)).deleteById(1L);
    }

    @Test
    public void obtenerEventoPorBandaTest() {
        List<Evento> listaPorBanda = List.of(evento);
        when(eventoRepository.findByIdBanda(5L)).thenReturn(listaPorBanda);

        List<Evento> resultado = eventoService.obtenerPorBanda(5L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getIdBanda());
        verify(eventoRepository, times(1)).findByIdBanda(5L);
    }
}
package cl.instrumentum.service_finanza.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import cl.instrumentum.service_finanza.model.Transaccion;
import cl.instrumentum.service_finanza.repository.TransaccionRepository;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

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
    private TransaccionService transaccionService;

    private Transaccion transaccionBase;

    @BeforeEach
    void setUp() {
        transaccionBase = new Transaccion(1L, 10L, "ingreso", 150000.0, LocalDate.now(), "Concierto");
    }


    @SuppressWarnings("unchecked")
    private void mockWebClientFlujoFeliz() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());
    }

    @Test
    public void listarTransaccionesTest() {
        // Arrange
        when(transaccionRepository.findAll()).thenReturn(List.of(transaccionBase));

        // Act
        List<Transaccion> resultado = transaccionService.listarTransacciones();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(transaccionRepository).findAll();
    }

    @Test
    public void buscarTransaccionPorIdTest() {
        // Arrange
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccionBase));

        // Act
        Optional<Transaccion> resultado = transaccionService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdTransaccion());
        verify(transaccionRepository).findById(1L);
    }

    @Test
    public void guardarTransaccionTest() {
        // Arrange
        mockWebClientFlujoFeliz();
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionBase);

        // Act
        Transaccion resultado = transaccionService.guardarTransaccion(transaccionBase);

        // Assert
        assertNotNull(resultado);
        assertEquals("ingreso", resultado.getTipoMovimiento());
        verify(transaccionRepository).save(transaccionBase);
    }

    @Test
    public void actualizarTransaccionTest() {
        // Arrange
        mockWebClientFlujoFeliz();
        Transaccion datosActualizados = new Transaccion(null, 10L, "egreso", 50000.0, LocalDate.now(), "Compra Cuerdas");
        
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(transaccionBase));
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionBase);

        // Act
        Transaccion resultado = transaccionService.actualizarTransaccion(1L, datosActualizados);

        // Assert
        assertNotNull(resultado);
        assertEquals("egreso", resultado.getTipoMovimiento());
        assertEquals(50000.0, resultado.getMonto());
        verify(transaccionRepository).findById(1L);
        verify(transaccionRepository).save(any(Transaccion.class));
    }

    @Test
    public void eliminarTransaccionTest() {
        // Arrange
        when(transaccionRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean resultado = transaccionService.eliminarTransaccion(1L);

        // Assert
        assertTrue(resultado);
        verify(transaccionRepository).existsById(1L);
        verify(transaccionRepository).deleteById(1L);
    }

    @Test
    public void obtenerTransaccionesPorBandaTest() {
        // Arrange
        when(transaccionRepository.findByIdBanda(10L)).thenReturn(List.of(transaccionBase));

        // Act
        List<Transaccion> resultado = transaccionService.obtenerPorBanda(10L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getIdBanda());
        verify(transaccionRepository).findByIdBanda(10L);
    }
}
package cl.instrumentum.service_mantenimiento.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import cl.instrumentum.service_mantenimiento.model.Mantenimiento;
import cl.instrumentum.service_mantenimiento.repository.MantenimientoRepository;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MantenimientoServiceTest {

    @Mock
    private MantenimientoRepository mantenimientoRepository;

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
    private MantenimientoService mantenimientoService;

    private Mantenimiento mantenimientoMock;

    @BeforeEach
    void setUp() {
        mantenimientoMock = new Mantenimiento(1L, 105L, LocalDate.now(), "Mantenimiento Preventivo", 50000.0);
    }

    @Test
    void buscarMantenimientoPorIdTest() {
        // Arrange
        when(mantenimientoRepository.findById(1L)).thenReturn(Optional.of(mantenimientoMock));

        // Act
        Optional<Mantenimiento> resultado = mantenimientoService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(mantenimientoRepository, times(1)).findById(1L);
    }

    @SuppressWarnings("unchecked")
    @Test
    void registrarMantenimientoTest() {
        // Arrange & Mocking encadenado de WebClient para simular flujo feliz síncrono (.block())
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());
        
        when(mantenimientoRepository.save(any(Mantenimiento.class))).thenReturn(mantenimientoMock);

        // Act
        Mantenimiento resultado = mantenimientoService.registrarMantenimiento(mantenimientoMock);

        // Assert
        assertNotNull(resultado);
        assertEquals(105L, resultado.getEquipoId());
        verify(mantenimientoRepository, times(1)).save(mantenimientoMock);
    }

    @Test
    void listarMantenimientoPorEquipoTest() {
        // Arrange
        List<Mantenimiento> lista = Collections.singletonList(mantenimientoMock);
        when(mantenimientoRepository.findByEquipoIdOrderByFechaDesc(105L)).thenReturn(lista);

        // Act
        List<Mantenimiento> resultado = mantenimientoService.listarPorEquipo(105L);

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(mantenimientoRepository, times(1)).findByEquipoIdOrderByFechaDesc(105L);
    }

    @Test
    void requiereMantenimientoTest() {
        // Arrange: Un mantenimiento antiguo de hace 7 meses (Debe dar true al requerir mantenimiento)
        Mantenimiento antiguo = new Mantenimiento(2L, 105L, LocalDate.now().minusMonths(7), "Antiguo", 100.0);
        when(mantenimientoRepository.findTopByEquipoIdOrderByFechaDesc(105L)).thenReturn(Optional.of(antiguo));

        // Act
        boolean resultado = mantenimientoService.requiereMantenimiento(105L);

        // Assert
        assertTrue(resultado);
        verify(mantenimientoRepository, times(1)).findTopByEquipoIdOrderByFechaDesc(105L);
    }

    @Test
    void eliminarMantenimientoTest() {
        // Arrange
        List<Mantenimiento> lista = Collections.singletonList(mantenimientoMock);
        when(mantenimientoRepository.findByEquipoIdOrderByFechaDesc(105L)).thenReturn(lista);
        doNothing().when(mantenimientoRepository).deleteByEquipoId(105L);

        // Act
        boolean resultado = mantenimientoService.eliminarMantenimiento(105L);

        // Assert
        assertTrue(resultado);
        verify(mantenimientoRepository, times(1)).deleteByEquipoId(105L);
    }

    @Test
    void eliminarMantenimientoPorIdTest() {
        // Arrange
        when(mantenimientoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(mantenimientoRepository).deleteById(1L);

        // Act
        boolean resultado = mantenimientoService.eliminarMantenimientoPorId(1L);

        // Assert
        assertTrue(resultado);
        verify(mantenimientoRepository, times(1)).deleteById(1L);
    }
}
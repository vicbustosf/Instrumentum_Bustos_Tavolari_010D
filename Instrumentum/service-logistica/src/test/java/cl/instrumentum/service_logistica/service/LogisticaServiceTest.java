package cl.instrumentum.service_logistica.service;

import cl.instrumentum.service_logistica.model.Contenedor;
import cl.instrumentum.service_logistica.model.ContenedorEquipo;
import cl.instrumentum.service_logistica.repository.ContenedorEquipoRepository;
import cl.instrumentum.service_logistica.repository.ContenedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogisticaServiceTest {

    @Mock
    private ContenedorRepository contenedorRepository;

    @Mock
    private ContenedorEquipoRepository contenedorEquipoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private LogisticaService logisticaService;

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        webClient = Mockito.mock(WebClient.class);
        requestHeadersUriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        responseSpec = Mockito.mock(WebClient.ResponseSpec.class);
    }

    private void mockWebClientExitoso() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("Debería crear un contenedor de forma exitosa")
    void crearContenedorTest() {
        mockWebClientExitoso();
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        Contenedor entrada = new Contenedor(null, 10L, "Flightcase Guitarras", 25.4, new ArrayList<>());
        
        when(contenedorRepository.save(any(Contenedor.class))).thenAnswer(invocation -> {
            Contenedor c = invocation.getArgument(0);
            c.setIdContenedor(1L);
            return c;
        });

        Contenedor resultado = logisticaService.crearContenedor(entrada);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdContenedor());
        assertEquals("Flightcase Guitarras", resultado.getNombreCaja());
        verify(contenedorRepository, times(1)).save(entrada);
    }

    @Test
    @DisplayName("Debería listar todos los contenedores de forma exitosa")
    void listarTodosContenedoresTest() {
        List<Contenedor> listaMock = new ArrayList<>();
        listaMock.add(new Contenedor(1L, 10L, "Caja A", 12.0, new ArrayList<>()));
        listaMock.add(new Contenedor(2L, 10L, "Caja B", 15.0, new ArrayList<>()));

        when(contenedorRepository.findAll()).thenReturn(listaMock);

        List<Contenedor> resultado = logisticaService.listarTodosContenedores();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Caja A", resultado.get(0).getNombreCaja());
        verify(contenedorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería buscar un contenedor por su ID de forma exitosa")
    void buscarPorIdTest() {
        Contenedor contenedorMock = new Contenedor(1L, 10L, "Flightcase Guitarras", 25.4, new ArrayList<>());
        when(contenedorRepository.findById(1L)).thenReturn(Optional.of(contenedorMock));

        Optional<Contenedor> resultadoOpt = logisticaService.buscarPorId(1L);

        assertNotNull(resultadoOpt);
        assertEquals(true, resultadoOpt.isPresent());
        assertEquals("Flightcase Guitarras", resultadoOpt.get().getNombreCaja());
        verify(contenedorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería listar contenedores por ID de banda de forma exitosa")
    void listarPorBandaTest() {
        mockWebClientExitoso();
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty()); // Validación de banda OK

        List<Contenedor> listaMock = new ArrayList<>();
        listaMock.add(new Contenedor(1L, 10L, "Flightcase Guitarras", 25.4, new ArrayList<>()));
        when(contenedorRepository.findByIdBanda(10L)).thenReturn(listaMock);

        List<Contenedor> resultado = logisticaService.listarPorBanda(10L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getIdBanda());
        verify(contenedorRepository, times(1)).findByIdBanda(10L);
    }

    @Test
    @DisplayName("Debería actualizar un contenedor de forma exitosa")
    void actualizarContenedorTest() {
        Contenedor contenedorExistente = new Contenedor(1L, 10L, "Caja Editada", 30.0, new ArrayList<>());
        when(contenedorRepository.save(any(Contenedor.class))).thenReturn(contenedorExistente);

        Contenedor resultado = logisticaService.actualizarContenedor(contenedorExistente);

        assertNotNull(resultado);
        assertEquals("Caja Editada", resultado.getNombreCaja());
        verify(contenedorRepository, times(1)).save(contenedorExistente);
    }

    @Test
    @DisplayName("Debería eliminar un contenedor de forma exitosa")
    void eliminarContenedorTest() {
        Contenedor contenedorMock = new Contenedor(1L, 10L, "A Borrar", 10.0, new ArrayList<>());
        when(contenedorRepository.findById(1L)).thenReturn(Optional.of(contenedorMock));

        logisticaService.eliminarContenedor(1L);

        verify(contenedorRepository, times(1)).findById(1L);
        verify(contenedorRepository, times(1)).delete(contenedorMock);
    }

    @Test
    @DisplayName("Debería agregar un equipo a un contenedor de forma exitosa")
    void agregarEquipoTest() {
        mockWebClientExitoso();
        when(responseSpec.bodyToMono(Object.class)).thenReturn(Mono.just(new Object()));

        Contenedor contenedorMock = new Contenedor(1L, 10L, "Flightcase Guitarras", 25.4, new ArrayList<>());
        when(contenedorRepository.findById(1L)).thenReturn(Optional.of(contenedorMock));

        ContenedorEquipo asociacionEsperada = new ContenedorEquipo(null, contenedorMock, 5L);
        when(contenedorEquipoRepository.save(any(ContenedorEquipo.class))).thenAnswer(invocation -> {
            ContenedorEquipo ce = invocation.getArgument(0);
            ce.setId(100L); // Le asignamos id ficticio de BD
            return ce;
        });

        ContenedorEquipo resultado = logisticaService.agregarEquipo(1L, 5L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals(5L, resultado.getIdEquipo());
        assertEquals(1L, resultado.getContenedor().getIdContenedor());
        verify(contenedorRepository, times(1)).findById(1L);
        verify(contenedorEquipoRepository, times(1)).save(any(ContenedorEquipo.class));
    }

    @Test
    @DisplayName("Debería listar todos los equipos en contenedores de forma exitosa")
    void listarTodosLosEquiposEnContenedoresTest() {
        Contenedor c = new Contenedor(1L, 10L, "Contenedor", 10.0, new ArrayList<>());
        List<ContenedorEquipo> listaMock = new ArrayList<>();
        listaMock.add(new ContenedorEquipo(100L, c, 5L));
        listaMock.add(new ContenedorEquipo(101L, c, 6L));

        when(contenedorEquipoRepository.findAll()).thenReturn(listaMock);

        List<ContenedorEquipo> resultado = logisticaService.listarTodosLosEquiposEnContenedores();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(5L, resultado.get(0).getIdEquipo());
        verify(contenedorEquipoRepository, times(1)).findAll();
    }
}
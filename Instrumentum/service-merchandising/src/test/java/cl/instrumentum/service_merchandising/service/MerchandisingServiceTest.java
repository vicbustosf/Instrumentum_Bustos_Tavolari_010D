package cl.instrumentum.service_merchandising.service;

import cl.instrumentum.service_merchandising.model.ProductoMerch;
import cl.instrumentum.service_merchandising.model.VentaMerch;
import cl.instrumentum.service_merchandising.repository.ProductoMerchRepository;
import cl.instrumentum.service_merchandising.repository.VentaMerchRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchandisingServiceTest {

    @Mock
    private ProductoMerchRepository productoMerchRepository;

    @Mock
    private VentaMerchRepository ventaMerchRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private MerchandisingService merchandisingService;

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
    @DisplayName("Debería crear un producto de forma exitosa")
    void crearProductoTest() {
        mockWebClientExitoso();
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        ProductoMerch entrada = new ProductoMerch(null, 1L, "Polera Tour 2026", "Polera", 15000.0, 50);

        when(productoMerchRepository.save(any(ProductoMerch.class))).thenAnswer(invocation -> {
            ProductoMerch p = invocation.getArgument(0);
            p.setIdProducto(1L);
            return p;
        });

        ProductoMerch resultado = merchandisingService.crearProducto(entrada);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
        assertEquals("Polera Tour 2026", resultado.getNombre());
        verify(productoMerchRepository, times(1)).save(entrada);
    }

    @Test
    @DisplayName("Debería listar todos los productos de forma exitosa")
    void listarTodosProductosTest() {
        List<ProductoMerch> listaMock = new ArrayList<>();
        listaMock.add(new ProductoMerch(1L, 1L, "Polera Tour 2026", "Polera", 15000.0, 50));
        listaMock.add(new ProductoMerch(2L, 1L, "Disco Primer Álbum", "Disco", 12000.0, 30));

        when(productoMerchRepository.findAll()).thenReturn(listaMock);

        List<ProductoMerch> resultado = merchandisingService.listarTodosProductos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Polera Tour 2026", resultado.get(0).getNombre());
        verify(productoMerchRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería buscar un producto por su ID de forma exitosa")
    void buscarProductoPorIdTest() {
        ProductoMerch productoMock = new ProductoMerch(1L, 1L, "Polera Tour 2026", "Polera", 15000.0, 50);
        when(productoMerchRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        Optional<ProductoMerch> resultadoOpt = merchandisingService.buscarProductoPorId(1L);

        assertNotNull(resultadoOpt);
        assertTrue(resultadoOpt.isPresent());
        assertEquals("Polera Tour 2026", resultadoOpt.get().getNombre());
        verify(productoMerchRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería listar productos por ID de banda de forma exitosa")
    void listarProductosPorBandaTest() {
        mockWebClientExitoso();
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        List<ProductoMerch> listaMock = new ArrayList<>();
        listaMock.add(new ProductoMerch(1L, 1L, "Polera Tour 2026", "Polera", 15000.0, 50));
        when(productoMerchRepository.findByIdBanda(1L)).thenReturn(listaMock);

        List<ProductoMerch> resultado = merchandisingService.listarProductosPorBanda(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdBanda());
        verify(productoMerchRepository, times(1)).findByIdBanda(1L);
    }

    @Test
    @DisplayName("Debería actualizar un producto de forma exitosa")
    void actualizarProductoTest() {
        ProductoMerch productoExistente = new ProductoMerch(1L, 1L, "Polera Tour Edición Limitada", "Polera", 18000.0, 40);
        when(productoMerchRepository.save(any(ProductoMerch.class))).thenReturn(productoExistente);

        ProductoMerch resultado = merchandisingService.actualizarProducto(productoExistente);

        assertNotNull(resultado);
        assertEquals("Polera Tour Edición Limitada", resultado.getNombre());
        verify(productoMerchRepository, times(1)).save(productoExistente);
    }

    @Test
    @DisplayName("Debería eliminar un producto de forma exitosa")
    void eliminarProductoTest() {
        ProductoMerch productoMock = new ProductoMerch(1L, 1L, "Parche Gira", "Parche", 5000.0, 20);
        when(productoMerchRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        when(ventaMerchRepository.findByProducto(productoMock)).thenReturn(new ArrayList<>());

        merchandisingService.eliminarProducto(1L);

        verify(productoMerchRepository, times(1)).findById(1L);
        verify(productoMerchRepository, times(1)).delete(productoMock);
    }

    @Test
    @DisplayName("Debería registrar una venta de forma exitosa")
    void registrarVentaTest() {
        ProductoMerch productoMock = new ProductoMerch(1L, 1L, "Polera Tour 2026", "Polera", 15000.0, 50);
        when(productoMerchRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        VentaMerch entrada = new VentaMerch(null, new ProductoMerch(1L, null, null, null, null, null), 2, null, null);

        when(ventaMerchRepository.save(any(VentaMerch.class))).thenAnswer(invocation -> {
            VentaMerch v = invocation.getArgument(0);
            v.setIdVenta(1L);
            return v;
        });

        VentaMerch resultado = merchandisingService.registrarVenta(entrada);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdVenta());
        assertEquals(30000.0, resultado.getMontoTotal());
        assertEquals(48, productoMock.getStock());
        verify(productoMerchRepository, times(1)).save(productoMock);
        verify(ventaMerchRepository, times(1)).save(entrada);
    }

    @Test
    @DisplayName("Debería registrar una venta asociada a un evento de forma exitosa")
    void registrarVentaConEventoTest() {
        mockWebClientExitoso();
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        ProductoMerch productoMock = new ProductoMerch(2L, 1L, "Disco Primer Álbum", "Disco", 12000.0, 30);
        when(productoMerchRepository.findById(2L)).thenReturn(Optional.of(productoMock));

        VentaMerch entrada = new VentaMerch(null, new ProductoMerch(2L, null, null, null, null, null), 1, null, 5L);

        when(ventaMerchRepository.save(any(VentaMerch.class))).thenAnswer(invocation -> {
            VentaMerch v = invocation.getArgument(0);
            v.setIdVenta(2L);
            return v;
        });

        VentaMerch resultado = merchandisingService.registrarVenta(entrada);

        assertNotNull(resultado);
        assertEquals(12000.0, resultado.getMontoTotal());
        assertEquals(5L, resultado.getIdEventoOrigen());
        assertEquals(29, productoMock.getStock());
        verify(productoMerchRepository, times(1)).save(productoMock);
    }

    @Test
    @DisplayName("Debería listar todas las ventas de forma exitosa")
    void listarTodasVentasTest() {
        ProductoMerch p = new ProductoMerch(1L, 1L, "Polera Tour 2026", "Polera", 15000.0, 48);
        List<VentaMerch> listaMock = new ArrayList<>();
        listaMock.add(new VentaMerch(1L, p, 2, 30000.0, null));
        listaMock.add(new VentaMerch(2L, p, 1, 15000.0, 5L));

        when(ventaMerchRepository.findAll()).thenReturn(listaMock);

        List<VentaMerch> resultado = merchandisingService.listarTodasVentas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(ventaMerchRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería buscar una venta por su ID de forma exitosa")
    void buscarVentaPorIdTest() {
        ProductoMerch p = new ProductoMerch(1L, 1L, "Polera Tour 2026", "Polera", 15000.0, 48);
        VentaMerch ventaMock = new VentaMerch(1L, p, 2, 30000.0, null);
        when(ventaMerchRepository.findById(1L)).thenReturn(Optional.of(ventaMock));

        Optional<VentaMerch> resultadoOpt = merchandisingService.buscarVentaPorId(1L);

        assertNotNull(resultadoOpt);
        assertTrue(resultadoOpt.isPresent());
        assertEquals(30000.0, resultadoOpt.get().getMontoTotal());
        verify(ventaMerchRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería listar las ventas de un producto de forma exitosa")
    void listarVentasPorProductoTest() {
        ProductoMerch p = new ProductoMerch(1L, 1L, "Polera Tour 2026", "Polera", 15000.0, 48);
        when(productoMerchRepository.findById(1L)).thenReturn(Optional.of(p));

        List<VentaMerch> listaMock = new ArrayList<>();
        listaMock.add(new VentaMerch(1L, p, 2, 30000.0, null));
        when(ventaMerchRepository.findByProducto(p)).thenReturn(listaMock);

        List<VentaMerch> resultado = merchandisingService.listarVentasPorProducto(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(ventaMerchRepository, times(1)).findByProducto(p);
    }

    @Test
    @DisplayName("Debería listar las ventas de un evento de forma exitosa")
    void listarVentasPorEventoTest() {
        ProductoMerch p = new ProductoMerch(2L, 1L, "Disco Primer Álbum", "Disco", 12000.0, 29);
        List<VentaMerch> listaMock = new ArrayList<>();
        listaMock.add(new VentaMerch(2L, p, 1, 12000.0, 5L));

        when(ventaMerchRepository.findByIdEventoOrigen(5L)).thenReturn(listaMock);

        List<VentaMerch> resultado = merchandisingService.listarVentasPorEvento(5L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getIdEventoOrigen());
        verify(ventaMerchRepository, times(1)).findByIdEventoOrigen(5L);
    }

    @Test
    @DisplayName("Debería actualizar una venta de forma exitosa")
    void actualizarVentaTest() {
        ProductoMerch p = new ProductoMerch(1L, 1L, "Polera Tour 2026", "Polera", 15000.0, 48);
        VentaMerch ventaExistente = new VentaMerch(1L, p, 2, 30000.0, 7L);
        when(ventaMerchRepository.save(any(VentaMerch.class))).thenReturn(ventaExistente);

        VentaMerch resultado = merchandisingService.actualizarVenta(ventaExistente);

        assertNotNull(resultado);
        assertEquals(7L, resultado.getIdEventoOrigen());
        verify(ventaMerchRepository, times(1)).save(ventaExistente);
    }

    @Test
    @DisplayName("Debería eliminar una venta y reintegrar el stock de forma exitosa")
    void eliminarVentaTest() {
        ProductoMerch p = new ProductoMerch(1L, 1L, "Polera Tour 2026", "Polera", 15000.0, 48);
        VentaMerch ventaMock = new VentaMerch(1L, p, 2, 30000.0, null);
        when(ventaMerchRepository.findById(1L)).thenReturn(Optional.of(ventaMock));

        merchandisingService.eliminarVenta(1L);

        assertEquals(50, p.getStock());
        verify(productoMerchRepository, times(1)).save(p);
        verify(ventaMerchRepository, times(1)).delete(ventaMock);
    }
}

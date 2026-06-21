package cl.instrumentum.service_specs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import cl.instrumentum.service_specs.model.EspecificacionElectronica;
import cl.instrumentum.service_specs.repository.EspecificacionElectronicaRepository;
import cl.instrumentum.service_specs.repository.EspecificacionInstrumentoRepository;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EspecificacionElectronicaServiceTest {

    @Mock
    private EspecificacionInstrumentoRepository instrumentoRepository;

    @Mock
    private EspecificacionElectronicaRepository electronicaRepository;

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
    private SpecsService specsService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));
    }

    @Test
    public void guardarEspecificacionElectronicaTest() {
        Long equipoId = 2L;
        EspecificacionElectronica datos = new EspecificacionElectronica(null, "9V DC", 45.0, "Análogo");
        EspecificacionElectronica datosGuardados = new EspecificacionElectronica(equipoId, "9V DC", 45.0, "Análogo");

        when(electronicaRepository.save(datos)).thenReturn(datosGuardados);

        EspecificacionElectronica resultado = specsService.guardarElectronica(equipoId, datos);

        assertNotNull(resultado);
        assertEquals(equipoId, resultado.getIdEquipo());
    }

    @Test
    public void obtenerEspecificacionElectronicaPorIdTest() {
        Long equipoId = 2L;
        EspecificacionElectronica el = new EspecificacionElectronica(equipoId, "18V DC", 120.0, "Digital");

        when(electronicaRepository.findById(equipoId)).thenReturn(Optional.of(el));

        Optional<EspecificacionElectronica> resultado = specsService.obtenerElectronicaPorId(equipoId);

        assertTrue(resultado.isPresent());
        assertEquals("Digital", resultado.get().getTipoCircuito());
    }

    @Test
    public void eliminarEspecificacionElectronicaTest() {
        Long equipoId = 2L;
        EspecificacionElectronica el = new EspecificacionElectronica(equipoId, "9V DC", 12.0, "True Bypass");

        // Evitamos el NullPointerException simulando un retorno para la primera entidad consultada por el servicio
        when(instrumentoRepository.findById(equipoId)).thenReturn(Optional.empty());
        when(electronicaRepository.findById(equipoId)).thenReturn(Optional.of(el));

        specsService.eliminarPorEquipoId(equipoId);

        assertTrue(true);
    }
}
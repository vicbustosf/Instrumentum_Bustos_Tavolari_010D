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
import cl.instrumentum.service_specs.model.EspecificacionInstrumento;
import cl.instrumentum.service_specs.repository.EspecificacionElectronicaRepository;
import cl.instrumentum.service_specs.repository.EspecificacionInstrumentoRepository;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EspecificacionInstrumentoServiceTest {

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
    public void guardarEspecificacionInstrumentoTest() {
        Long equipoId = 1L;
        EspecificacionInstrumento espec = new EspecificacionInstrumento(null, "Caoba", "HSS", "0.010");
        EspecificacionInstrumento especGuardada = new EspecificacionInstrumento(equipoId, "Caoba", "HSS", "0.010");

        when(instrumentoRepository.save(espec)).thenReturn(especGuardada);

        EspecificacionInstrumento resultado = specsService.guardarInstrumento(equipoId, espec);

        assertNotNull(resultado);
        assertEquals(equipoId, resultado.getIdEquipo());
    }

    @Test
    public void obtenerEspecificacionInstrumentoPorIdTest() {
        Long equipoId = 1L;
        EspecificacionInstrumento espec = new EspecificacionInstrumento(equipoId, "Aliso", "SSS", "0.009");

        when(instrumentoRepository.findById(equipoId)).thenReturn(Optional.of(espec));

        Optional<EspecificacionInstrumento> resultado = specsService.obtenerInstrumentoPorId(equipoId);

        assertTrue(resultado.isPresent());
        assertEquals("Aliso", resultado.get().getTipoMadera());
    }

    @Test
    public void eliminarEspecificacionInstrumentoTest() {
        Long equipoId = 1L;
        EspecificacionInstrumento espec = new EspecificacionInstrumento(equipoId, "Fresno", "HH", "0.011");

        when(instrumentoRepository.findById(equipoId)).thenReturn(Optional.of(espec));
        when(electronicaRepository.findById(equipoId)).thenReturn(Optional.empty());

        specsService.eliminarPorEquipoId(equipoId);

        assertTrue(true);
    }
}
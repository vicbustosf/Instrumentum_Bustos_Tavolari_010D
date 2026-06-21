package cl.instrumentum.service_rig.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.instrumentum.service_rig.model.Cancion;
import cl.instrumentum.service_rig.model.EquipoCancion;
import cl.instrumentum.service_rig.repository.CancionRepository;
import cl.instrumentum.service_rig.repository.EquipoCancionRepository;

@ExtendWith(MockitoExtension.class)
public class EquipoCancionServiceTest {

    @Mock
    private EquipoCancionRepository equipoCancionRepository;

    @Mock
    private CancionRepository cancionRepository;

    @InjectMocks
    private RigService rigService;

    @Test
    public void buscarEquipoCancionTest() {
        Cancion cancion = new Cancion(1L, "Cancion A", 1L, 180, new ArrayList<>());
        Long equipoId = 55L;
        EquipoCancion ecSimulado = new EquipoCancion(100L, cancion, equipoId, 1, "Gain: 5");
        
        when(equipoCancionRepository.findByCancionAndEquipoId(cancion, equipoId))
                .thenReturn(Optional.of(ecSimulado));

        Optional<EquipoCancion> resultado = rigService.buscarEquipoCancion(cancion, equipoId);

        assertTrue(resultado.isPresent());
        assertEquals(equipoId, resultado.get().getEquipoId());
        verify(equipoCancionRepository, times(1)).findByCancionAndEquipoId(cancion, equipoId);
    }

    @Test
    public void guardarEquipoCancionTest() {
        EquipoCancion ecParaGuardar = new EquipoCancion(null, null, 55L, 1, "Gain: 5");
        EquipoCancion ecGuardado = new EquipoCancion(100L, null, 55L, 1, "Gain: 5");
        
        when(equipoCancionRepository.save(ecParaGuardar)).thenReturn(ecGuardado);

        EquipoCancion resultado = rigService.guardarEquipoCancion(ecParaGuardar);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        verify(equipoCancionRepository, times(1)).save(ecParaGuardar);
    }

    @Test
    public void removerEquipoCancionTest() {
        Cancion cancion = new Cancion(1L, "Cancion A", 1L, 180, new ArrayList<>());
        Long equipoId = 55L;
        EquipoCancion ecExistente = new EquipoCancion(100L, cancion, equipoId, 1, "Gain: 5");
        
        when(equipoCancionRepository.findByCancionAndEquipoId(cancion, equipoId))
                .thenReturn(Optional.of(ecExistente));
        doNothing().when(equipoCancionRepository).delete(ecExistente);

        boolean resultado = rigService.removerEquipo(cancion, equipoId);

        assertTrue(resultado);
        verify(equipoCancionRepository, times(1)).findByCancionAndEquipoId(cancion, equipoId);
        verify(equipoCancionRepository, times(1)).delete(ecExistente);
    }

    @Test
    public void equipoEstaEnAlgunaCancionEquipoCancionTest() {
        Long equipoId = 55L;
        when(equipoCancionRepository.existsByEquipoId(equipoId)).thenReturn(true);

        boolean resultado = rigService.equipoEstaEnAlgunaCancion(equipoId);

        assertTrue(resultado);
        verify(equipoCancionRepository, times(1)).existsByEquipoId(equipoId);
    }

    @Test
    public void obtenerSetupCompletoEquipoCancionTest() {
        Long cancionId = 1L;
        Cancion cancion = new Cancion(cancionId, "Cancion A", 1L, 180, new ArrayList<>());
        List<EquipoCancion> listaEquipos = List.of(
            new EquipoCancion(100L, cancion, 55L, 1, "Gain: 5"),
            new EquipoCancion(101L, cancion, 56L, 2, "Delay: 300ms")
        );

        when(cancionRepository.findById(cancionId)).thenReturn(Optional.of(cancion));
        when(equipoCancionRepository.findByCancionOrderByPosicionAsc(cancion)).thenReturn(listaEquipos);

        Map<String, Object> resultado = rigService.obtenerSetupCompleto(cancionId);

        assertNotNull(resultado);
        assertEquals(cancion, resultado.get("cancion"));
        assertEquals(listaEquipos, resultado.get("equipos"));
        verify(cancionRepository, times(1)).findById(cancionId);
        verify(equipoCancionRepository, times(1)).findByCancionOrderByPosicionAsc(cancion);
    }
}
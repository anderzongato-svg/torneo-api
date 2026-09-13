package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.model.Sugerencia;
import com.torneomalaga.api.repository.CampeonatoRepository;
import com.torneomalaga.api.repository.SugerenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SugerenciaServiceTest {

    @Mock
    private SugerenciaRepository sugerenciaRepository;

    @Mock
    private CampeonatoRepository campeonatoRepository;

    private SugerenciaService sugerenciaService;
    private Campeonato campeonatoExistente;

    @BeforeEach
    void setUp() {
        CampeonatoService campeonatoService = new CampeonatoService(campeonatoRepository);
        sugerenciaService = new SugerenciaService(sugerenciaRepository, campeonatoService);

        campeonatoExistente = new Campeonato();
        campeonatoExistente.setId(1L);
        campeonatoExistente.setNombre("Torneo Primera Malaga");
    }

    private Sugerencia construirSugerenciaConCampeonatoRef() {
        Campeonato campeonatoRef = new Campeonato();
        campeonatoRef.setId(1L);
        Sugerencia sugerencia = new Sugerencia();
        sugerencia.setCampeonato(campeonatoRef);
        sugerencia.setNombreUsuario("Carlos Vega");
        sugerencia.setMensaje("Solicito mejorar el sistema de venta de boletas");
        return sugerencia;
    }

    @Test
    void listarTodas_debeRetornarTodasLasSugerencias() {
        when(sugerenciaRepository.findAll()).thenReturn(List.of(construirSugerenciaConCampeonatoRef()));

        List<Sugerencia> resultado = sugerenciaService.listarTodas();

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_debeLanzarExcepcion_siNoExiste() {
        when(sugerenciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> sugerenciaService.buscarPorId(99L));
    }

    @Test
    void crear_debeAsociarElCampeonatoRealYGuardar() {
        Sugerencia nueva = construirSugerenciaConCampeonatoRef();
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonatoExistente));
        when(sugerenciaRepository.save(nueva)).thenReturn(nueva);

        Sugerencia resultado = sugerenciaService.crear(nueva);

        assertEquals("Carlos Vega", resultado.getNombreUsuario());
        assertEquals("Torneo Primera Malaga", resultado.getCampeonato().getNombre());
        verify(sugerenciaRepository, times(1)).save(nueva);
    }

    @Test
    void crear_debeLanzarExcepcion_siElCampeonatoNoExiste() {
        Sugerencia nueva = construirSugerenciaConCampeonatoRef();
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sugerenciaService.crear(nueva));
        verify(sugerenciaRepository, never()).save(any());
    }

    @Test
    void eliminar_debeBorrarLaSugerencia_siExiste() {
        Sugerencia existente = construirSugerenciaConCampeonatoRef();
        existente.setId(1L);
        when(sugerenciaRepository.findById(1L)).thenReturn(Optional.of(existente));

        sugerenciaService.eliminar(1L);

        verify(sugerenciaRepository, times(1)).delete(existente);
    }

    @Test
    void eliminar_debeLanzarExcepcion_siNoExiste() {
        when(sugerenciaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> sugerenciaService.eliminar(99L));
        verify(sugerenciaRepository, never()).delete(any());
    }
}

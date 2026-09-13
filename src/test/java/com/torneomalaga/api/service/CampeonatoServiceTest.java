package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.repository.CampeonatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampeonatoServiceTest {

    @Mock
    private CampeonatoRepository campeonatoRepository;

    private CampeonatoService campeonatoService;

    @BeforeEach
    void setUp() {
        campeonatoService = new CampeonatoService(campeonatoRepository);
    }

    private Campeonato construirCampeonato() {
        Campeonato c = new Campeonato();
        c.setNombre("Torneo Primera Malaga");
        c.setFechaInicio(LocalDate.of(2026, 3, 1));
        c.setFechaFin(LocalDate.of(2026, 8, 30));
        c.setSede("Estadio Municipal Malaga");
        c.setCategoria("Abierta");
        return c;
    }

    @Test
    void listarTodos_debeRetornarTodosLosCampeonatos() {
        when(campeonatoRepository.findAll()).thenReturn(List.of(construirCampeonato()));

        List<Campeonato> resultado = campeonatoService.listarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_debeLanzarExcepcion_siNoExiste() {
        when(campeonatoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> campeonatoService.buscarPorId(99L));
    }

    @Test
    void crear_debeGuardarYRetornarElCampeonato() {
        Campeonato nuevo = construirCampeonato();
        when(campeonatoRepository.save(nuevo)).thenReturn(nuevo);

        Campeonato resultado = campeonatoService.crear(nuevo);

        assertEquals("Abierta", resultado.getCategoria());
        assertEquals(LocalDate.of(2026, 3, 1), resultado.getFechaInicio());
        verify(campeonatoRepository, times(1)).save(nuevo);
    }

    @Test
    void actualizar_debeModificarTodosLosCampos() {
        Campeonato existente = new Campeonato();
        existente.setId(1L);
        existente.setNombre("Nombre viejo");
        existente.setFechaInicio(LocalDate.of(2025, 1, 1));
        existente.setFechaFin(LocalDate.of(2025, 6, 1));

        Campeonato datosNuevos = construirCampeonato();

        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(campeonatoRepository.save(any(Campeonato.class))).thenAnswer(inv -> inv.getArgument(0));

        Campeonato resultado = campeonatoService.actualizar(1L, datosNuevos);

        assertEquals("Torneo Primera Malaga", resultado.getNombre());
        assertEquals(LocalDate.of(2026, 3, 1), resultado.getFechaInicio());
        assertEquals(LocalDate.of(2026, 8, 30), resultado.getFechaFin());
        assertEquals("Estadio Municipal Malaga", resultado.getSede());
        assertEquals("Abierta", resultado.getCategoria());
    }

    @Test
    void eliminar_debeBorrarElCampeonato_siExiste() {
        Campeonato existente = construirCampeonato();
        existente.setId(1L);
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(existente));

        campeonatoService.eliminar(1L);

        verify(campeonatoRepository, times(1)).delete(existente);
    }

    @Test
    void eliminar_debeLanzarExcepcion_siNoExiste() {
        when(campeonatoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> campeonatoService.eliminar(99L));
        verify(campeonatoRepository, never()).delete(any());
    }
}

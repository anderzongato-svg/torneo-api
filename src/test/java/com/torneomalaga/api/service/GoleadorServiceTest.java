package com.torneomalaga.api.service;

import com.torneomalaga.api.model.*;
import com.torneomalaga.api.repository.*;
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

/**
 * NOTA: goleador se relaciona con Partido (no con Campeonato como se
 * asumia antes). Se encadenan JugadorService, EquipoService,
 * CampeonatoService y PartidoService, cada uno con su repositorio
 * mockeado, para reproducir la cadena real de dependencias.
 */
@ExtendWith(MockitoExtension.class)
class GoleadorServiceTest {

    @Mock
    private GoleadorRepository goleadorRepository;
    @Mock
    private JugadorRepository jugadorRepository;
    @Mock
    private EquipoRepository equipoRepository;
    @Mock
    private CampeonatoRepository campeonatoRepository;
    @Mock
    private PartidoRepository partidoRepository;

    private GoleadorService goleadorService;
    private Jugador jugadorExistente;
    private Partido partidoExistente;

    @BeforeEach
    void setUp() {
        CampeonatoService campeonatoService = new CampeonatoService(campeonatoRepository);
        EquipoService equipoService = new EquipoService(equipoRepository, campeonatoService);
        JugadorService jugadorService = new JugadorService(jugadorRepository, equipoService);
        PartidoService partidoService = new PartidoService(partidoRepository, equipoService, campeonatoService);
        goleadorService = new GoleadorService(goleadorRepository, jugadorService, partidoService);

        jugadorExistente = new Jugador();
        jugadorExistente.setId(1L);
        jugadorExistente.setNombre("Juan Perez");

        partidoExistente = new Partido();
        partidoExistente.setId(1L);
        partidoExistente.setFecha(LocalDate.of(2026, 3, 15));
    }

    @Test
    void listarTodos_debeOrdenarPorGolesDescendente() {
        when(goleadorRepository.findAllByOrderByGolesDesc()).thenReturn(List.of(new Goleador()));

        List<Goleador> resultado = goleadorService.listarTodos();

        assertEquals(1, resultado.size());
        verify(goleadorRepository, times(1)).findAllByOrderByGolesDesc();
    }

    @Test
    void listarPorCampeonato_debeConsultarViaPartidoCampeonato() {
        when(goleadorRepository.findByPartido_Campeonato_IdOrderByGolesDesc(1L)).thenReturn(List.of());

        goleadorService.listarPorCampeonato(1L);

        verify(goleadorRepository, times(1)).findByPartido_Campeonato_IdOrderByGolesDesc(1L);
    }

    @Test
    void crear_debeAsociarJugadorYPartidoRealesYGuardar() {
        Jugador jugadorRef = new Jugador();
        jugadorRef.setId(1L);
        Partido partidoRef = new Partido();
        partidoRef.setId(1L);

        Goleador nuevo = new Goleador();
        nuevo.setJugador(jugadorRef);
        nuevo.setPartido(partidoRef);
        nuevo.setGoles(2);

        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(1L)).thenReturn(Optional.of(partidoExistente));
        when(goleadorRepository.save(any(Goleador.class))).thenAnswer(inv -> inv.getArgument(0));

        Goleador resultado = goleadorService.crear(nuevo);

        assertEquals("Juan Perez", resultado.getJugador().getNombre());
        assertEquals(2, resultado.getGoles());
    }

    @Test
    void actualizar_debeModificarGoles() {
        Goleador existente = new Goleador();
        existente.setId(1L);
        existente.setJugador(jugadorExistente);
        existente.setPartido(partidoExistente);
        existente.setGoles(1);

        Jugador jugadorRef = new Jugador();
        jugadorRef.setId(1L);
        Partido partidoRef = new Partido();
        partidoRef.setId(1L);
        Goleador datosNuevos = new Goleador();
        datosNuevos.setJugador(jugadorRef);
        datosNuevos.setPartido(partidoRef);
        datosNuevos.setGoles(3);

        when(goleadorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugadorExistente));
        when(partidoRepository.findById(1L)).thenReturn(Optional.of(partidoExistente));
        when(goleadorRepository.save(any(Goleador.class))).thenAnswer(inv -> inv.getArgument(0));

        Goleador resultado = goleadorService.actualizar(1L, datosNuevos);

        assertEquals(3, resultado.getGoles());
    }

    @Test
    void eliminar_debeBorrarElGoleador_siExiste() {
        Goleador existente = new Goleador();
        existente.setId(1L);
        when(goleadorRepository.findById(1L)).thenReturn(Optional.of(existente));

        goleadorService.eliminar(1L);

        verify(goleadorRepository, times(1)).delete(existente);
    }
}

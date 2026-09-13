package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.model.Equipo;
import com.torneomalaga.api.model.Jugador;
import com.torneomalaga.api.repository.CampeonatoRepository;
import com.torneomalaga.api.repository.EquipoRepository;
import com.torneomalaga.api.repository.JugadorRepository;
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
 * JugadorService depende de EquipoService, que a su vez ahora depende
 * de CampeonatoService (cada equipo pertenece a un campeonato
 * obligatorio). Se encadenan instancias reales con sus respectivos
 * repositorios mockeados.
 */
@ExtendWith(MockitoExtension.class)
class JugadorServiceTest {

    @Mock
    private JugadorRepository jugadorRepository;
    @Mock
    private EquipoRepository equipoRepository;
    @Mock
    private CampeonatoRepository campeonatoRepository;

    private JugadorService jugadorService;
    private Equipo equipoExistente;

    @BeforeEach
    void setUp() {
        CampeonatoService campeonatoService = new CampeonatoService(campeonatoRepository);
        EquipoService equipoService = new EquipoService(equipoRepository, campeonatoService);
        jugadorService = new JugadorService(jugadorRepository, equipoService);

        equipoExistente = new Equipo();
        equipoExistente.setId(1L);
        equipoExistente.setNombre("Calidosos");
    }

    @Test
    void listarPorEquipo_debeRetornarJugadoresDelEquipo() {
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoExistente));
        Jugador j1 = new Jugador();
        j1.setNombre("Juan Perez");
        when(jugadorRepository.findByEquipoId(1L)).thenReturn(List.of(j1));

        List<Jugador> resultado = jugadorService.listarPorEquipo(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void listarPorEquipo_debeLanzarExcepcion_siElEquipoNoExiste() {
        when(equipoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> jugadorService.listarPorEquipo(99L));
        verify(jugadorRepository, never()).findByEquipoId(any());
    }

    @Test
    void crear_debeAsociarElEquipoRealYGuardar() {
        Equipo equipoReferencia = new Equipo();
        equipoReferencia.setId(1L);

        Jugador nuevo = new Jugador();
        nuevo.setNombre("Juan Perez");
        nuevo.setNumeroCamiseta(10);
        nuevo.setPosicion("Delantero");
        nuevo.setFechaNacimiento(LocalDate.of(2000, 5, 20));
        nuevo.setEquipo(equipoReferencia);

        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoExistente));
        when(jugadorRepository.save(any(Jugador.class))).thenAnswer(inv -> inv.getArgument(0));

        Jugador resultado = jugadorService.crear(nuevo);

        assertEquals("Calidosos", resultado.getEquipo().getNombre());
        assertEquals(10, resultado.getNumeroCamiseta());
    }

    @Test
    void crear_debeLanzarExcepcion_siElEquipoNoExiste() {
        Equipo equipoReferencia = new Equipo();
        equipoReferencia.setId(99L);
        Jugador nuevo = new Jugador();
        nuevo.setEquipo(equipoReferencia);

        when(equipoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jugadorService.crear(nuevo));
        verify(jugadorRepository, never()).save(any());
    }

    @Test
    void actualizar_debeModificarNombreNumeroCamisetaPosicionYEquipo() {
        Jugador existente = new Jugador();
        existente.setId(5L);
        existente.setNombre("Nombre viejo");
        existente.setNumeroCamiseta(7);
        existente.setPosicion("Defensa");
        existente.setEquipo(equipoExistente);

        Equipo nuevoEquipoReferencia = new Equipo();
        nuevoEquipoReferencia.setId(1L);
        Jugador datosNuevos = new Jugador();
        datosNuevos.setNombre("Juan Perez");
        datosNuevos.setNumeroCamiseta(10);
        datosNuevos.setPosicion("Delantero");
        datosNuevos.setEquipo(nuevoEquipoReferencia);

        when(jugadorRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipoExistente));
        when(jugadorRepository.save(any(Jugador.class))).thenAnswer(inv -> inv.getArgument(0));

        Jugador resultado = jugadorService.actualizar(5L, datosNuevos);

        assertEquals("Juan Perez", resultado.getNombre());
        assertEquals(10, resultado.getNumeroCamiseta());
        assertEquals("Delantero", resultado.getPosicion());
    }

    @Test
    void eliminar_debeBorrarElJugador_siExiste() {
        Jugador existente = new Jugador();
        existente.setId(5L);
        when(jugadorRepository.findById(5L)).thenReturn(Optional.of(existente));

        jugadorService.eliminar(5L);

        verify(jugadorRepository, times(1)).delete(existente);
    }
}

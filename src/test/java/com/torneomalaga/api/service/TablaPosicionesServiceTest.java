package com.torneomalaga.api.service;

import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.model.Equipo;
import com.torneomalaga.api.model.Partido;
import com.torneomalaga.api.model.TablaPosiciones;
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
 * TablaPosicionesService.recalcular() aplica las reglas de puntaje de
 * futbol (3 pts victoria, 1 pt empate, 0 pts derrota). NOTA: los campos
 * ganados/empatados/perdidos ahora se llaman partidosGanados/
 * partidosEmpatados/partidosPerdidos, y PartidoService ya no depende
 * de ArbitroService.
 */
@ExtendWith(MockitoExtension.class)
class TablaPosicionesServiceTest {

    @Mock
    private TablaPosicionesRepository tablaPosicionesRepository;
    @Mock
    private PartidoRepository partidoRepository;
    @Mock
    private EquipoRepository equipoRepository;
    @Mock
    private CampeonatoRepository campeonatoRepository;

    private TablaPosicionesService tablaPosicionesService;

    private Campeonato campeonato;
    private Equipo calidosos;
    private Equipo deportivoConcepcion;

    @BeforeEach
    void setUp() {
        CampeonatoService campeonatoService = new CampeonatoService(campeonatoRepository);
        EquipoService equipoService = new EquipoService(equipoRepository, campeonatoService);
        PartidoService partidoService = new PartidoService(partidoRepository, equipoService, campeonatoService);
        tablaPosicionesService = new TablaPosicionesService(
                tablaPosicionesRepository, partidoService, equipoService, campeonatoService);

        campeonato = new Campeonato();
        campeonato.setId(1L);
        campeonato.setNombre("Torneo Primera Malaga");
        campeonato.setFechaInicio(LocalDate.of(2026, 3, 1));
        campeonato.setFechaFin(LocalDate.of(2026, 8, 30));

        calidosos = new Equipo();
        calidosos.setId(1L);
        calidosos.setNombre("Calidosos");
        calidosos.setCampeonato(campeonato);

        deportivoConcepcion = new Equipo();
        deportivoConcepcion.setId(2L);
        deportivoConcepcion.setNombre("Deportivo Concepcion");
        deportivoConcepcion.setCampeonato(campeonato);
    }

    private Partido crearPartidoFinalizado(int golesLocal, int golesVisitante) {
        Partido partido = new Partido();
        partido.setCampeonato(campeonato);
        partido.setFecha(LocalDate.of(2026, 3, 15));
        partido.setEquipoLocal(calidosos);
        partido.setEquipoVisitante(deportivoConcepcion);
        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setEstado("Finalizado");
        return partido;
    }

    @Test
    void recalcular_victoriaLocal_debeDar3PuntosAlLocalY0AlVisitante() {
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));
        when(partidoRepository.findByCampeonatoIdAndEstadoIgnoreCase(1L, "Finalizado"))
                .thenReturn(List.of(crearPartidoFinalizado(3, 1)));
        when(tablaPosicionesRepository.findByEquipoIdAndCampeonatoId(anyLong(), eq(1L)))
                .thenReturn(Optional.empty());
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(calidosos));
        when(equipoRepository.findById(2L)).thenReturn(Optional.of(deportivoConcepcion));
        when(tablaPosicionesRepository.save(any(TablaPosiciones.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tablaPosicionesRepository.findByCampeonatoIdOrderByPuntosDesc(1L)).thenReturn(List.of());

        tablaPosicionesService.recalcular(1L);

        org.mockito.ArgumentCaptor<TablaPosiciones> captor = org.mockito.ArgumentCaptor.forClass(TablaPosiciones.class);
        verify(tablaPosicionesRepository, times(2)).save(captor.capture());

        TablaPosiciones registroLocal = captor.getAllValues().stream()
                .filter(r -> r.getEquipo().getId().equals(1L)).findFirst().orElseThrow();
        TablaPosiciones registroVisitante = captor.getAllValues().stream()
                .filter(r -> r.getEquipo().getId().equals(2L)).findFirst().orElseThrow();

        assertEquals(3, registroLocal.getPuntos());
        assertEquals(1, registroLocal.getPartidosGanados());
        assertEquals(0, registroVisitante.getPuntos());
        assertEquals(1, registroVisitante.getPartidosPerdidos());
    }

    @Test
    void recalcular_empate_debeDar1PuntoACadaEquipo() {
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));
        when(partidoRepository.findByCampeonatoIdAndEstadoIgnoreCase(1L, "Finalizado"))
                .thenReturn(List.of(crearPartidoFinalizado(2, 2)));
        when(tablaPosicionesRepository.findByEquipoIdAndCampeonatoId(anyLong(), eq(1L)))
                .thenReturn(Optional.empty());
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(calidosos));
        when(equipoRepository.findById(2L)).thenReturn(Optional.of(deportivoConcepcion));
        when(tablaPosicionesRepository.save(any(TablaPosiciones.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tablaPosicionesRepository.findByCampeonatoIdOrderByPuntosDesc(1L)).thenReturn(List.of());

        tablaPosicionesService.recalcular(1L);

        org.mockito.ArgumentCaptor<TablaPosiciones> captor = org.mockito.ArgumentCaptor.forClass(TablaPosiciones.class);
        verify(tablaPosicionesRepository, times(2)).save(captor.capture());

        captor.getAllValues().forEach(registro -> {
            assertEquals(1, registro.getPuntos());
            assertEquals(1, registro.getPartidosEmpatados());
        });
    }

    @Test
    void recalcular_debeAcumularSobreUnRegistroExistente_enVezDeDuplicar() {
        TablaPosiciones registroPrevio = new TablaPosiciones();
        registroPrevio.setId(50L);
        registroPrevio.setEquipo(calidosos);
        registroPrevio.setCampeonato(campeonato);
        registroPrevio.setPartidosJugados(1);
        registroPrevio.setPartidosGanados(1);
        registroPrevio.setPuntos(3);
        registroPrevio.setGolesFavor(2);
        registroPrevio.setGolesContra(0);

        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));
        when(partidoRepository.findByCampeonatoIdAndEstadoIgnoreCase(1L, "Finalizado"))
                .thenReturn(List.of(crearPartidoFinalizado(1, 1)));

        when(tablaPosicionesRepository.findByEquipoIdAndCampeonatoId(1L, 1L))
                .thenReturn(Optional.of(registroPrevio));
        when(tablaPosicionesRepository.findByEquipoIdAndCampeonatoId(2L, 1L))
                .thenReturn(Optional.empty());
        when(equipoRepository.findById(2L)).thenReturn(Optional.of(deportivoConcepcion));
        when(tablaPosicionesRepository.save(any(TablaPosiciones.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tablaPosicionesRepository.findByCampeonatoIdOrderByPuntosDesc(1L)).thenReturn(List.of());

        tablaPosicionesService.recalcular(1L);

        assertEquals(2, registroPrevio.getPartidosJugados());
        assertEquals(4, registroPrevio.getPuntos());
        assertEquals(1, registroPrevio.getPartidosGanados());
        assertEquals(1, registroPrevio.getPartidosEmpatados());
    }

    @Test
    void actualizar_debeModificarTodasLasEstadisticas() {
        TablaPosiciones existente = new TablaPosiciones();
        existente.setId(1L);
        existente.setEquipo(calidosos);
        existente.setCampeonato(campeonato);

        TablaPosiciones datosNuevos = new TablaPosiciones();
        datosNuevos.setEquipo(calidosos);
        datosNuevos.setCampeonato(campeonato);
        datosNuevos.setPartidosJugados(5);
        datosNuevos.setPartidosGanados(3);
        datosNuevos.setPartidosEmpatados(1);
        datosNuevos.setPartidosPerdidos(1);
        datosNuevos.setGolesFavor(10);
        datosNuevos.setGolesContra(5);
        datosNuevos.setPuntos(10);

        when(tablaPosicionesRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(tablaPosicionesRepository.save(any(TablaPosiciones.class))).thenAnswer(inv -> inv.getArgument(0));

        TablaPosiciones resultado = tablaPosicionesService.actualizar(1L, datosNuevos);

        assertEquals(5, resultado.getPartidosJugados());
        assertEquals(10, resultado.getPuntos());
    }

    @Test
    void eliminar_debeBorrarElRegistro_siExiste() {
        TablaPosiciones existente = new TablaPosiciones();
        existente.setId(1L);
        when(tablaPosicionesRepository.findById(1L)).thenReturn(Optional.of(existente));

        tablaPosicionesService.eliminar(1L);

        verify(tablaPosicionesRepository, times(1)).delete(existente);
    }
}

package com.torneomalaga.api.service;

import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.model.Equipo;
import com.torneomalaga.api.model.Partido;
import com.torneomalaga.api.repository.CampeonatoRepository;
import com.torneomalaga.api.repository.EquipoRepository;
import com.torneomalaga.api.repository.PartidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * NOTA: PartidoService ya no depende de ArbitroService (la tabla real
 * partido no tiene relacion con arbitro). Campeonato es obligatorio.
 */
@ExtendWith(MockitoExtension.class)
class PartidoServiceTest {

    @Mock
    private PartidoRepository partidoRepository;
    @Mock
    private EquipoRepository equipoRepository;
    @Mock
    private CampeonatoRepository campeonatoRepository;

    private PartidoService partidoService;

    private Campeonato campeonato;
    private Equipo local;
    private Equipo visitante;

    @BeforeEach
    void setUp() {
        CampeonatoService campeonatoService = new CampeonatoService(campeonatoRepository);
        EquipoService equipoService = new EquipoService(equipoRepository, campeonatoService);
        partidoService = new PartidoService(partidoRepository, equipoService, campeonatoService);

        campeonato = new Campeonato();
        campeonato.setId(1L);
        campeonato.setNombre("Torneo Primera Malaga");

        local = new Equipo();
        local.setId(1L);
        local.setNombre("Calidosos");

        visitante = new Equipo();
        visitante.setId(2L);
        visitante.setNombre("Deportivo Concepcion");
    }

    private Partido construirPartidoValido() {
        Campeonato campeonatoRef = new Campeonato();
        campeonatoRef.setId(1L);
        Equipo localRef = new Equipo();
        localRef.setId(1L);
        Equipo visitanteRef = new Equipo();
        visitanteRef.setId(2L);

        Partido partido = new Partido();
        partido.setCampeonato(campeonatoRef);
        partido.setFecha(LocalDate.of(2026, 3, 15));
        partido.setHora(LocalTime.of(16, 0));
        partido.setEquipoLocal(localRef);
        partido.setEquipoVisitante(visitanteRef);
        return partido;
    }

    @Test
    void crear_debeGuardarUnPartidoValido() {
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(local));
        when(equipoRepository.findById(2L)).thenReturn(Optional.of(visitante));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(inv -> inv.getArgument(0));

        Partido resultado = partidoService.crear(construirPartidoValido());

        assertEquals("Calidosos", resultado.getEquipoLocal().getNombre());
        assertEquals("Deportivo Concepcion", resultado.getEquipoVisitante().getNombre());
        assertEquals(LocalTime.of(16, 0), resultado.getHora());
    }

    @Test
    void crear_debeLanzarExcepcion_siElEquipoLocalYVisitanteSonElMismo() {
        Campeonato campeonatoRef = new Campeonato();
        campeonatoRef.setId(1L);
        Equipo mismoEquipoRef1 = new Equipo();
        mismoEquipoRef1.setId(1L);
        Equipo mismoEquipoRef2 = new Equipo();
        mismoEquipoRef2.setId(1L);

        Partido partido = new Partido();
        partido.setCampeonato(campeonatoRef);
        partido.setFecha(LocalDate.of(2026, 3, 15));
        partido.setEquipoLocal(mismoEquipoRef1);
        partido.setEquipoVisitante(mismoEquipoRef2);

        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(local));

        assertThrows(IllegalArgumentException.class, () -> partidoService.crear(partido));
        verify(partidoRepository, never()).save(any());
    }

    @Test
    void crear_debeLanzarExcepcion_siElCampeonatoNoExiste() {
        Partido partido = construirPartidoValido();
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(com.torneomalaga.api.exception.ResourceNotFoundException.class,
                () -> partidoService.crear(partido));
        verify(partidoRepository, never()).save(any());
    }

    @Test
    void actualizar_debeModificarGolesYEstado() {
        Partido existente = construirPartidoValido();
        existente.setId(10L);

        Partido datosNuevos = construirPartidoValido();
        datosNuevos.setGolesLocal(3);
        datosNuevos.setGolesVisitante(1);
        datosNuevos.setEstado("Finalizado");

        when(partidoRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(local));
        when(equipoRepository.findById(2L)).thenReturn(Optional.of(visitante));
        when(partidoRepository.save(any(Partido.class))).thenAnswer(inv -> inv.getArgument(0));

        Partido resultado = partidoService.actualizar(10L, datosNuevos);

        assertEquals(3, resultado.getGolesLocal());
        assertEquals(1, resultado.getGolesVisitante());
        assertEquals("Finalizado", resultado.getEstado());
    }

    @Test
    void listarFinalizadosPorCampeonato_debeConsultarConElEstadoCorrecto() {
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonato));
        when(partidoRepository.findByCampeonatoIdAndEstadoIgnoreCase(1L, "Finalizado"))
                .thenReturn(java.util.List.of());

        partidoService.listarFinalizadosPorCampeonato(1L);

        verify(partidoRepository, times(1)).findByCampeonatoIdAndEstadoIgnoreCase(1L, "Finalizado");
    }
}

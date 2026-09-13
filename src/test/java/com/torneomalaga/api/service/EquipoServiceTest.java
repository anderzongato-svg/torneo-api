package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.model.Equipo;
import com.torneomalaga.api.repository.CampeonatoRepository;
import com.torneomalaga.api.repository.EquipoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * EquipoService depende de CampeonatoService (cada equipo pertenece a
 * un campeonato obligatorio). Se usa una instancia real de
 * CampeonatoService con su propio repositorio mockeado.
 */
@ExtendWith(MockitoExtension.class)
class EquipoServiceTest {

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private CampeonatoRepository campeonatoRepository;

    private EquipoService equipoService;
    private Campeonato campeonatoExistente;

    @BeforeEach
    void setUp() {
        CampeonatoService campeonatoService = new CampeonatoService(campeonatoRepository);
        equipoService = new EquipoService(equipoRepository, campeonatoService);

        campeonatoExistente = new Campeonato();
        campeonatoExistente.setId(1L);
        campeonatoExistente.setNombre("Torneo Primera Malaga");
    }

    private Equipo construirEquipoConCampeonatoRef() {
        Campeonato campeonatoRef = new Campeonato();
        campeonatoRef.setId(1L);
        Equipo equipo = new Equipo();
        equipo.setCampeonato(campeonatoRef);
        equipo.setNombre("Calidosos");
        equipo.setCiudad("Malaga");
        equipo.setDt("Carlos Ramirez");
        equipo.setColores("Rojo y Blanco");
        equipo.setEstadio("Estadio Municipal Malaga");
        return equipo;
    }

    @Test
    void listarTodos_debeRetornarTodosLosEquipos() {
        when(equipoRepository.findAll()).thenReturn(List.of(construirEquipoConCampeonatoRef()));

        List<Equipo> resultado = equipoService.listarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_debeRetornarElEquipo_siExiste() {
        Equipo equipo = construirEquipoConCampeonatoRef();
        equipo.setId(1L);
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(equipo));

        Equipo resultado = equipoService.buscarPorId(1L);

        assertEquals("Calidosos", resultado.getNombre());
    }

    @Test
    void buscarPorId_debeLanzarExcepcion_siNoExiste() {
        when(equipoRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> equipoService.buscarPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void crear_debeAsociarElCampeonatoRealYGuardar() {
        Equipo nuevo = construirEquipoConCampeonatoRef();
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonatoExistente));
        when(equipoRepository.save(nuevo)).thenReturn(nuevo);

        Equipo resultado = equipoService.crear(nuevo);

        assertEquals("Torneo Primera Malaga", resultado.getCampeonato().getNombre());
        verify(equipoRepository, times(1)).save(nuevo);
    }

    @Test
    void crear_debeLanzarExcepcion_siElCampeonatoNoExiste() {
        Equipo nuevo = construirEquipoConCampeonatoRef();
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> equipoService.crear(nuevo));
        verify(equipoRepository, never()).save(any());
    }

    @Test
    void actualizar_debeModificarLosCamposReales() {
        Equipo existente = new Equipo();
        existente.setId(1L);
        existente.setCampeonato(campeonatoExistente);
        existente.setNombre("Nombre viejo");
        existente.setCiudad("Ciudad vieja");
        existente.setDt("DT viejo");
        existente.setColores("Colores viejos");
        existente.setEstadio("Estadio viejo");

        Equipo datosNuevos = construirEquipoConCampeonatoRef();

        when(equipoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonatoExistente));
        when(equipoRepository.save(any(Equipo.class))).thenAnswer(inv -> inv.getArgument(0));

        Equipo resultado = equipoService.actualizar(1L, datosNuevos);

        assertEquals("Calidosos", resultado.getNombre());
        assertEquals("Malaga", resultado.getCiudad());
        assertEquals("Carlos Ramirez", resultado.getDt());
        assertEquals("Rojo y Blanco", resultado.getColores());
        assertEquals("Estadio Municipal Malaga", resultado.getEstadio());
    }

    @Test
    void actualizar_debeLanzarExcepcion_siElEquipoNoExiste() {
        when(equipoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipoService.actualizar(99L, construirEquipoConCampeonatoRef()));
        verify(equipoRepository, never()).save(any());
    }

    @Test
    void eliminar_debeBorrarElEquipo_siExiste() {
        Equipo existente = construirEquipoConCampeonatoRef();
        existente.setId(1L);
        when(equipoRepository.findById(1L)).thenReturn(Optional.of(existente));

        equipoService.eliminar(1L);

        verify(equipoRepository, times(1)).delete(existente);
    }

    @Test
    void eliminar_debeLanzarExcepcion_siNoExiste() {
        when(equipoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> equipoService.eliminar(99L));
        verify(equipoRepository, never()).delete(any());
    }
}

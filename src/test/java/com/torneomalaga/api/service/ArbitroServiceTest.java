package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Arbitro;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.repository.ArbitroRepository;
import com.torneomalaga.api.repository.CampeonatoRepository;
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
class ArbitroServiceTest {

    @Mock
    private ArbitroRepository arbitroRepository;

    @Mock
    private CampeonatoRepository campeonatoRepository;

    private ArbitroService arbitroService;
    private Campeonato campeonatoExistente;

    @BeforeEach
    void setUp() {
        CampeonatoService campeonatoService = new CampeonatoService(campeonatoRepository);
        arbitroService = new ArbitroService(arbitroRepository, campeonatoService);

        campeonatoExistente = new Campeonato();
        campeonatoExistente.setId(1L);
        campeonatoExistente.setNombre("Torneo Primera Malaga");
    }

    private Arbitro construirArbitroConCampeonatoRef() {
        Campeonato campeonatoRef = new Campeonato();
        campeonatoRef.setId(1L);
        Arbitro arbitro = new Arbitro();
        arbitro.setCampeonato(campeonatoRef);
        arbitro.setNombre("Roberto Cardenas");
        arbitro.setRol("Central");
        arbitro.setLicencia("ARB-001");
        return arbitro;
    }

    @Test
    void listarTodos_debeRetornarTodosLosArbitros() {
        when(arbitroRepository.findAll()).thenReturn(List.of(construirArbitroConCampeonatoRef()));

        List<Arbitro> resultado = arbitroService.listarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_debeRetornarElArbitro_siExiste() {
        Arbitro arbitro = construirArbitroConCampeonatoRef();
        arbitro.setId(1L);
        when(arbitroRepository.findById(1L)).thenReturn(Optional.of(arbitro));

        Arbitro resultado = arbitroService.buscarPorId(1L);

        assertEquals("Roberto Cardenas", resultado.getNombre());
    }

    @Test
    void buscarPorId_debeLanzarExcepcion_siNoExiste() {
        when(arbitroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> arbitroService.buscarPorId(99L));
    }

    @Test
    void crear_debeAsociarElCampeonatoRealYGuardar() {
        Arbitro nuevo = construirArbitroConCampeonatoRef();
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonatoExistente));
        when(arbitroRepository.save(nuevo)).thenReturn(nuevo);

        Arbitro resultado = arbitroService.crear(nuevo);

        assertEquals("Torneo Primera Malaga", resultado.getCampeonato().getNombre());
        verify(arbitroRepository, times(1)).save(nuevo);
    }

    @Test
    void actualizar_debeModificarLosCamposReales() {
        Arbitro existente = new Arbitro();
        existente.setId(1L);
        existente.setCampeonato(campeonatoExistente);
        existente.setNombre("Nombre viejo");
        existente.setRol("Asistente 1");
        existente.setLicencia("ARB-000");

        Arbitro datosNuevos = construirArbitroConCampeonatoRef();

        when(arbitroRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(campeonatoRepository.findById(1L)).thenReturn(Optional.of(campeonatoExistente));
        when(arbitroRepository.save(any(Arbitro.class))).thenAnswer(inv -> inv.getArgument(0));

        Arbitro resultado = arbitroService.actualizar(1L, datosNuevos);

        assertEquals("Roberto Cardenas", resultado.getNombre());
        assertEquals("Central", resultado.getRol());
        assertEquals("ARB-001", resultado.getLicencia());
    }

    @Test
    void actualizar_debeLanzarExcepcion_siElArbitroNoExiste() {
        when(arbitroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> arbitroService.actualizar(99L, construirArbitroConCampeonatoRef()));
        verify(arbitroRepository, never()).save(any());
    }

    @Test
    void eliminar_debeBorrarElArbitro_siExiste() {
        Arbitro existente = construirArbitroConCampeonatoRef();
        existente.setId(1L);
        when(arbitroRepository.findById(1L)).thenReturn(Optional.of(existente));

        arbitroService.eliminar(1L);

        verify(arbitroRepository, times(1)).delete(existente);
    }

    @Test
    void eliminar_debeLanzarExcepcion_siNoExiste() {
        when(arbitroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> arbitroService.eliminar(99L));
        verify(arbitroRepository, never()).delete(any());
    }
}

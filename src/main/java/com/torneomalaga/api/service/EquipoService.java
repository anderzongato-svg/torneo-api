package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.model.Equipo;
import com.torneomalaga.api.repository.EquipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Reglas de negocio para el recurso Equipo. Cada equipo pertenece a
 * un campeonato (id_campeonato es obligatorio en la BD real), por
 * eso este servicio depende de CampeonatoService para validarlo.
 */
@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final CampeonatoService campeonatoService;

    public EquipoService(EquipoRepository equipoRepository, CampeonatoService campeonatoService) {
        this.equipoRepository = equipoRepository;
        this.campeonatoService = campeonatoService;
    }

    public List<Equipo> listarTodos() {
        return equipoRepository.findAll();
    }

    public List<Equipo> listarPorCampeonato(Long campeonatoId) {
        campeonatoService.buscarPorId(campeonatoId);
        return equipoRepository.findByCampeonatoId(campeonatoId);
    }

    public Equipo buscarPorId(Long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", id));
    }

    public Equipo crear(Equipo equipo) {
        asociarCampeonato(equipo);
        return equipoRepository.save(equipo);
    }

    public Equipo actualizar(Long id, Equipo datos) {
        Equipo existente = buscarPorId(id);
        asociarCampeonato(datos);
        existente.setCampeonato(datos.getCampeonato());
        existente.setNombre(datos.getNombre());
        existente.setCiudad(datos.getCiudad());
        existente.setDt(datos.getDt());
        existente.setColores(datos.getColores());
        existente.setEstadio(datos.getEstadio());
        return equipoRepository.save(existente);
    }

    public void eliminar(Long id) {
        Equipo existente = buscarPorId(id);
        equipoRepository.delete(existente);
    }

    private void asociarCampeonato(Equipo equipo) {
        Campeonato campeonato = campeonatoService.buscarPorId(equipo.getCampeonato().getId());
        equipo.setCampeonato(campeonato);
    }
}

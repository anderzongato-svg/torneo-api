package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.repository.CampeonatoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampeonatoService {

    private final CampeonatoRepository campeonatoRepository;

    public CampeonatoService(CampeonatoRepository campeonatoRepository) {
        this.campeonatoRepository = campeonatoRepository;
    }

    public List<Campeonato> listarTodos() {
        return campeonatoRepository.findAll();
    }

    public Campeonato buscarPorId(Long id) {
        return campeonatoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campeonato", id));
    }

    public Campeonato crear(Campeonato campeonato) {
        return campeonatoRepository.save(campeonato);
    }

    public Campeonato actualizar(Long id, Campeonato datos) {
        Campeonato existente = buscarPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setFechaInicio(datos.getFechaInicio());
        existente.setFechaFin(datos.getFechaFin());
        existente.setSede(datos.getSede());
        existente.setCategoria(datos.getCategoria());
        return campeonatoRepository.save(existente);
    }

    public void eliminar(Long id) {
        Campeonato existente = buscarPorId(id);
        campeonatoRepository.delete(existente);
    }
}

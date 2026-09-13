package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Arbitro;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.repository.ArbitroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Cada arbitro pertenece a un campeonato (id_campeonato es obligatorio
 * en la BD real), por eso depende de CampeonatoService para validarlo.
 */
@Service
public class ArbitroService {

    private final ArbitroRepository arbitroRepository;
    private final CampeonatoService campeonatoService;

    public ArbitroService(ArbitroRepository arbitroRepository, CampeonatoService campeonatoService) {
        this.arbitroRepository = arbitroRepository;
        this.campeonatoService = campeonatoService;
    }

    public List<Arbitro> listarTodos() {
        return arbitroRepository.findAll();
    }

    public List<Arbitro> listarPorCampeonato(Long campeonatoId) {
        campeonatoService.buscarPorId(campeonatoId);
        return arbitroRepository.findByCampeonatoId(campeonatoId);
    }

    public Arbitro buscarPorId(Long id) {
        return arbitroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arbitro", id));
    }

    public Arbitro crear(Arbitro arbitro) {
        asociarCampeonato(arbitro);
        return arbitroRepository.save(arbitro);
    }

    public Arbitro actualizar(Long id, Arbitro datos) {
        Arbitro existente = buscarPorId(id);
        asociarCampeonato(datos);
        existente.setCampeonato(datos.getCampeonato());
        existente.setNombre(datos.getNombre());
        existente.setRol(datos.getRol());
        existente.setLicencia(datos.getLicencia());
        return arbitroRepository.save(existente);
    }

    public void eliminar(Long id) {
        Arbitro existente = buscarPorId(id);
        arbitroRepository.delete(existente);
    }

    private void asociarCampeonato(Arbitro arbitro) {
        Campeonato campeonato = campeonatoService.buscarPorId(arbitro.getCampeonato().getId());
        arbitro.setCampeonato(campeonato);
    }
}

package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.model.Sugerencia;
import com.torneomalaga.api.repository.SugerenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Cada sugerencia pertenece a un campeonato (id_campeonato es
 * obligatorio en la BD real). No tiene actualizar(): una sugerencia
 * enviada no se edita, solo se consulta o se elimina.
 */
@Service
public class SugerenciaService {

    private final SugerenciaRepository sugerenciaRepository;
    private final CampeonatoService campeonatoService;

    public SugerenciaService(SugerenciaRepository sugerenciaRepository, CampeonatoService campeonatoService) {
        this.sugerenciaRepository = sugerenciaRepository;
        this.campeonatoService = campeonatoService;
    }

    public List<Sugerencia> listarTodas() {
        return sugerenciaRepository.findAll();
    }

    public List<Sugerencia> listarPorCampeonato(Long campeonatoId) {
        campeonatoService.buscarPorId(campeonatoId);
        return sugerenciaRepository.findByCampeonatoId(campeonatoId);
    }

    public Sugerencia buscarPorId(Long id) {
        return sugerenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sugerencia", id));
    }

    public Sugerencia crear(Sugerencia sugerencia) {
        Campeonato campeonato = campeonatoService.buscarPorId(sugerencia.getCampeonato().getId());
        sugerencia.setCampeonato(campeonato);
        return sugerenciaRepository.save(sugerencia);
    }

    public void eliminar(Long id) {
        Sugerencia existente = buscarPorId(id);
        sugerenciaRepository.delete(existente);
    }
}

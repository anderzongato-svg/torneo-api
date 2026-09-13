package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Goleador;
import com.torneomalaga.api.model.Jugador;
import com.torneomalaga.api.model.Partido;
import com.torneomalaga.api.repository.GoleadorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NOTA: goleador se relaciona con partido (no directo con campeonato,
 * como se asumia antes) -- cada fila representa los goles de un
 * jugador en un partido especifico. Para "por campeonato" se navega
 * a traves de partido.campeonato.
 */
@Service
public class GoleadorService {

    private final GoleadorRepository goleadorRepository;
    private final JugadorService jugadorService;
    private final PartidoService partidoService;

    public GoleadorService(GoleadorRepository goleadorRepository, JugadorService jugadorService, PartidoService partidoService) {
        this.goleadorRepository = goleadorRepository;
        this.jugadorService = jugadorService;
        this.partidoService = partidoService;
    }

    public List<Goleador> listarTodos() {
        return goleadorRepository.findAllByOrderByGolesDesc();
    }

    public List<Goleador> listarPorCampeonato(Long campeonatoId) {
        return goleadorRepository.findByPartido_Campeonato_IdOrderByGolesDesc(campeonatoId);
    }

    public List<Goleador> listarPorPartido(Long partidoId) {
        partidoService.buscarPorId(partidoId);
        return goleadorRepository.findByPartidoId(partidoId);
    }

    public Goleador buscarPorId(Long id) {
        return goleadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goleador", id));
    }

    public Goleador crear(Goleador goleador) {
        asociar(goleador);
        return goleadorRepository.save(goleador);
    }

    public Goleador actualizar(Long id, Goleador datos) {
        Goleador existente = buscarPorId(id);
        asociar(datos);
        existente.setJugador(datos.getJugador());
        existente.setPartido(datos.getPartido());
        existente.setGoles(datos.getGoles());
        return goleadorRepository.save(existente);
    }

    public void eliminar(Long id) {
        Goleador existente = buscarPorId(id);
        goleadorRepository.delete(existente);
    }

    private void asociar(Goleador goleador) {
        Jugador jugador = jugadorService.buscarPorId(goleador.getJugador().getId());
        goleador.setJugador(jugador);
        Partido partido = partidoService.buscarPorId(goleador.getPartido().getId());
        goleador.setPartido(partido);
    }
}

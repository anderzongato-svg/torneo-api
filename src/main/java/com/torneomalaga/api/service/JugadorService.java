package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Equipo;
import com.torneomalaga.api.model.Jugador;
import com.torneomalaga.api.repository.JugadorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final EquipoService equipoService;

    public JugadorService(JugadorRepository jugadorRepository, EquipoService equipoService) {
        this.jugadorRepository = jugadorRepository;
        this.equipoService = equipoService;
    }

    public List<Jugador> listarTodos() {
        return jugadorRepository.findAll();
    }

    public List<Jugador> listarPorEquipo(Long equipoId) {
        equipoService.buscarPorId(equipoId); // valida que el equipo exista
        return jugadorRepository.findByEquipoId(equipoId);
    }

    public Jugador buscarPorId(Long id) {
        return jugadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador", id));
    }

    public Jugador crear(Jugador jugador) {
        Equipo equipo = equipoService.buscarPorId(jugador.getEquipo().getId());
        jugador.setEquipo(equipo);
        return jugadorRepository.save(jugador);
    }

    public Jugador actualizar(Long id, Jugador datos) {
        Jugador existente = buscarPorId(id);
        Equipo equipo = equipoService.buscarPorId(datos.getEquipo().getId());
        existente.setNombre(datos.getNombre());
        existente.setNumeroCamiseta(datos.getNumeroCamiseta());
        existente.setPosicion(datos.getPosicion());
        existente.setFechaNacimiento(datos.getFechaNacimiento());
        existente.setEquipo(equipo);
        return jugadorRepository.save(existente);
    }

    public void eliminar(Long id) {
        Jugador existente = buscarPorId(id);
        jugadorRepository.delete(existente);
    }
}

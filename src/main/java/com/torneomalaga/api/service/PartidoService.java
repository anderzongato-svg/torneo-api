package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.model.Equipo;
import com.torneomalaga.api.model.Partido;
import com.torneomalaga.api.repository.PartidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NOTA: la tabla partido real no tiene relacion con arbitro (se
 * elimino ese campo de la version anterior asumida). El campeonato,
 * en cambio, si es obligatorio.
 */
@Service
public class PartidoService {

    private final PartidoRepository partidoRepository;
    private final EquipoService equipoService;
    private final CampeonatoService campeonatoService;

    public PartidoService(PartidoRepository partidoRepository, EquipoService equipoService,
                           CampeonatoService campeonatoService) {
        this.partidoRepository = partidoRepository;
        this.equipoService = equipoService;
        this.campeonatoService = campeonatoService;
    }

    public List<Partido> listarTodos() {
        return partidoRepository.findAll();
    }

    public List<Partido> listarPorEquipo(Long equipoId) {
        equipoService.buscarPorId(equipoId);
        return partidoRepository.findByEquipoLocalIdOrEquipoVisitanteId(equipoId, equipoId);
    }

    public List<Partido> listarFinalizadosPorCampeonato(Long campeonatoId) {
        campeonatoService.buscarPorId(campeonatoId);
        return partidoRepository.findByCampeonatoIdAndEstadoIgnoreCase(campeonatoId, "Finalizado");
    }

    public Partido buscarPorId(Long id) {
        return partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido", id));
    }

    public Partido crear(Partido partido) {
        validarYAsociar(partido);
        return partidoRepository.save(partido);
    }

    public Partido actualizar(Long id, Partido datos) {
        Partido existente = buscarPorId(id);
        validarYAsociar(datos);
        existente.setFecha(datos.getFecha());
        existente.setHora(datos.getHora());
        existente.setCampeonato(datos.getCampeonato());
        existente.setEquipoLocal(datos.getEquipoLocal());
        existente.setEquipoVisitante(datos.getEquipoVisitante());
        existente.setGolesLocal(datos.getGolesLocal());
        existente.setGolesVisitante(datos.getGolesVisitante());
        existente.setEstado(datos.getEstado());
        return partidoRepository.save(existente);
    }

    public void eliminar(Long id) {
        Partido existente = buscarPorId(id);
        partidoRepository.delete(existente);
    }

    /** Verifica que campeonato y equipos existan, y que un equipo no juegue contra si mismo. */
    private void validarYAsociar(Partido partido) {
        Campeonato campeonato = campeonatoService.buscarPorId(partido.getCampeonato().getId());
        Equipo local = equipoService.buscarPorId(partido.getEquipoLocal().getId());
        Equipo visitante = equipoService.buscarPorId(partido.getEquipoVisitante().getId());
        if (local.getId().equals(visitante.getId())) {
            throw new IllegalArgumentException("El equipo local y el visitante no pueden ser el mismo");
        }
        partido.setCampeonato(campeonato);
        partido.setEquipoLocal(local);
        partido.setEquipoVisitante(visitante);
    }
}

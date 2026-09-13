package com.torneomalaga.api.service;

import com.torneomalaga.api.exception.ResourceNotFoundException;
import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.model.Partido;
import com.torneomalaga.api.model.TablaPosiciones;
import com.torneomalaga.api.repository.TablaPosicionesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * A diferencia de la primera version de esta API, la tabla de posiciones
 * aqui SI se persiste en su propia tabla (tabla_posiciones), tal como esta
 * en el esquema real. Este servicio permite tanto administrarla via CRUD
 * como recalcularla automaticamente a partir de los partidos finalizados
 * de un campeonato, para que no haya que actualizarla fila por fila a mano.
 */
@Service
public class TablaPosicionesService {

    private final TablaPosicionesRepository tablaPosicionesRepository;
    private final PartidoService partidoService;
    private final EquipoService equipoService;
    private final CampeonatoService campeonatoService;

    public TablaPosicionesService(TablaPosicionesRepository tablaPosicionesRepository, PartidoService partidoService,
                                   EquipoService equipoService, CampeonatoService campeonatoService) {
        this.tablaPosicionesRepository = tablaPosicionesRepository;
        this.partidoService = partidoService;
        this.equipoService = equipoService;
        this.campeonatoService = campeonatoService;
    }

    public List<TablaPosiciones> listarTodos() {
        return tablaPosicionesRepository.findAllByOrderByPuntosDesc();
    }

    public List<TablaPosiciones> listarPorCampeonato(Long campeonatoId) {
        campeonatoService.buscarPorId(campeonatoId);
        return tablaPosicionesRepository.findByCampeonatoIdOrderByPuntosDesc(campeonatoId);
    }

    public TablaPosiciones buscarPorId(Long id) {
        return tablaPosicionesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de tabla de posiciones", id));
    }

    public TablaPosiciones crear(TablaPosiciones registro) {
        return tablaPosicionesRepository.save(registro);
    }

    public TablaPosiciones actualizar(Long id, TablaPosiciones datos) {
        TablaPosiciones existente = buscarPorId(id);
        existente.setEquipo(datos.getEquipo());
        existente.setCampeonato(datos.getCampeonato());
        existente.setPartidosJugados(datos.getPartidosJugados());
        existente.setPartidosGanados(datos.getPartidosGanados());
        existente.setPartidosEmpatados(datos.getPartidosEmpatados());
        existente.setPartidosPerdidos(datos.getPartidosPerdidos());
        existente.setGolesFavor(datos.getGolesFavor());
        existente.setGolesContra(datos.getGolesContra());
        existente.setPuntos(datos.getPuntos());
        return tablaPosicionesRepository.save(existente);
    }

    public void eliminar(Long id) {
        TablaPosiciones existente = buscarPorId(id);
        tablaPosicionesRepository.delete(existente);
    }

    /**
     * Recalcula y persiste la tabla de posiciones de un campeonato a partir
     * de sus partidos en estado "Finalizado". No borra el historico: si un
     * equipo ya tiene fila para ese campeonato, la actualiza; si no, la crea.
     */
    public List<TablaPosiciones> recalcular(Long campeonatoId) {
        Campeonato campeonato = campeonatoService.buscarPorId(campeonatoId);
        List<Partido> finalizados = partidoService.listarFinalizadosPorCampeonato(campeonatoId);

        for (Partido partido : finalizados) {
            aplicarResultado(campeonato, partido.getEquipoLocal().getId(), partido.getGolesLocal(), partido.getGolesVisitante());
            aplicarResultado(campeonato, partido.getEquipoVisitante().getId(), partido.getGolesVisitante(), partido.getGolesLocal());
        }
        return listarPorCampeonato(campeonatoId);
    }

    private void aplicarResultado(Campeonato campeonato, Long equipoId, int golesPropios, int golesRival) {
        Optional<TablaPosiciones> existenteOpt = tablaPosicionesRepository.findByEquipoIdAndCampeonatoId(equipoId, campeonato.getId());
        TablaPosiciones registro = existenteOpt.orElseGet(() -> {
            TablaPosiciones nuevo = new TablaPosiciones();
            nuevo.setEquipo(equipoService.buscarPorId(equipoId));
            nuevo.setCampeonato(campeonato);
            return nuevo;
        });

        registro.setPartidosJugados(registro.getPartidosJugados() + 1);
        registro.setGolesFavor(registro.getGolesFavor() + golesPropios);
        registro.setGolesContra(registro.getGolesContra() + golesRival);

        if (golesPropios > golesRival) {
            registro.setPartidosGanados(registro.getPartidosGanados() + 1);
            registro.setPuntos(registro.getPuntos() + 3);
        } else if (golesPropios == golesRival) {
            registro.setPartidosEmpatados(registro.getPartidosEmpatados() + 1);
            registro.setPuntos(registro.getPuntos() + 1);
        } else {
            registro.setPartidosPerdidos(registro.getPartidosPerdidos() + 1);
        }

        tablaPosicionesRepository.save(registro);
    }
}

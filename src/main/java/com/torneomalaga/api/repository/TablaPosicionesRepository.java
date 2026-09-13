package com.torneomalaga.api.repository;

import com.torneomalaga.api.model.TablaPosiciones;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TablaPosicionesRepository extends JpaRepository<TablaPosiciones, Long> {
    List<TablaPosiciones> findByCampeonatoIdOrderByPuntosDesc(Long campeonatoId);
    List<TablaPosiciones> findAllByOrderByPuntosDesc();
    Optional<TablaPosiciones> findByEquipoIdAndCampeonatoId(Long equipoId, Long campeonatoId);
}

package com.torneomalaga.api.repository;

import com.torneomalaga.api.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartidoRepository extends JpaRepository<Partido, Long> {
    List<Partido> findByEquipoLocalIdOrEquipoVisitanteId(Long equipoLocalId, Long equipoVisitanteId);
    List<Partido> findByCampeonatoIdAndEstadoIgnoreCase(Long campeonatoId, String estado);
}

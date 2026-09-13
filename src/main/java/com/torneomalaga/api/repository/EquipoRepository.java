package com.torneomalaga.api.repository;

import com.torneomalaga.api.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    List<Equipo> findByCampeonatoId(Long campeonatoId);
}

package com.torneomalaga.api.repository;

import com.torneomalaga.api.model.Sugerencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SugerenciaRepository extends JpaRepository<Sugerencia, Long> {
    List<Sugerencia> findByCampeonatoId(Long campeonatoId);
}

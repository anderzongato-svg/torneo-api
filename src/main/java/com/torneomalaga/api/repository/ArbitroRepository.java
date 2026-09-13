package com.torneomalaga.api.repository;

import com.torneomalaga.api.model.Arbitro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArbitroRepository extends JpaRepository<Arbitro, Long> {
    List<Arbitro> findByCampeonatoId(Long campeonatoId);
}

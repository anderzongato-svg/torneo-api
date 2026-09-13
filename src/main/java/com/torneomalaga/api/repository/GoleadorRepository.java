package com.torneomalaga.api.repository;

import com.torneomalaga.api.model.Goleador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoleadorRepository extends JpaRepository<Goleador, Long> {

    List<Goleador> findAllByOrderByGolesDesc();

    // goleador ya no se relaciona directo con campeonato: se llega a el
    // a traves de partido.campeonato (cada goleador es el goles de un
    // jugador en un partido especifico de ese campeonato).
    List<Goleador> findByPartido_Campeonato_IdOrderByGolesDesc(Long campeonatoId);

    List<Goleador> findByPartidoId(Long partidoId);
}

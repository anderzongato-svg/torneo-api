package com.torneomalaga.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * Estructura confirmada contra la base de datos real: PK id_goleador.
 * IMPORTANTE: goleador se relaciona con "partido" (id_partido), NO con
 * "campeonato" directamente como se asumio antes -- es decir, cada fila
 * representa los goles de un jugador EN un partido especifico, no un
 * acumulado por campeonato. "totalGoles" en realidad es la columna "goles".
 */
@Entity
@Table(name = "goleador")
public class Goleador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_goleador")
    private Long id;

    @NotNull(message = "El jugador es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jugador", nullable = false)
    private Jugador jugador;

    @NotNull(message = "El partido es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_partido", nullable = false)
    private Partido partido;

    private Integer goles = 1;

    public Goleador() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Jugador getJugador() { return jugador; }
    public void setJugador(Jugador jugador) { this.jugador = jugador; }
    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
    public Integer getGoles() { return goles; }
    public void setGoles(Integer goles) { this.goles = goles; }
}

package com.torneomalaga.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * Estructura confirmada contra la base de datos real: PK id_posicion.
 * "ganados"/"empatados"/"perdidos" en realidad se llaman
 * "partidos_ganados"/"partidos_empatados"/"partidos_perdidos". Tanto
 * campeonato como equipo son obligatorios.
 */
@Entity
@Table(name = "tabla_posiciones")
public class TablaPosiciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_posicion")
    private Long id;

    @NotNull(message = "El campeonato es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campeonato", nullable = false)
    private Campeonato campeonato;

    @NotNull(message = "El equipo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false)
    private Equipo equipo;

    private Integer puntos = 0;

    @Column(name = "partidos_jugados")
    private Integer partidosJugados = 0;

    @Column(name = "partidos_ganados")
    private Integer partidosGanados = 0;

    @Column(name = "partidos_empatados")
    private Integer partidosEmpatados = 0;

    @Column(name = "partidos_perdidos")
    private Integer partidosPerdidos = 0;

    @Column(name = "goles_favor")
    private Integer golesFavor = 0;

    @Column(name = "goles_contra")
    private Integer golesContra = 0;

    public TablaPosiciones() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Campeonato getCampeonato() { return campeonato; }
    public void setCampeonato(Campeonato campeonato) { this.campeonato = campeonato; }
    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }
    public Integer getPuntos() { return puntos; }
    public void setPuntos(Integer puntos) { this.puntos = puntos; }
    public Integer getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(Integer partidosJugados) { this.partidosJugados = partidosJugados; }
    public Integer getPartidosGanados() { return partidosGanados; }
    public void setPartidosGanados(Integer partidosGanados) { this.partidosGanados = partidosGanados; }
    public Integer getPartidosEmpatados() { return partidosEmpatados; }
    public void setPartidosEmpatados(Integer partidosEmpatados) { this.partidosEmpatados = partidosEmpatados; }
    public Integer getPartidosPerdidos() { return partidosPerdidos; }
    public void setPartidosPerdidos(Integer partidosPerdidos) { this.partidosPerdidos = partidosPerdidos; }
    public Integer getGolesFavor() { return golesFavor; }
    public void setGolesFavor(Integer golesFavor) { this.golesFavor = golesFavor; }
    public Integer getGolesContra() { return golesContra; }
    public void setGolesContra(Integer golesContra) { this.golesContra = golesContra; }
}

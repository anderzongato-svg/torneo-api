package com.torneomalaga.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Estructura confirmada contra la base de datos real: PK id_equipo,
 * relacion obligatoria con campeonato (id_campeonato), "municipio" en
 * realidad se llama "ciudad", y existe un campo "dt" (director tecnico)
 * que no estaba contemplado antes.
 */
@Entity
@Table(name = "equipo")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Long id;

    @NotNull(message = "El campeonato es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campeonato", nullable = false)
    private Campeonato campeonato;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 80)
    private String ciudad;

    @Size(max = 100)
    private String dt;

    @Size(max = 50)
    private String colores;

    @Size(max = 100)
    private String estadio;

    public Equipo() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Campeonato getCampeonato() { return campeonato; }
    public void setCampeonato(Campeonato campeonato) { this.campeonato = campeonato; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getDt() { return dt; }
    public void setDt(String dt) { this.dt = dt; }
    public String getColores() { return colores; }
    public void setColores(String colores) { this.colores = colores; }
    public String getEstadio() { return estadio; }
    public void setEstadio(String estadio) { this.estadio = estadio; }
}

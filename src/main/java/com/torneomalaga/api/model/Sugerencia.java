package com.torneomalaga.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Estructura confirmada contra la base de datos real: PK id_sugerencia.
 * Tiene relacion obligatoria con campeonato (id_campeonato), que no
 * existia antes. "nombre" en realidad es "nombre_usuario" (opcional).
 * "mensaje" es TEXT (sin limite de 500 caracteres). "fecha" en realidad
 * es "fecha_envio", tipo DATETIME con valor por defecto en la BD.
 */
@Entity
@Table(name = "sugerencia")
public class Sugerencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sugerencia")
    private Long id;

    @NotNull(message = "El campeonato es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_campeonato", nullable = false)
    private Campeonato campeonato;

    @Size(max = 100)
    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    @NotBlank(message = "El mensaje de la sugerencia es obligatorio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    public Sugerencia() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Campeonato getCampeonato() { return campeonato; }
    public void setCampeonato(Campeonato campeonato) { this.campeonato = campeonato; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}

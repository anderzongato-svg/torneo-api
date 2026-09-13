package com.torneomalaga.api.controller;

import com.torneomalaga.api.model.Jugador;
import com.torneomalaga.api.service.JugadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Servicio REST para la gestion de jugadores.
 * Base: /api/jugadores
 */
@RestController
@RequestMapping("/api/jugadores")
@Tag(name = "Jugadores", description = "Gestion de los jugadores de cada equipo")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @Operation(summary = "Listar todos los jugadores, o filtrar por equipo con ?equipoId=")
    @GetMapping
    public List<Jugador> listar(@RequestParam(required = false) Long equipoId) {
        return equipoId != null ? jugadorService.listarPorEquipo(equipoId) : jugadorService.listarTodos();
    }

    @Operation(summary = "Consultar un jugador por su id")
    @GetMapping("/{id}")
    public Jugador obtener(@PathVariable Long id) {
        return jugadorService.buscarPorId(id);
    }

    @Operation(summary = "Registrar un nuevo jugador en un equipo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Jugador crear(@Valid @RequestBody Jugador jugador) {
        return jugadorService.crear(jugador);
    }

    @Operation(summary = "Actualizar los datos de un jugador")
    @PutMapping("/{id}")
    public Jugador actualizar(@PathVariable Long id, @Valid @RequestBody Jugador jugador) {
        return jugadorService.actualizar(id, jugador);
    }

    @Operation(summary = "Eliminar un jugador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        jugadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

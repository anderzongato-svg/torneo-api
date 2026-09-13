package com.torneomalaga.api.controller;

import com.torneomalaga.api.model.Goleador;
import com.torneomalaga.api.service.GoleadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Servicio REST para la tabla de goleadores (acumulado de goles por jugador).
 * Base: /api/goleadores
 */
@RestController
@RequestMapping("/api/goleadores")
@Tag(name = "Goleadores", description = "Acumulado de goles por jugador en cada campeonato")
public class GoleadorController {

    private final GoleadorService goleadorService;

    public GoleadorController(GoleadorService goleadorService) {
        this.goleadorService = goleadorService;
    }

    @Operation(summary = "Listar goleadores ordenados de mayor a menor, o filtrar por campeonato con ?campeonatoId=")
    @GetMapping
    public List<Goleador> listar(@RequestParam(required = false) Long campeonatoId) {
        return campeonatoId != null ? goleadorService.listarPorCampeonato(campeonatoId) : goleadorService.listarTodos();
    }

    @Operation(summary = "Consultar un registro de goleador por su id")
    @GetMapping("/{id}")
    public Goleador obtener(@PathVariable Long id) {
        return goleadorService.buscarPorId(id);
    }

    @Operation(summary = "Registrar el acumulado de goles de un jugador")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Goleador crear(@Valid @RequestBody Goleador goleador) {
        return goleadorService.crear(goleador);
    }

    @Operation(summary = "Actualizar el total de goles de un jugador")
    @PutMapping("/{id}")
    public Goleador actualizar(@PathVariable Long id, @Valid @RequestBody Goleador goleador) {
        return goleadorService.actualizar(id, goleador);
    }

    @Operation(summary = "Eliminar un registro de goleador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        goleadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.torneomalaga.api.controller;

import com.torneomalaga.api.model.Campeonato;
import com.torneomalaga.api.service.CampeonatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Servicio REST para la gestion de campeonatos (ediciones del torneo).
 * Base: /api/campeonatos
 */
@RestController
@RequestMapping("/api/campeonatos")
@Tag(name = "Campeonatos", description = "Gestion de las ediciones/campeonatos del Torneo Primera Malaga")
public class CampeonatoController {

    private final CampeonatoService campeonatoService;

    public CampeonatoController(CampeonatoService campeonatoService) {
        this.campeonatoService = campeonatoService;
    }

    @Operation(summary = "Listar todos los campeonatos")
    @GetMapping
    public List<Campeonato> listar() {
        return campeonatoService.listarTodos();
    }

    @Operation(summary = "Consultar un campeonato por su id")
    @GetMapping("/{id}")
    public Campeonato obtener(@PathVariable Long id) {
        return campeonatoService.buscarPorId(id);
    }

    @Operation(summary = "Registrar un nuevo campeonato")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Campeonato crear(@Valid @RequestBody Campeonato campeonato) {
        return campeonatoService.crear(campeonato);
    }

    @Operation(summary = "Actualizar los datos de un campeonato")
    @PutMapping("/{id}")
    public Campeonato actualizar(@PathVariable Long id, @Valid @RequestBody Campeonato campeonato) {
        return campeonatoService.actualizar(id, campeonato);
    }

    @Operation(summary = "Eliminar un campeonato")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        campeonatoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

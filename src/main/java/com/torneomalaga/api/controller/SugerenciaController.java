package com.torneomalaga.api.controller;

import com.torneomalaga.api.model.Sugerencia;
import com.torneomalaga.api.service.SugerenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Servicio REST para las sugerencias del publico/hinchada sobre el torneo.
 * Base: /api/sugerencias
 */
@RestController
@RequestMapping("/api/sugerencias")
@Tag(name = "Sugerencias", description = "Buzon de sugerencias del publico sobre el Torneo Primera Malaga")
public class SugerenciaController {

    private final SugerenciaService sugerenciaService;

    public SugerenciaController(SugerenciaService sugerenciaService) {
        this.sugerenciaService = sugerenciaService;
    }

    @Operation(summary = "Listar todas las sugerencias, o filtrar por campeonato con ?campeonatoId=")
    @GetMapping
    public List<Sugerencia> listar(@RequestParam(required = false) Long campeonatoId) {
        return campeonatoId != null ? sugerenciaService.listarPorCampeonato(campeonatoId) : sugerenciaService.listarTodas();
    }

    @Operation(summary = "Consultar una sugerencia por su id")
    @GetMapping("/{id}")
    public Sugerencia obtener(@PathVariable Long id) {
        return sugerenciaService.buscarPorId(id);
    }

    @Operation(summary = "Registrar una nueva sugerencia")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sugerencia crear(@Valid @RequestBody Sugerencia sugerencia) {
        return sugerenciaService.crear(sugerencia);
    }

    @Operation(summary = "Eliminar una sugerencia")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sugerenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

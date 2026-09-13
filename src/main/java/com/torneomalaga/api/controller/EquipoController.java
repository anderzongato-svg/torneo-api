package com.torneomalaga.api.controller;

import com.torneomalaga.api.model.Equipo;
import com.torneomalaga.api.service.EquipoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Servicio REST para la gestion de equipos del torneo.
 * Base: /api/equipos
 */
@RestController
@RequestMapping("/api/equipos")
@Tag(name = "Equipos", description = "Gestion de los equipos participantes en el Torneo Primera Malaga")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @Operation(summary = "Listar todos los equipos, o filtrar por campeonato con ?campeonatoId=")
    @GetMapping
    public List<Equipo> listar(@RequestParam(required = false) Long campeonatoId) {
        return campeonatoId != null ? equipoService.listarPorCampeonato(campeonatoId) : equipoService.listarTodos();
    }

    @Operation(summary = "Consultar un equipo por su id")
    @GetMapping("/{id}")
    public Equipo obtener(@PathVariable Long id) {
        return equipoService.buscarPorId(id);
    }

    @Operation(summary = "Registrar un nuevo equipo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Equipo crear(@Valid @RequestBody Equipo equipo) {
        return equipoService.crear(equipo);
    }

    @Operation(summary = "Actualizar los datos de un equipo existente")
    @PutMapping("/{id}")
    public Equipo actualizar(@PathVariable Long id, @Valid @RequestBody Equipo equipo) {
        return equipoService.actualizar(id, equipo);
    }

    @Operation(summary = "Eliminar un equipo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        equipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

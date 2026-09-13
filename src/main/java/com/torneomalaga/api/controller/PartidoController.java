package com.torneomalaga.api.controller;

import com.torneomalaga.api.model.Partido;
import com.torneomalaga.api.service.PartidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Servicio REST para la gestion de partidos del torneo.
 * Base: /api/partidos
 */
@RestController
@RequestMapping("/api/partidos")
@Tag(name = "Partidos", description = "Programacion y resultados de los partidos del torneo")
public class PartidoController {

    private final PartidoService partidoService;

    public PartidoController(PartidoService partidoService) {
        this.partidoService = partidoService;
    }

    @Operation(summary = "Listar todos los partidos, o filtrar por equipo con ?equipoId=")
    @GetMapping
    public List<Partido> listar(@RequestParam(required = false) Long equipoId) {
        return equipoId != null ? partidoService.listarPorEquipo(equipoId) : partidoService.listarTodos();
    }

    @Operation(summary = "Consultar un partido por su id")
    @GetMapping("/{id}")
    public Partido obtener(@PathVariable Long id) {
        return partidoService.buscarPorId(id);
    }

    @Operation(summary = "Programar un nuevo partido")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Partido crear(@Valid @RequestBody Partido partido) {
        return partidoService.crear(partido);
    }

    @Operation(summary = "Actualizar un partido (fecha, equipos, arbitro o marcador)")
    @PutMapping("/{id}")
    public Partido actualizar(@PathVariable Long id, @Valid @RequestBody Partido partido) {
        return partidoService.actualizar(id, partido);
    }

    @Operation(summary = "Eliminar un partido")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        partidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

package com.torneomalaga.api.controller;

import com.torneomalaga.api.model.Arbitro;
import com.torneomalaga.api.service.ArbitroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Servicio REST para la gestion de arbitros.
 * Base: /api/arbitros
 */
@RestController
@RequestMapping("/api/arbitros")
@Tag(name = "Arbitros", description = "Gestion de los arbitros habilitados para dirigir partidos")
public class ArbitroController {

    private final ArbitroService arbitroService;

    public ArbitroController(ArbitroService arbitroService) {
        this.arbitroService = arbitroService;
    }

    @Operation(summary = "Listar todos los arbitros, o filtrar por campeonato con ?campeonatoId=")
    @GetMapping
    public List<Arbitro> listar(@RequestParam(required = false) Long campeonatoId) {
        return campeonatoId != null ? arbitroService.listarPorCampeonato(campeonatoId) : arbitroService.listarTodos();
    }

    @Operation(summary = "Consultar un arbitro por su id")
    @GetMapping("/{id}")
    public Arbitro obtener(@PathVariable Long id) {
        return arbitroService.buscarPorId(id);
    }

    @Operation(summary = "Registrar un nuevo arbitro")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Arbitro crear(@Valid @RequestBody Arbitro arbitro) {
        return arbitroService.crear(arbitro);
    }

    @Operation(summary = "Actualizar los datos de un arbitro")
    @PutMapping("/{id}")
    public Arbitro actualizar(@PathVariable Long id, @Valid @RequestBody Arbitro arbitro) {
        return arbitroService.actualizar(id, arbitro);
    }

    @Operation(summary = "Eliminar un arbitro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        arbitroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

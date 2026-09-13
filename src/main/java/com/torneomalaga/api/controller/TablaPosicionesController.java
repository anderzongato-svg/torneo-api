package com.torneomalaga.api.controller;

import com.torneomalaga.api.model.TablaPosiciones;
import com.torneomalaga.api.service.TablaPosicionesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Servicio REST para la tabla de posiciones (esta persistida en su propia
 * tabla, tal como en el esquema real; no se calcula solo en memoria).
 * Base: /api/tabla-posiciones
 */
@RestController
@RequestMapping("/api/tabla-posiciones")
@Tag(name = "Tabla de posiciones", description = "Clasificacion del torneo, persistida por equipo y campeonato")
public class TablaPosicionesController {

    private final TablaPosicionesService tablaPosicionesService;

    public TablaPosicionesController(TablaPosicionesService tablaPosicionesService) {
        this.tablaPosicionesService = tablaPosicionesService;
    }

    @Operation(summary = "Listar la tabla de posiciones completa, o filtrar por campeonato con ?campeonatoId=")
    @GetMapping
    public List<TablaPosiciones> listar(@RequestParam(required = false) Long campeonatoId) {
        return campeonatoId != null ? tablaPosicionesService.listarPorCampeonato(campeonatoId) : tablaPosicionesService.listarTodos();
    }

    @Operation(summary = "Consultar un registro de la tabla de posiciones por su id")
    @GetMapping("/{id}")
    public TablaPosiciones obtener(@PathVariable Long id) {
        return tablaPosicionesService.buscarPorId(id);
    }

    @Operation(summary = "Registrar manualmente una fila de la tabla de posiciones")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TablaPosiciones crear(@Valid @RequestBody TablaPosiciones registro) {
        return tablaPosicionesService.crear(registro);
    }

    @Operation(summary = "Actualizar manualmente una fila de la tabla de posiciones")
    @PutMapping("/{id}")
    public TablaPosiciones actualizar(@PathVariable Long id, @Valid @RequestBody TablaPosiciones registro) {
        return tablaPosicionesService.actualizar(id, registro);
    }

    @Operation(summary = "Eliminar una fila de la tabla de posiciones")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tablaPosicionesService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Recalcular automaticamente la tabla de posiciones de un campeonato a partir de sus partidos finalizados")
    @PostMapping("/recalcular/{campeonatoId}")
    public List<TablaPosiciones> recalcular(@PathVariable Long campeonatoId) {
        return tablaPosicionesService.recalcular(campeonatoId);
    }
}

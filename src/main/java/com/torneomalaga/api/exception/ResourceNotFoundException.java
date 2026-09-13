package com.torneomalaga.api.exception;

/**
 * Se lanza cuando un servicio solicita un recurso (equipo, jugador,
 * arbitro, partido, gol o tarjeta) que no existe en la base de datos.
 * El manejador global la traduce a una respuesta HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entidad, Long id) {
        super(entidad + " con id " + id + " no fue encontrado(a)");
    }
}

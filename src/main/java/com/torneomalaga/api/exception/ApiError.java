package com.torneomalaga.api.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estructura estandar de error devuelta por todos los servicios de la API,
 * para que el cliente (frontend, Postman, otro sistema) reciba siempre
 * el mismo formato de respuesta ante un fallo.
 */
public class ApiError {
    private LocalDateTime fecha = LocalDateTime.now();
    private int codigo;
    private String mensaje;
    private List<String> detalles;

    public ApiError(int codigo, String mensaje, List<String> detalles) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.detalles = detalles;
    }

    public LocalDateTime getFecha() { return fecha; }
    public int getCodigo() { return codigo; }
    public String getMensaje() { return mensaje; }
    public List<String> getDetalles() { return detalles; }
}

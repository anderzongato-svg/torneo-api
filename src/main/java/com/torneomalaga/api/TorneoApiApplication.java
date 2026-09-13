package com.torneomalaga.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API REST del Sistema de Gestion
 * del Torneo Primera Malaga (Malaga, Santander).
 *
 * Proyecto formativo SENA - Tecnologia en Analisis y Desarrollo de Software (ADSO).
 * Modulo: Diseno y codificacion de servicios API.
 */
@SpringBootApplication
public class TorneoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TorneoApiApplication.class, args);
    }
}
